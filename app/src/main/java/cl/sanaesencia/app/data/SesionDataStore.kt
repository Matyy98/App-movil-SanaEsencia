package cl.sanaesencia.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Archivo "sesion" donde se guarda quién inició sesión (guía 12).
// Tiene otro nombre que el de preferencias del perfil para no chocar.
val Context.sesionDataStore by preferencesDataStore(name = "sesion")

class SesionDataStore(private val context: Context) {

    // Clave: el nombre con que se guarda el correo del usuario conectado
    private val CORREO_SESION = stringPreferencesKey("correo_sesion")

    // Guarda el correo al iniciar sesión
    suspend fun guardarSesion(correo: String) {
        context.sesionDataStore.edit { preferencias ->
            preferencias[CORREO_SESION] = correo
        }
    }

    // Lee el correo guardado; "" si nadie ha iniciado sesión
    fun obtenerSesion(): Flow<String> {
        return context.sesionDataStore.data.map { preferencias ->
            preferencias[CORREO_SESION] ?: ""
        }
    }

    // Al cerrar sesión se deja el correo vacío
    suspend fun cerrarSesion() {
        context.sesionDataStore.edit { preferencias ->
            preferencias[CORREO_SESION] = ""
        }
    }
}