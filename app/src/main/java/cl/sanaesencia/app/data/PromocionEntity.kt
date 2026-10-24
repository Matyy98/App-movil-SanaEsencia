package cl.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "promociones" de la base de datos (Room). Las crea Administración.
@Entity(tableName = "promociones")
data class PromocionEntity(
    // Room pone el número solo (1, 2, 3...)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    // Porcentaje de descuento, por ejemplo 20
    val descuento: Int,
    // "Todos", "Inactivos" o "Nuevos"
    val segmento: String
)