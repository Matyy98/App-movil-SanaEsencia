package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.logic.GestorBoxes
import cl.sanaesencia.app.logic.MotorDesercion
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.AlertaDesercion
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.NivelRiesgo
import cl.sanaesencia.app.model.OcupacionBox
import cl.sanaesencia.app.model.Promocion
import cl.sanaesencia.app.model.PromocionUiState
import cl.sanaesencia.app.repository.CitaRepository
import cl.sanaesencia.app.repository.HistorialDemo
import cl.sanaesencia.app.repository.PromocionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// ViewModel de Administración: ocupación de boxes, alertas de deserción y promociones.
// Es AndroidViewModel porque necesita el Context para abrir la base de datos.
class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PromocionRepository(AppDatabase.obtener(application).promocionDao())
    private val repositorioCitas = CitaRepository(AppDatabase.obtener(application).citaDao())

    // Promociones guardadas (se actualiza sola cuando se agrega una)
    private val _promociones = MutableStateFlow<List<Promocion>>(emptyList())
    val promociones: StateFlow<List<Promocion>> = _promociones

    // Lo que se escribe en el formulario
    private val _formulario = MutableStateFlow(PromocionUiState())
    val formulario: StateFlow<PromocionUiState> = _formulario

    // ---------- Nuevo: citas, día del panel y filtro de alertas ----------

    // Citas guardadas en Room
    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    // Día que se está mirando en el panel (parte en hoy)
    private val _dia = MutableStateFlow(LocalDate.now())
    val dia: StateFlow<LocalDate> = _dia

    // Nivel elegido en Alertas (null = todos)
    private val _filtroNivel = MutableStateFlow<NivelRiesgo?>(null)
    val filtroNivel: StateFlow<NivelRiesgo?> = _filtroNivel

    // Escucha las tablas de promociones y citas desde que se crea el ViewModel
    init {
        viewModelScope.launch {
            repositorio.obtenerPromociones().collectLatest { lista ->
                _promociones.value = lista
            }
        }
        viewModelScope.launch {
            repositorioCitas.obtenerCitas().collectLatest { lista ->
                _citas.value = lista
            }
        }
    }

    // ---------- Panel: ocupación de los 8 boxes ----------

    // cambio = 1 (día siguiente) o -1 (día anterior)
    fun cambiarDia(cambio: Int) {
        _dia.update { it.plusDays(cambio.toLong()) }
    }

    fun esHoy(dia: LocalDate): Boolean = dia == LocalDate.now()

    // El centro no atiende los domingos
    fun esDiaHabil(dia: LocalDate): Boolean = Fechas.esDiaHabil(dia)

    // Ocupación de cada box en el día (citas de Room + historial ficticio)
    fun ocupacion(citas: List<Cita>, dia: LocalDate): List<OcupacionBox> =
        GestorBoxes.ocupacionPorBox(HistorialDemo.juntarCon(citas), Fechas.formatear(dia))

    fun promedio(ocupaciones: List<OcupacionBox>): Int = GestorBoxes.promedioOcupacion(ocupaciones)

    fun esBajaDemanda(promedio: Int): Boolean = GestorBoxes.esBajaDemanda(promedio)

    // ---------- Alertas de deserción ----------

    // Todos los pacientes del centro con su riesgo, los de más riesgo primero
    fun alertas(citas: List<Cita>): List<AlertaDesercion> =
        MotorDesercion.alertas(HistorialDemo.juntarCon(citas))

    // Solo los del nivel elegido (null = todos)
    fun alertasFiltradas(citas: List<Cita>, nivel: NivelRiesgo?): List<AlertaDesercion> {
        val todas = alertas(citas)
        return if (nivel == null) todas else todas.filter { it.nivel == nivel }
    }

    fun contar(alertas: List<AlertaDesercion>, nivel: NivelRiesgo): Int = MotorDesercion.contar(alertas, nivel)

    fun elegirFiltro(nivel: NivelRiesgo?) {
        _filtroNivel.value = nivel
    }

    // ---------- Promociones (igual que antes) ----------

    // Cada campo: guarda lo escrito y borra su error (patrón guía 11)
    fun onTituloChange(valor: String) {
        _formulario.update { it.copy(titulo = valor, errores = it.errores.copy(titulo = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _formulario.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }

    fun onDescuentoChange(valor: String) {
        _formulario.update { it.copy(descuento = valor, errores = it.errores.copy(descuento = null)) }
    }

    fun onSegmentoChange(valor: String) {
        _formulario.update { it.copy(segmento = valor, errores = it.errores.copy(segmento = null)) }
    }

    // Abre el formulario limpio
    fun abrirFormulario() {
        _formulario.value = PromocionUiState(mostrarFormulario = true)
    }

    // Vuelve a la lista sin guardar
    fun cerrarFormulario() {
        _formulario.value = PromocionUiState()
    }

    // Revisa todos los campos con Validaciones. true = todo bien
    fun validarFormulario(): Boolean {
        val actual = _formulario.value
        val errores = actual.errores.copy(
            titulo = Validaciones.tituloPromocion(actual.titulo),
            descripcion = Validaciones.descripcionPromocion(actual.descripcion),
            descuento = Validaciones.descuento(actual.descuento),
            segmento = Validaciones.segmento(actual.segmento)
        )
        _formulario.update { it.copy(errores = errores) }
        return listOfNotNull(errores.titulo, errores.descripcion, errores.descuento, errores.segmento).isEmpty()
    }

    // Si todo está bien, guarda en Room y vuelve a la lista con mensaje de éxito
    fun guardarPromocion() {
        if (!validarFormulario()) return
        val actual = _formulario.value
        viewModelScope.launch {
            repositorio.agregar(
                titulo = actual.titulo.trim(),
                descripcion = actual.descripcion.trim(),
                // Ya se validó que es un número, el ?: 0 es solo por seguridad
                descuento = actual.descuento.trim().toIntOrNull() ?: 0,
                segmento = actual.segmento
            )
            _formulario.value = PromocionUiState(mensajeExito = "Promoción \"${actual.titulo.trim()}\" guardada")
        }
    }
}