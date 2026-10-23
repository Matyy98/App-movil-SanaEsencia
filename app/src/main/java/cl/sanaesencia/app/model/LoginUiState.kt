package cl.sanaesencia.app.model

// Lo que el usuario escribe en el login (patrón guía 11)
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    // true mientras se revisan los datos (se muestra una ruedita de carga)
    val cargando: Boolean = false,
    // Error que no es de un campo, por ejemplo "Correo o contraseña incorrectos"
    val errorGeneral: String? = null,
    val errores: LoginErrores = LoginErrores()
)

// Error de cada campo del login (null = sin error)
data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null
)