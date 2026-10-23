package cl.sanaesencia.app.model

// Promoción para atraer pacientes cuando hay poca demanda
data class Promocion(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    // Porcentaje de descuento, por ejemplo 20
    val descuento: Int,
    // A quién va dirigida: "Todos", "Inactivos" o "Nuevos"
    val segmento: String
)