package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.data.SesionDataStore
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.LoginErrores
import cl.sanaesencia.app.model.LoginUiState
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel de la sesión: formulario de login y usuario conectado.
// Es AndroidViewModel porque necesita el Context para Room y DataStore.
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(
        AppDatabase.obtener(application).usuarioDao(),
        SesionDataStore(application)
    )

    // Usuario conectado (null = nadie ha iniciado sesión)
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    // true mientras se revisa si había una sesión guardada al abrir la app
    private val _revisandoSesion = MutableStateFlow(true)
    val revisandoSesion: StateFlow<Boolean> = _revisandoSesion

    // Lo que se escribe en el login
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado

    // Al crearse revisa si alguien quedó conectado la última vez
    init {
        viewModelScope.launch {
            _usuario.value = repositorio.usuarioEnSesion()
            _revisandoSesion.value = false
        }
    }

    // Cada campo: guarda lo escrito y borra su error (patrón guía 11)
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errorGeneral = null, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errorGeneral = null, errores = it.errores.copy(clave = null)) }
    }

    // Revisa los dos campos y deja los errores en el estado
    fun validarFormulario(): Boolean {
        val actual = _estado.value
        val errores = LoginErrores(
            correo = Validaciones.correo(actual.correo),
            clave = Validaciones.claveIngreso(actual.clave)
        )
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.correo, errores.clave).isEmpty()
    }

    // Si los campos están bien, busca el usuario y guarda la sesión
    fun iniciarSesion() {
        if (!validarFormulario()) {
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val encontrado = repositorio.validarCredenciales(_estado.value.correo, _estado.value.clave)
            if (encontrado == null) {
                _estado.update { it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos") }
            } else {
                repositorio.guardarSesion(encontrado.correo)
                _usuario.value = encontrado
                // Deja el formulario limpio para la próxima vez
                _estado.value = LoginUiState()
            }
        }
    }

    // Borra la sesión guardada y vuelve a dejar sin usuario
    fun cerrarSesion() {
        // Primero se desconecta en la app (así el login ya no lo ve conectado)
        _usuario.value = null
        viewModelScope.launch {
            repositorio.cerrarSesion()
        }
    }
}