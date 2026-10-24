package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.data.SesionDataStore
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.RegistroUiState
import cl.sanaesencia.app.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel del registro en 2 pasos:
// paso 1 = nombre, correo y teléfono; paso 2 = contraseña y términos
class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(
        AppDatabase.obtener(application).usuarioDao(),
        SesionDataStore(application)
    )

    // Lo que se escribe en el registro
    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado

    // Cada campo: guarda lo escrito y borra su error (patrón guía 11)
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errorGeneral = null, errores = it.errores.copy(correo = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onConfirmarClaveChange(valor: String) {
        _estado.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }
    }

    // El botón de términos se marca y desmarca con cada toque
    fun onTerminosChange() {
        _estado.update { it.copy(aceptaTerminos = !it.aceptaTerminos, errores = it.errores.copy(terminos = null)) }
    }

    // Revisa solo los campos del paso 1
    fun validarPaso1(): Boolean {
        val actual = _estado.value
        val errores = actual.errores.copy(
            nombre = Validaciones.nombre(actual.nombre),
            correo = Validaciones.correo(actual.correo),
            telefono = Validaciones.telefono(actual.telefono)
        )
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.nombre, errores.correo, errores.telefono).isEmpty()
    }

    // Revisa solo los campos del paso 2
    fun validarPaso2(): Boolean {
        val actual = _estado.value
        val errores = actual.errores.copy(
            clave = Validaciones.clave(actual.clave),
            confirmarClave = Validaciones.confirmarClave(actual.clave, actual.confirmarClave),
            terminos = Validaciones.terminos(actual.aceptaTerminos)
        )
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.clave, errores.confirmarClave, errores.terminos).isEmpty()
    }

    // Pasa al paso 2 solo si el paso 1 está bien
    fun siguientePaso() {
        if (validarPaso1()) {
            _estado.update { it.copy(paso = 2) }
        }
    }

    // Vuelve al paso 1 sin borrar lo escrito
    fun pasoAnterior() {
        _estado.update { it.copy(paso = 1, errorGeneral = null) }
    }

    // Si el paso 2 está bien, guarda el paciente en Room
    fun registrar() {
        if (!validarPaso2()) {
            return
        }
        viewModelScope.launch {
            val actual = _estado.value
            val creado = repositorio.registrar(actual.nombre, actual.correo, actual.telefono, actual.clave)
            if (creado) {
                _estado.update { it.copy(registroExitoso = true, errorGeneral = null) }
            } else {
                // El correo ya existe: vuelve al paso 1 y marca el campo correo
                _estado.update {
                    it.copy(
                        paso = 1,
                        errorGeneral = "Ya existe una cuenta con ese correo",
                        errores = it.errores.copy(correo = "Usa otro correo")
                    )
                }
            }
        }
    }

    // Deja el formulario vacío (al salir de la pantalla de registro)
    fun limpiar() {
        _estado.value = RegistroUiState()
    }
}