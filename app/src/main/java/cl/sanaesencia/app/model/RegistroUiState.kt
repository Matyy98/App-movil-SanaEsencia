package cl.sanaesencia.app.model

// Lo que el usuario escribe en el registro (patrón guía 11)
data class RegistroUiState(
    // 1 = datos personales, 2 = contraseña y términos
    val paso: Int = 1,
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val aceptaTerminos: Boolean = false,
    // true cuando la cuenta se creó (se muestra un mensaje de éxito)
    val registroExitoso: Boolean = false,
    // Error que no es de un campo, por ejemplo "Ya existe una cuenta con ese correo"
    val errorGeneral: String? = null,
    val errores: RegistroErrores = RegistroErrores()
)

// Error de cada campo del registro (null = sin error)
data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val terminos: String? = null
)