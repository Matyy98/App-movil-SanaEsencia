package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.navigation.inicioPorRol
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch

// Una opción del menú: a qué pantalla lleva, su texto y su ícono
data class OpcionMenu(val screen: Screen, val titulo: String, val icono: ImageVector)

// Opciones de la barra de abajo según el rol (máximo 5 para que quepan)
fun opcionesInferiores(rol: Rol): List<OpcionMenu> = when (rol) {
    Rol.PACIENTE -> listOf(
        OpcionMenu(Screen.Inicio, "Inicio", Icons.Default.Home),
        OpcionMenu(Screen.Servicios, "Servicios", Icons.Default.Favorite),
        OpcionMenu(Screen.Agendar, "Agendar", Icons.Default.Add),
        OpcionMenu(Screen.MisCitas, "Mis citas", Icons.Default.DateRange),
        OpcionMenu(Screen.Perfil, "Perfil", Icons.Default.Person)
    )
    Rol.TERAPEUTA -> listOf(
        OpcionMenu(Screen.MisPacientes, "Pacientes", Icons.Default.Face),
        OpcionMenu(Screen.Perfil, "Perfil", Icons.Default.Person)
    )
    Rol.ADMIN -> listOf(
        OpcionMenu(Screen.PanelAdmin, "Panel", Icons.Default.Home),
        OpcionMenu(Screen.Alertas, "Alertas", Icons.Default.Warning),
        OpcionMenu(Screen.Promociones, "Promociones", Icons.Default.Star),
        OpcionMenu(Screen.Perfil, "Perfil", Icons.Default.Person)
    )
}

// Opciones del menú lateral. El paciente tiene además Profesionales y Productos.
fun opcionesLaterales(rol: Rol): List<OpcionMenu> = when (rol) {
    Rol.PACIENTE -> listOf(
        OpcionMenu(Screen.Inicio, "Inicio", Icons.Default.Home),
        OpcionMenu(Screen.Servicios, "Servicios", Icons.Default.Favorite),
        OpcionMenu(Screen.Profesionales, "Profesionales", Icons.Default.Face),
        OpcionMenu(Screen.Productos, "Productos", Icons.Default.ShoppingCart),
        OpcionMenu(Screen.Agendar, "Agendar hora", Icons.Default.Add),
        OpcionMenu(Screen.MisCitas, "Mis citas", Icons.Default.DateRange),
        OpcionMenu(Screen.Perfil, "Mi perfil", Icons.Default.Person)
    )
    else -> opcionesInferiores(rol)
}

// Marco común de las pantallas internas (guía 10):
// menú lateral + barra superior + barra inferior, según el rol del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    usuario: Usuario,
    rutaActual: Screen,
    mainViewModel: MainViewModel,
    onCerrarSesion: () -> Unit,
    contenido: @Composable (Modifier) -> Unit
) {
    // Si el menú lateral está abierto o cerrado
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Para abrir y cerrar el menú (es una animación, por eso va con launch)
    val scope = rememberCoroutineScope()
    // Pantalla de inicio del rol: al navegar se limpia el historial hasta ella
    val inicio = inicioPorRol(usuario.rol)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Encabezado del menú con el nombre y el rol
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(usuario.rol.etiqueta, color = MaterialTheme.colorScheme.primary)
                }
                opcionesLaterales(usuario.rol).forEach { opcion ->
                    NavigationDrawerItem(
                        label = { Text(opcion.titulo) },
                        icon = { Icon(opcion.icono, contentDescription = null) },
                        selected = opcion.screen == rutaActual,
                        onClick = {
                            scope.launch { drawerState.close() }
                            mainViewModel.navigateTo(opcion.screen, popUpToRoute = inicio, singleTop = true)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    icon = { Icon(Icons.Default.Close, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titulo) },
                    // Botón ☰ que abre el menú lateral
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    // Botón de perfil a la derecha
                    actions = {
                        IconButton(onClick = { mainViewModel.navigateTo(Screen.Perfil, popUpToRoute = inicio, singleTop = true) }) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    opcionesInferiores(usuario.rol).forEach { opcion ->
                        NavigationBarItem(
                            selected = opcion.screen == rutaActual,
                            onClick = { mainViewModel.navigateTo(opcion.screen, popUpToRoute = inicio, singleTop = true) },
                            icon = { Icon(opcion.icono, contentDescription = opcion.titulo) },
                            label = { Text(opcion.titulo) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Cada pantalla dibuja su contenido dentro del espacio que dejan las barras
            contenido(Modifier.padding(innerPadding))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PantallaBasePreview() {
    AppSanaesencia_Grupo8Theme {
        PantallaBase(
            titulo = "Inicio",
            usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE),
            rutaActual = Screen.Inicio,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            Text("Contenido de la pantalla", modifier = modifier.padding(16.dp))
        }
    }
}