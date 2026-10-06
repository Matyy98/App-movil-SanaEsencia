package cl.sanaesencia.app.model

// Modalidades de atención que ofrece el centro (enum visto en Experiencia 1).
enum class Modalidad {
    PRESENCIAL,
    ONLINE,
    AMBAS
}

// Servicio terapéutico del catálogo. Datos ficticios, solo para pruebas.
data class Servicio(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val descripcion: String,
    val modalidad: Modalidad
)
