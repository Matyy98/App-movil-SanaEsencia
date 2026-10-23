package cl.sanaesencia.app.navigation

// Órdenes de navegación que puede dar el MainViewModel (guía 10)
sealed class NavigationEvent {

    // Ir a una pantalla
    data class NavigateTo(
        val route: Screen,
        // Si no es null, borra el historial hasta esta pantalla
        val popUpToRoute: Screen? = null,
        // true = también borra la pantalla de popUpToRoute
        val inclusive: Boolean = false,
        // true = no abre la misma pantalla dos veces seguidas
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver a la pantalla anterior
    data object PopBackStack : NavigationEvent()
}