package cl.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Acciones que se pueden hacer con la tabla de promociones
@Dao
interface PromocionDao {

    // Guarda una promoción nueva
    @Insert
    suspend fun insertar(promocion: PromocionEntity)

    // Todas las promociones, la más nueva primero.
    // Es un Flow: si se agrega una, la lista se actualiza sola en la pantalla
    @Query("SELECT * FROM promociones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<PromocionEntity>>
}