package cl.sanaesencia.app.model

// Estado de las pantallas del catálogo (guía 11): lo que se muestra y qué filtros están elegidos.
data class CatalogoUiState(
    // Servicios que pasan los filtros actuales
    val servicios: List<Servicio> = emptyList(),
    // Opciones del filtro de especialidad (la primera es "Todas")
    val especialidades: List<String> = emptyList(),
    val especialidadElegida: String = TODAS,
    // null = sin filtro de modalidad
    val modalidadElegida: Modalidad? = null,
    // Estos dos no se filtran: los usan las pantallas de profesionales y productos
    val terapeutas: List<Terapeuta> = emptyList(),
    val productos: List<Producto> = emptyList()
) {
    companion object {
        const val TODAS = "Todas"
    }
}
