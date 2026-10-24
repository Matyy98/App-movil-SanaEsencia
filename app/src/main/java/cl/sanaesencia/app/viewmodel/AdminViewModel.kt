package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.Promocion
import cl.sanaesencia.app.model.PromocionUiState
import cl.sanaesencia.app.repository.PromocionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel de Administración. Por ahora: promociones guardadas en Room.
// Es AndroidViewModel porque necesita el Context para abrir la base de datos.
class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PromocionRepository(AppDatabase.obtener(application).promocionDao())

    // Promociones guardadas (se actualiza sola cuando se agrega una)
    private val _promociones = MutableStateFlow<List<Promocion>>(emptyList())
    val promociones: StateFlow<List<Promocion>> = _promociones

    // Lo que se escribe en el formulario
    private val _formulario = MutableStateFlow(PromocionUiState())
    val formulario: StateFlow<PromocionUiState> = _formulario

    // Escucha la tabla de promociones desde que se crea el ViewModel
    init {
        viewModelScope.launch {
            repositorio.obtenerPromociones().collectLatest { lista ->
                _promociones.value = lista
            }
        }
    }

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