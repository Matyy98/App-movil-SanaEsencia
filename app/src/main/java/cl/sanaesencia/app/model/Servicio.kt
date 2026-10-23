package cl.sanaesencia.app.model

// Modalidades de atención que ofrece el centro (enum visto en Experiencia 1).
enum class Modalidad(val etiqueta: String) {
    PRESENCIAL("Presencial"),
    ONLINE("Online"),
    AMBAS("Presencial y online")
}

// Servicio terapéutico del catálogo. Datos ficticios, solo para pruebas.
data class Servicio(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val descripcion: String,
    val duracionMinutos: Int,
    val precio: Int,
    val modalidad: Modalidad,
    // ids de los profesionales que atienden este servicio
    val terapeutaIds: List<Int>
)
