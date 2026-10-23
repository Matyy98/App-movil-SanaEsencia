package cl.sanaesencia.app.model

// Perfiles de la app: cada uno ve pantallas distintas
enum class Rol(val etiqueta: String) {
    PACIENTE("Paciente"),
    TERAPEUTA("Terapeuta"),
    ADMIN("Administración")
}

// Persona que usa la app..
data class Usuario(
    val nombre: String,
    val correo: String,
    val clave: String,
    val telefono: String,
    val rol: Rol,
    // Solo para terapeutas: el número del profesional que representa (0 si no es terapeuta)
    val terapeutaId: Int = 0
)