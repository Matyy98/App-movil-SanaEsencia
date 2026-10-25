package cl.sanaesencia.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.repository.CatalogoRepository
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.screens.acceso.LoginScreen
import cl.sanaesencia.app.ui.screens.acceso.RegistroScreen
import cl.sanaesencia.app.ui.screens.admin.PromocionesScreen
import cl.sanaesencia.app.ui.screens.admin.AlertasScreen
import cl.sanaesencia.app.ui.screens.admin.PanelAdminScreen
import cl.sanaesencia.app.ui.screens.paciente.AgendarScreen
import cl.sanaesencia.app.ui.screens.paciente.InicioScreen
import cl.sanaesencia.app.ui.screens.paciente.MisCitasScreen
import cl.sanaesencia.app.ui.screens.paciente.ProductosScreen
import cl.sanaesencia.app.ui.screens.paciente.ProfesionalesScreen
import cl.sanaesencia.app.ui.screens.paciente.ServiciosScreen
import cl.sanaesencia.app.ui.screens.perfil.PerfilScreen
import cl.sanaesencia.app.ui.screens.terapeuta.MisPacientesScreen
import cl.sanaesencia.app.viewmodel.TerapeutaViewModel
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
    val adminViewModel: AdminViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val terapeutaViewModel: TerapeutaViewModel = viewModel()

    // Usuario conectado (null = nadie) y si todavía se revisa la sesión guardada
    val usuario by sesionViewModel.usuario.collectAsState()
    val revisandoSesion by sesionViewModel.revisandoSesion.collectAsState()

    // Servicios que se muestran en el inicio
    val servicios = remember { CatalogoRepository().obtenerServicios() }

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

    // Cerrar sesión: borra la sesión y vuelve al login sin dejar historial
    val cerrarSesion = { rol: Rol ->
        sesionViewModel.cerrarSesion()
        mainViewModel.navigateTo(Screen.Login, popUpToRoute = inicioPorRol(rol), inclusive = true)
    }

    if (revisandoSesion) {
        // Mientras se revisa si había una sesión guardada se muestra la ruedita
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // La primera pantalla se elige una sola vez: el login, o el inicio del rol si ya había sesión
        val primeraPantalla = remember { usuario?.let { inicioPorRol(it.rol) } ?: Screen.Login }

        // Mapa de pantallas: cada ruta dibuja su pantalla
        NavHost(navController = navController, startDestination = primeraPantalla.route) {

            // Acceso (sin menú)
            composable(Screen.Login.route) {
                // Cuando el login sale bien se va al inicio del rol y el login se borra del historial
                LaunchedEffect(key1 = usuario) {
                    usuario?.let {
                        mainViewModel.navigateTo(inicioPorRol(it.rol), popUpToRoute = Screen.Login, inclusive = true)
                    }
                }
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

            // Paciente
            composable(Screen.Inicio.route) {
                PantallaInterna("Inicio", Screen.Inicio, usuario, mainViewModel, cerrarSesion) { actual, modifier ->
                    InicioScreen(actual, servicios, anchoPantalla, mainViewModel, modifier)
                }
            }
            composable(Screen.Servicios.route) {
                PantallaInterna("Servicios", Screen.Servicios, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    ServiciosScreen(catalogoViewModel, agendaViewModel, mainViewModel, modifier)
                }
            }
            composable(Screen.Profesionales.route) {
                PantallaInterna("Profesionales", Screen.Profesionales, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    ProfesionalesScreen(catalogoViewModel, modifier)
                }
            }
            composable(Screen.Productos.route) {
                PantallaInterna("Productos", Screen.Productos, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    ProductosScreen(catalogoViewModel, modifier)
                }
            }
            composable(Screen.Agendar.route) {
                PantallaInterna("Agendar hora", Screen.Agendar, usuario, mainViewModel, cerrarSesion) { actual, modifier ->
                    AgendarScreen(agendaViewModel, actual, mainViewModel, modifier)
                }
            }
            composable(Screen.MisCitas.route) {
                PantallaInterna("Mis citas", Screen.MisCitas, usuario, mainViewModel, cerrarSesion) { actual, modifier ->
                    MisCitasScreen(agendaViewModel, actual, mainViewModel, modifier)
                }
            }

            // Terapeuta
            composable(Screen.MisPacientes.route) {
                PantallaInterna("Mis pacientes", Screen.MisPacientes, usuario, mainViewModel, cerrarSesion) { actual, modifier ->
                    MisPacientesScreen(terapeutaViewModel, actual, modifier)
                }
            }

            // Administración
            composable(Screen.PanelAdmin.route) {
                PantallaInterna("Panel", Screen.PanelAdmin, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    PanelAdminScreen(adminViewModel, mainViewModel, modifier)
                }
            }
            composable(Screen.Alertas.route) {
                PantallaInterna("Alertas", Screen.Alertas, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    AlertasScreen(adminViewModel, modifier)
                }
            }
            composable(Screen.Promociones.route) {
                PantallaInterna("Promociones", Screen.Promociones, usuario, mainViewModel, cerrarSesion) { _, modifier ->
                    PromocionesScreen(adminViewModel, modifier)
                }
            }

            // Todos los perfiles
            composable(Screen.Perfil.route) {
                PantallaInterna("Mi perfil", Screen.Perfil, usuario, mainViewModel, cerrarSesion) { actual, modifier ->
                    PerfilScreen(perfilViewModel, actual, modifier)
                }
            }
        }
    }
}

// Pone la pantalla dentro del marco con menú (PantallaBase).
// Si no hay nadie conectado (justo al cerrar sesión) no dibuja nada.
@Composable
fun PantallaInterna(
    titulo: String,
    ruta: Screen,
    usuario: Usuario?,
    mainViewModel: MainViewModel,
    onCerrarSesion: (Rol) -> Unit,
    contenido: @Composable (Usuario, Modifier) -> Unit
) {
    if (usuario != null) {
        PantallaBase(
            titulo = titulo,
            usuario = usuario,
            rutaActual = ruta,
            mainViewModel = mainViewModel,
            onCerrarSesion = { onCerrarSesion(usuario.rol) }
        ) { modifier ->
            contenido(usuario, modifier)
        }
    }
}