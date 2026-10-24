package cl.sanaesencia.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.PreferenciasDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

// ViewModel del perfil: foto (cámara o galería) y preferencia de recordatorios (guías 12 y 13).
// Es AndroidViewModel porque necesita el Context para DataStore y para crear el archivo de la foto.
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencias = PreferenciasDataStore(application)

    // Foto actual. null = sin foto (se muestra el ícono por defecto)
    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri: StateFlow<Uri?> = _fotoUri

    // null mientras se lee DataStore (la pantalla muestra una ruedita de carga)
    private val _recordatorios = MutableStateFlow<Boolean?>(null)
    val recordatorios: StateFlow<Boolean?> = _recordatorios

    // Dirección que se le pasa a la cámara. Se guarda para usarla cuando vuelva con la foto
    private var uriCamara: Uri? = null

    // Al crear el ViewModel se cargan los datos guardados
    init {
        viewModelScope.launch {
            _recordatorios.value = preferencias.obtenerRecordatorios().first()
            val ruta = preferencias.obtenerFoto().first()
            if (ruta != null) {
                // La ruta se guardó como texto, aquí se vuelve a convertir en Uri
                _fotoUri.value = Uri.parse(ruta)
            }
        }
    }

    // Activa o desactiva los recordatorios y lo guarda
    fun cambiarRecordatorios() {
        val nuevoValor = !(_recordatorios.value ?: false)
        _recordatorios.value = nuevoValor
        viewModelScope.launch {
            preferencias.guardarRecordatorios(nuevoValor)
        }
    }

    // Crea el archivo vacío donde la cámara dejará la foto y devuelve su dirección segura
    fun crearUriCamara(): Uri {
        val contexto = getApplication<Application>()
        // Carpeta "fotos" dentro de la app (la misma de file_paths.xml)
        val carpeta = File(contexto.filesDir, "fotos")
        carpeta.mkdirs()
        // La hora hace que cada foto tenga un nombre distinto (se cambian los ":" por "-")
        val nombre = "perfil_" + LocalDateTime.now().toString().split(":").joinToString("-") + ".jpg"
        val archivo = File(carpeta, nombre)
        // Mismo nombre que "authorities" del Manifest
        val uri = FileProvider.getUriForFile(contexto, "cl.sanaesencia.app.fileprovider", archivo)
        uriCamara = uri
        return uri
    }

    // Respuesta de la cámara: true si se tomó la foto, false si el usuario canceló
    fun onFotoTomada(exito: Boolean) {
        val uri = uriCamara
        if (exito && uri != null) {
            guardarFoto(uri)
        }
    }

    // Respuesta de la galería: null si el usuario canceló
    fun onFotoElegida(uri: Uri?) {
        if (uri != null) {
            guardarFoto(uri)
        }
    }

    // Muestra la foto y guarda su ruta para la próxima vez que se abra la app
    private fun guardarFoto(uri: Uri) {
        _fotoUri.value = uri
        viewModelScope.launch {
            preferencias.guardarFoto(uri.toString())
        }
    }
}