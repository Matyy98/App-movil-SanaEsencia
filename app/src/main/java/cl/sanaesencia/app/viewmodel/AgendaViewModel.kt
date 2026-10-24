package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.logic.GestorBoxes
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.AgendaErrores
import cl.sanaesencia.app.model.AgendaUiState
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.model.Terapeuta
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.repository.CatalogoRepository
import cl.sanaesencia.app.repository.CitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel del agendamiento: formulario de 4 pasos (guía 11) y lista de citas guardadas en Room (guía 12).
// Es AndroidViewModel porque necesita el Context para abrir la base de datos.
class AgendaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = CitaRepository(AppDatabase.obtener(application).citaDao())
    private val catalogo = CatalogoRepository()

    // Todas las citas guardadas (se actualiza sola cuando se agrega o cancela una)
    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    // Lo que se va escribiendo en el formulario
    private val _estado = MutableStateFlow(AgendaUiState())
    val estado: StateFlow<AgendaUiState> = _estado

    // Escucha la tabla de citas desde que se crea el ViewModel
    init {
        viewModelScope.launch {
            repositorio.obtenerCitas().collectLatest { lista ->
                _citas.value = lista
            }
        }
    }

    // ---------- Datos del catálogo para las pantallas ----------

    fun servicios(): List<Servicio> = catalogo.obtenerServicios()

    fun servicioElegido(): Servicio? = catalogo.buscarServicio(_estado.value.servicioId)

    fun terapeutaElegido(): Terapeuta? = catalogo.buscarTerapeuta(_estado.value.terapeutaId)

    fun nombreServicio(id: Int): String = catalogo.buscarServicio(id)?.nombre ?: "Servicio"

    fun nombreTerapeuta(id: Int): String = catalogo.buscarTerapeuta(id)?.nombre ?: "Profesional"

    // Profesionales del servicio elegido que tienen la agenda abierta
    fun profesionalesDisponibles(): List<Terapeuta> {
        val servicio = servicioElegido()
        return if (servicio == null) listOf() else catalogo.terapeutasDeServicio(servicio)
    }

    // Modalidades que acepta el servicio y el profesional elegidos
    fun modalidadesPosibles(): List<Modalidad> {
        val servicio = servicioElegido()
        val terapeuta = terapeutaElegido()
        return if (servicio == null || terapeuta == null) listOf() else GestorBoxes.modalidadesPosibles(servicio, terapeuta)
    }

    // Horas libres del día elegido: sin pasar del reloj, sin profesional ocupado y con box libre si es presencial.
    // Recibe las citas para que la pantalla se redibuje si cambian.
    fun horasLibres(citas: List<Cita>): List<String> {
        val e = _estado.value
        val dia = Fechas.parsear(e.fecha)
        val modalidad = e.modalidad
        if (dia == null || modalidad == null) return listOf()
        return Fechas.horasDelDia(dia).filter { hora ->
            GestorBoxes.horaDisponible(citas, e.terapeutaId, e.fecha, hora, modalidad)
        }
    }

    // Si hay una sola modalidad posible, se elige sola
    private fun modalidadUnica(servicioId: Int, terapeutaId: Int): Modalidad? {
        val servicio = catalogo.buscarServicio(servicioId)
        val terapeuta = catalogo.buscarTerapeuta(terapeutaId)
        if (servicio == null || terapeuta == null) return null
        val posibles = GestorBoxes.modalidadesPosibles(servicio, terapeuta)
        return if (posibles.size == 1) posibles[0] else null
    }

    // ---------- Cambios en el formulario (cada uno borra el error de su campo) ----------

    fun elegirServicio(id: Int) {
        val servicio = catalogo.buscarServicio(id)
        val profesionales: List<Terapeuta> = if (servicio == null) listOf() else catalogo.terapeutasDeServicio(servicio)
        // Si hay un solo profesional, queda elegido solo
        val unico = if (profesionales.size == 1) profesionales[0].id else 0
        _estado.update {
            it.copy(
                servicioId = id,
                terapeutaId = unico,
                modalidad = modalidadUnica(id, unico),
                fecha = "",
                hora = "",
                errores = AgendaErrores(),
                errorGeneral = null
            )
        }
    }

    // Desde la pantalla de servicios: parte de cero con el servicio ya elegido y salta al paso 2
    fun agendarServicio(id: Int) {
        _estado.value = AgendaUiState()
        elegirServicio(id)
        _estado.update { it.copy(paso = 2) }
    }

    fun elegirProfesional(id: Int) {
        _estado.update {
            it.copy(
                terapeutaId = id,
                modalidad = modalidadUnica(it.servicioId, id),
                hora = "",
                errores = it.errores.copy(profesional = null, modalidad = null),
                errorGeneral = null
            )
        }
    }

    fun elegirModalidad(modalidad: Modalidad) {
        // Cambiar la modalidad cambia las horas libres, por eso se borra la hora
        _estado.update { it.copy(modalidad = modalidad, hora = "", errores = it.errores.copy(modalidad = null)) }
    }

    fun elegirFecha(fecha: String) {
        _estado.update { it.copy(fecha = fecha, hora = "", errores = it.errores.copy(fecha = null)) }
    }

    fun elegirHora(hora: String) {
        _estado.update { it.copy(hora = hora, errores = it.errores.copy(hora = null), errorGeneral = null) }
    }

    fun onMotivoChange(valor: String) {
        _estado.update { it.copy(motivo = valor, errores = it.errores.copy(motivo = null)) }
    }

    // cambio = 1 (siguiente) o -1 (anterior)
    fun cambiarPaginaFechas(cambio: Int) {
        _estado.update { it.copy(paginaFechas = it.paginaFechas + cambio) }
    }

    // ---------- Pasos ----------

    // Revisa solo los campos del paso actual y deja los errores en el estado (patrón guía 11)
    fun validarPaso(): Boolean {
        val e = _estado.value
        val errores = when (e.paso) {
            1 -> AgendaErrores(servicio = Validaciones.servicioElegido(e.servicioId))
            2 -> AgendaErrores(
                profesional = Validaciones.profesionalElegido(e.terapeutaId),
                modalidad = Validaciones.modalidadElegida(e.modalidad),
                fecha = Validaciones.fechaCita(e.fecha)
            )
            3 -> AgendaErrores(
                hora = Validaciones.horaElegida(e.hora),
                motivo = Validaciones.motivoCita(e.motivo)
            )
            else -> AgendaErrores()
        }
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(
            errores.servicio, errores.profesional, errores.modalidad,
            errores.fecha, errores.hora, errores.motivo
        ).isEmpty()
    }

    fun siguiente() {
        if (validarPaso() && _estado.value.paso < 4) {
            _estado.update { it.copy(paso = it.paso + 1, errorGeneral = null) }
        }
    }

    fun anterior() {
        if (_estado.value.paso > 1) {
            _estado.update { it.copy(paso = it.paso - 1, errores = AgendaErrores(), errorGeneral = null) }
        }
    }

    // Paso 4: vuelve a revisar que la hora siga libre y guarda la cita en Room
    fun confirmar(usuario: Usuario) {
        val e = _estado.value
        val modalidad = e.modalidad
        if (modalidad == null) {
            _estado.update { it.copy(paso = 2, errorGeneral = "Elige una modalidad") }
            return
        }
        // Puede que otra persona haya tomado la hora mientras se llenaba el formulario
        if (!GestorBoxes.horaDisponible(_citas.value, e.terapeutaId, e.fecha, e.hora, modalidad)) {
            _estado.update { it.copy(paso = 3, hora = "", errorGeneral = "Esa hora ya no está disponible, elige otra") }
            return
        }
        val cita = Cita(
            id = 0,
            correoPaciente = usuario.correo,
            nombrePaciente = usuario.nombre,
            servicioId = e.servicioId,
            terapeutaId = e.terapeutaId,
            fecha = e.fecha,
            hora = e.hora,
            modalidad = modalidad,
            box = GestorBoxes.asignarBox(_citas.value, e.fecha, e.hora, modalidad),
            estado = EstadoCita.AGENDADA
        )
        viewModelScope.launch {
            repositorio.agregar(cita)
            _estado.update { it.copy(citaConfirmada = cita, errorGeneral = null) }
        }
    }

    // Deja el formulario en blanco para agendar otra hora
    fun reiniciar() {
        _estado.value = AgendaUiState()
    }

    // ---------- Mis citas ----------

    // Citas agendadas del paciente, la más cercana primero
    fun citasProximas(citas: List<Cita>, correo: String): List<Cita> {
        return citas
            .filter { it.correoPaciente == correo && it.estado == EstadoCita.AGENDADA }
            .sortedBy { Fechas.claveOrden(it) }
    }

    // Citas completadas o canceladas del paciente, la más reciente primero
    fun citasHistorial(citas: List<Cita>, correo: String): List<Cita> {
        return citas
            .filter { it.correoPaciente == correo && it.estado != EstadoCita.AGENDADA }
            .sortedBy { Fechas.claveOrden(it) }
            .reversed()
    }

    fun cancelarCita(id: Int) {
        viewModelScope.launch {
            repositorio.actualizarEstado(id, EstadoCita.CANCELADA)
        }
    }
}
