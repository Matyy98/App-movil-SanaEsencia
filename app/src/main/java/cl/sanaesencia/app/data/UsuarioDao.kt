package cl.sanaesencia.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Acciones que se pueden hacer con la tabla de usuarios
@Dao
interface UsuarioDao {

    // Guardar un usuario nuevo (agrega una fila)
    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    // Buscar un usuario por su correo. Devuelve null si no existe.
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?
}