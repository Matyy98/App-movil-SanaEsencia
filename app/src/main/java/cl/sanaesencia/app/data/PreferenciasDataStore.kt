package cl.sanaesencia.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Archivo "preferencias" donde se guardan los datos del perfil (guía 12).
// Tiene otro nombre que el DataStore de sesión para no chocar con él.
val Context.preferenciasDataStore by preferencesDataStore(name = "preferencias")

class PreferenciasDataStore(private val context: Context) {

    // Claves: el nombre con que se guarda cada dato
    private val RECORDATORIOS = booleanPreferencesKey("recordatorios")
    private val FOTO_PERFIL = stringPreferencesKey("foto_perfil")

    // Guarda si el usuario quiere recordatorios de sus citas
    suspend fun guardarRecordatorios(activo: Boolean) {
        context.preferenciasDataStore.edit { preferencias ->
            preferencias[RECORDATORIOS] = activo
        }
    }

    // Lee la preferencia; si nunca se guardó, parte en false
    fun obtenerRecordatorios(): Flow<Boolean> {
        return context.preferenciasDataStore.data.map { preferencias ->
            preferencias[RECORDATORIOS] ?: false
        }
    }

    // Guarda la ruta de la foto como texto (Uri convertido con toString)
    suspend fun guardarFoto(ruta: String) {
        context.preferenciasDataStore.edit { preferencias ->
            preferencias[FOTO_PERFIL] = ruta
        }
    }

    // Lee la ruta de la foto; null si todavía no hay foto
    fun obtenerFoto(): Flow<String?> {
        return context.preferenciasDataStore.data.map { preferencias ->
            preferencias[FOTO_PERFIL]
        }
    }
}