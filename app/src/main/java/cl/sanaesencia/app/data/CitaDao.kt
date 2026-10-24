package cl.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Acciones que se pueden hacer con la tabla de citas
@Dao
interface CitaDao {

    // Guarda una cita nueva
    @Insert
    suspend fun insertar(cita: CitaEntity)

    // Todas las citas. Es un Flow: si se agrega o cambia una, la lista se actualiza sola
    @Query("SELECT * FROM citas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<CitaEntity>>

    // Cambia el estado de una cita (por ejemplo a CANCELADA). El estado entra como texto (el name del enum)
    @Query("UPDATE citas SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)
}
