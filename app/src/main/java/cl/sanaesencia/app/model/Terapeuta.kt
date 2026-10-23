package cl.sanaesencia.app.model

// Profesional del centro (datos ficticios)
data class Terapeuta(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val descripcion: String,
    val modalidad: Modalidad,
    // false si el profesional no tiene horas disponibles
    val agendaDisponible: Boolean
)