package cl.sanaesencia.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.repository.ServicioRepository
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.screens.HomeScreen
import cl.sanaesencia.app.ui.screens.acceso.LoginScreen
import cl.sanaesencia.app.ui.screens.acceso.RegistroScreen
import cl.sanaesencia.app.ui.screens.admin.PromocionesScreen
import cl.sanaesencia.app.ui.screens.paciente.AgendarScreen
import cl.sanaesencia.app.ui.screens.paciente.MisCitasScreen
import cl.sanaesencia.app.ui.screens.paciente.ProductosScreen
import cl.sanaesencia.app.ui.screens.paciente.ProfesionalesScreen
import cl.sanaesencia.app.ui.screens.paciente.ServiciosScreen
import cl.sanaesencia.app.ui.screens.perfil.PerfilScreen
import cl.sanaesencia.app.viewmodel.AdminViewModel
import cl.sanaesencia.app.viewmodel.AgendaViewModel
import cl.sanaesencia.app.viewmodel.CatalogoViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel
import cl.sanaesencia.app.viewmodel.PerfilViewModel
import cl.sanaesencia.app.viewmodel.RegistroViewModel
import cl.sanaesencia.app.viewmodel.SesionViewModel
import kotlinx.coroutines.flow.collectLatest

// Navegación de toda la app (guías 10 y 11).
// Los ViewModels se crean una sola vez aquí y se pasan a las pantallas.
@Composable
fun AppNavigation(anchoPantalla: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val sesionViewModel: SesionViewModel = viewModel()
    val registroViewModel: RegistroViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()
    val agendaViewModel: AgendaViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    // Usuario conectado (null = nadie) y si todavía se está revisando la sesión guardada
    val usuario by sesionViewModel.usuario.collectAsState()
    val revisandoSesion by sesionViewModel.revisandoSesion.collectAsState()

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

    // Cuando alguien inicia sesión, pasa del login a la pantalla de inicio de su perfil
    LaunchedEffect(key1 = usuario) {
        val actual = usuario
        val rutaActual = navController.currentBackStackEntry?.destination?.route
        if (actual != null && rutaActual == Screen.Login.route) {
            mainViewModel.navigateTo(inicioPorRol(actual.rol), popUpToRoute = Screen.Login, inclusive = true)
        }
    }

    if (revisandoSesion) {
        // Mientras se lee la sesión guardada se muestra una ruedita (guía 12)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Se calcula una sola vez: sin sesión parte en el login; con sesión, en el inicio de su perfil
        val destinoInicial = remember {
            val actual = usuario
            if (actual == null) Screen.Login else inicioPorRol(actual.rol)
        }

        // Mapa de pantallas: cada ruta dibuja su pantalla
        NavHost(navController = navController, startDestination = destinoInicial.route) {

            // ---------- Acceso ----------
            composable(Screen.Login.route) {
                LoginScreen(
                    sesionViewModel = sesionViewModel,
                    anchoPantalla = anchoPantalla,
                    onIrARegistro = { mainViewModel.navigateTo(Screen.Registro) }
                )
            }
            composable(Screen.Registro.route) {
                RegistroScreen(
                    registroViewModel = registroViewModel,
                    anchoPantalla = anchoPantalla,
                    onVolverALogin = { mainViewModel.navigateBack() }
                )
            }

            // ---------- Paciente ----------
            composable(Screen.Inicio.route) {
                ConMarco("Inicio", Screen.Inicio, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    HomeScreen(
                        anchoPantalla = anchoPantalla,
                        servicios = ServicioRepository().obtenerServicios(),
                        onAgendar = {
                            mainViewModel.navigateTo(Screen.Agendar, popUpToRoute = Screen.Inicio, singleTop = true)
                        },
                        onProfesionales = {
                            mainViewModel.navigateTo(Screen.Profesionales, popUpToRoute = Screen.Inicio, singleTop = true)
                        },
                        modifier = modifier
                    )
                }
            }
            composable(Screen.Servicios.route) {
                ConMarco("Servicios", Screen.Servicios, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    ServiciosScreen(catalogoViewModel, agendaViewModel, mainViewModel, modifier)
                }
            }
            composable(Screen.Profesionales.route) {
                ConMarco("Profesionales", Screen.Profesionales, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    ProfesionalesScreen(catalogoViewModel, modifier)
                }
            }
            composable(Screen.Productos.route) {
                ConMarco("Productos", Screen.Productos, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    ProductosScreen(catalogoViewModel, modifier)
                }
            }
            composable(Screen.Agendar.route) {
                ConMarco("Agendar hora", Screen.Agendar, usuario, mainViewModel, sesionViewModel) { u, modifier ->
                    AgendarScreen(agendaViewModel, u, mainViewModel, modifier)
                }
            }
            composable(Screen.MisCitas.route) {
                ConMarco("Mis citas", Screen.MisCitas, usuario, mainViewModel, sesionViewModel) { u, modifier ->
                    MisCitasScreen(agendaViewModel, u, mainViewModel, modifier)
                }
            }

            // ---------- Todos los perfiles ----------
            composable(Screen.Perfil.route) {
                ConMarco("Mi perfil", Screen.Perfil, usuario, mainViewModel, sesionViewModel) { u, modifier ->
                    PerfilScreen(perfilViewModel, u, modifier)
                }
            }

            // ---------- Terapeuta (pantalla pendiente) ----------
            composable(Screen.MisPacientes.route) {
                ConMarco("Mis pacientes", Screen.MisPacientes, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    PantallaPendiente("Mis pacientes", modifier)
                }
            }

            // ---------- Administración ----------
            composable(Screen.PanelAdmin.route) {
                ConMarco("Panel", Screen.PanelAdmin, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    PantallaPendiente("Panel de administración", modifier)
                }
            }
            composable(Screen.Alertas.route) {
                ConMarco("Alertas", Screen.Alertas, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    PantallaPendiente("Alertas de deserción", modifier)
                }
            }
            composable(Screen.Promociones.route) {
                ConMarco("Promociones", Screen.Promociones, usuario, mainViewModel, sesionViewModel) { _, modifier ->
                    PromocionesScreen(adminViewModel, modifier)
                }
            }
        }
    }
}

// Pone el menú lateral y las barras (PantallaBase) alrededor de una pantalla interna.
// Si no hay usuario conectado no dibuja nada (pasa un instante al cerrar sesión).
@Composable
private fun ConMarco(
    titulo: String,
    pantalla: Screen,
    usuario: Usuario?,
    mainViewModel: MainViewModel,
    sesionViewModel: SesionViewModel,
    contenido: @Composable (Usuario, Modifier) -> Unit
) {
    if (usuario != null) {
        PantallaBase(
            titulo = titulo,
            usuario = usuario,
            rutaActual = pantalla,
            mainViewModel = mainViewModel,
            onCerrarSesion = {
                // Primero vuelve al login borrando el historial, después borra la sesión
                mainViewModel.navigateTo(Screen.Login, popUpToRoute = inicioPorRol(usuario.rol), inclusive = true)
                sesionViewModel.cerrarSesion()
            }
        ) { modifier ->
            contenido(usuario, modifier)
        }
    }
}

// Pantalla temporal para las que todavía no se han hecho
@Composable
private fun PantallaPendiente(titulo: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
        Text("Esta pantalla todavía no está lista.")
    }
}
