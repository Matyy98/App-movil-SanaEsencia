package cl.sanaesencia.app.logic

// Reglas de validación de todos los formularios (sin nada de pantalla).
// Cada función devuelve null si el dato está bien, o el mensaje de error si está mal.
object Validaciones {

    // Formatos permitidos (Regex, Experiencia 1)
    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val regexNombre = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]{3,}[A-Za-zÁÉÍÓÚáéíóúÑñ ]*$")
    private val regexTelefono = Regex("^\\+569[0-9]{8}$")
    private val regexTieneNumero = Regex(".*[0-9].*")
    private val regexTieneLetra = Regex(".*[A-Za-z].*")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre"
        !regexNombre.matches(valor.trim()) -> "Usa al menos 3 letras, sin números ni símbolos"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !regexCorreo.matches(valor.trim()) -> "Correo no válido (ejemplo: nombre@correo.cl)"
        else -> null
    }

    fun telefono(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu teléfono"
        !regexTelefono.matches(valor.trim()) -> "Usa el formato +569XXXXXXXX"
        else -> null
    }

    // Clave al registrarse: 8 o más caracteres, con letras y números
    fun clave(valor: String): String? = when {
        valor.isBlank() -> "Ingresa una contraseña"
        valor.length < 8 -> "Debe tener al menos 8 caracteres"
        !regexTieneNumero.matches(valor) -> "Debe incluir al menos un número"
        !regexTieneLetra.matches(valor) -> "Debe incluir al menos una letra"
        else -> null
    }

    // Clave al iniciar sesión: solo se revisa que no venga vacía
    fun claveIngreso(valor: String): String? =
        if (valor.isBlank()) "Ingresa tu contraseña" else null

    fun confirmarClave(clave: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Repite la contraseña"
        clave != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    fun terminos(aceptados: Boolean): String? =
        if (!aceptados) "Debes aceptar el uso de datos ficticios" else null

    // ---------- Promociones (Administración) ----------

    fun tituloPromocion(valor: String): String? = when {
        valor.isBlank() -> "Ingresa un título"
        valor.trim().length < 5 -> "El título debe tener al menos 5 caracteres"
        else -> null
    }

    fun descripcionPromocion(valor: String): String? = when {
        valor.isBlank() -> "Ingresa una descripción"
        valor.trim().length < 10 -> "Describe la promoción con al menos 10 caracteres"
        else -> null
    }

    // Descuento: número entero entre 5 y 50 (porcentaje)
    fun descuento(valor: String): String? {
        val numero = valor.trim().toIntOrNull()
        return when {
            valor.isBlank() -> "Ingresa el porcentaje de descuento"
            numero == null -> "Escribe solo números, sin el signo %"
            numero < 5 || numero > 50 -> "El descuento debe estar entre 5% y 50%"
            else -> null
        }
    }

    // Segmento: "Todos", "Inactivos" o "Nuevos"
    fun segmento(valor: String): String? =
        if (valor.isBlank()) "Elige a quién va dirigida la promoción" else null
}