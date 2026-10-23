package cl.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.navigation.NavigationEvent
import cl.sanaesencia.app.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Centraliza la navegación: las pantallas piden ir a otra y AppNavigation lo hace (guía 10)
class MainViewModel : ViewModel() {

    // "Radio" por donde se transmiten las órdenes. Solo este ViewModel puede transmitir.
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Versión de solo lectura: AppNavigation solo puede escuchar
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Ir a una pantalla
    fun navigateTo(
        screen: Screen,
        popUpToRoute: Screen? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(screen, popUpToRoute, inclusive, singleTop)
            )
        }
    }

    // Volver a la pantalla anterior
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}