package cl.sanaesencia.app.model

// En qué estado está una cita
enum class EstadoCita(val etiqueta: String) {
    AGENDADA("Agendada"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada")
}

// Cita del centro. Fecha "dd/MM/yyyy" y hora "HH:mm".
data class Cita(
    val id: Int,
    val correoPaciente: String,
    val nombrePaciente: String,
    val servicioId: Int,
    val terapeutaId: Int,
    val fecha: String,
    val hora: String,
    val modalidad: Modalidad,
    // Box del 1 al 8. Vale 0 si la cita es online.
    val box: Int,
    val estado: EstadoCita
)