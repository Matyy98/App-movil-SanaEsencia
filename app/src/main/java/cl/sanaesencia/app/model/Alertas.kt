package cl.sanaesencia.app.model

// Qué tan probable es que un paciente abandone el tratamiento
enum class NivelRiesgo(val etiqueta: String) {
    BAJO("Bajo"),
    MEDIO("Medio"),
    ALTO("Alto")
}

// Aviso de un paciente que podría dejar de venir (deserción)
data class AlertaDesercion(
    val nombrePaciente: String,
    val correoPaciente: String,
    val sesionesCompletadas: Int,
    val diasSinSesion: Int,
    val tieneCitaFutura: Boolean,
    val nivel: NivelRiesgo,
    val recomendacion: String
)

// Cuánto se usó un box en el día
data class OcupacionBox(
    val box: Int,
    val citas: Int,
    val porcentaje: Int
)