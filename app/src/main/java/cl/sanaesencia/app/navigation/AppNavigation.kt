package cl.sanaesencia.app.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.sanaesencia.app.repository.ServicioRepository
import cl.sanaesencia.app.ui.screens.HomeScreen
import cl.sanaesencia.app.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

// Navegación de toda la app (guías 10 y 11).
// Los ViewModels se crean una sola vez aquí y se pasan a las pantallas.
@Composable
fun AppNavigation(anchoPantalla: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()

    // Escucha las órdenes del MainViewModel y cambia de pantalla
    LaunchedEffect(key1 = Unit) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        // Si la orden pide borrar historial, se borra hasta esa pantalla
                        event.popUpToRoute?.let {
                            popUpTo(it.route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    // Mapa de pantallas: cada ruta dibuja su pantalla
    NavHost(navController = navController, startDestination = Screen.Inicio.route) {
        composable(Screen.Inicio.route) {
            HomeScreen(
                anchoPantalla = anchoPantalla,
                servicios = ServicioRepository().obtenerServicios()
            )
        }
    }
}