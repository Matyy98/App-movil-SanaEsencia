package cl.sanaesencia.app.model

// Lo que se escribe en el formulario de promociones (patrón guía 11)
data class PromocionUiState(
    val titulo: String = "",
    val descripcion: String = "",
    // Se guarda como texto porque viene de un campo; se convierte a número al guardar
    val descuento: String = "",
    // "Todos", "Inactivos" o "Nuevos" ("" = no elegido)
    val segmento: String = "",
    // true = se ve el formulario, false = se ve la lista de promociones
    val mostrarFormulario: Boolean = false,
    // Mensaje verde después de guardar (null = no se muestra)
    val mensajeExito: String? = null,
    val errores: PromocionErrores = PromocionErrores()
) {
    companion object {
        // Opciones de segmento para los botones
        val SEGMENTOS = listOf("Todos", "Inactivos", "Nuevos")
    }
}

// Error de cada campo del formulario (null = sin error)
data class PromocionErrores(
    val titulo: String? = null,
    val descripcion: String? = null,
    val descuento: String? = null,
    val segmento: String? = null
)