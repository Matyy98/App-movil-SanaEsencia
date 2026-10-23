package cl.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.sanaesencia.app.model.Rol

// Tabla "usuarios" de la base de datos (Room). Guarda los pacientes que se registran.
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    // Número de fila: Room lo pone solo (1, 2, 3...)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String,
    val telefono: String,
    val rol: Rol,
    val terapeutaId: Int = 0
)