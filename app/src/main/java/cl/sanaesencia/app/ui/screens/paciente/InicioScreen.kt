package cl.sanaesencia.app.ui.screens.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.repository.CatalogoRepository
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.ui.components.ServicioItem
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.MainViewModel

// Inicio del paciente: según el ancho disponible elige qué diseño mostrar (guía 9)
@Composable
fun InicioScreen(
    usuario: Usuario,
    servicios: List<Servicio>,
    anchoPantalla: WindowWidthSizeClass,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    when (anchoPantalla) {
        WindowWidthSizeClass.Compact -> InicioCompacto(usuario, servicios, mainViewModel, modifier)
        WindowWidthSizeClass.Medium -> InicioMediano(usuario, servicios, mainViewModel, modifier)
        else -> InicioExpandido(usuario, servicios, mainViewModel, modifier)
    }
}

// Celular vertical: todo en una columna y los servicios de 2 en 2
@Composable
fun InicioCompacto(usuario: Usuario, servicios: List<Servicio>, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Saludo(usuario)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonesAccesos(mainViewModel)
        }
        ServiciosPaginados(servicios, porPagina = 2)
    }
}

// Celular horizontal o tablet chica: saludo a la izquierda y servicios a la derecha
@Composable
fun InicioMediano(usuario: Usuario, servicios: List<Servicio>, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier.width(240.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Saludo(usuario)
            BotonesAccesos(mainViewModel)
        }
        ServiciosPaginados(servicios, porPagina = 2, modifier = Modifier.fillMaxWidth())
    }
}

// Tablet: panel de bienvenida más grande y más servicios por página
@Composable
fun InicioExpandido(usuario: Usuario, servicios: List<Servicio>, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        Column(
            modifier = Modifier.width(360.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Centro Integrativo Sanaesencia",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Saludo(usuario)
            BotonesAccesos(mainViewModel)
        }
        ServiciosPaginados(servicios, porPagina = 4, modifier = Modifier.fillMaxWidth())
    }
}

// Saludo con el nombre del usuario conectado
@Composable
fun Saludo(usuario: Usuario) {
    Column {
        Text("Hola, ${usuario.nombre}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text("Acompañamos tu bienestar integral", style = MaterialTheme.typography.bodyLarge)
    }
}

// Accesos rápidos. Se ponen en fila o en columna según dónde se llamen.
// Limpian el historial hasta Inicio, igual que el menú.
@Composable
fun BotonesAccesos(mainViewModel: MainViewModel) {
    Button(onClick = { mainViewModel.navigateTo(Screen.Agendar, popUpToRoute = Screen.Inicio, singleTop = true) }) {
        Text("Agendar hora")
    }
    Button(onClick = { mainViewModel.navigateTo(Screen.MisCitas, popUpToRoute = Screen.Inicio, singleTop = true) }) {
        Text("Mis citas")
    }
}

// Lista de servicios por páginas (no usamos scroll)
@Composable
fun ServiciosPaginados(servicios: List<Servicio>, porPagina: Int, modifier: Modifier = Modifier) {
    // Página que se está mostrando (parte en 0)
    var pagina by remember { mutableStateOf(0) }
    val paginas = servicios.chunked(porPagina)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Nuestros servicios", style = MaterialTheme.typography.titleLarge)
        if (paginas.isEmpty()) {
            Text("No hay servicios para mostrar")
        } else {
            paginas[pagina].forEach { servicio ->
                ServicioItem(servicio)
            }
            Paginador(
                pagina = pagina,
                totalPaginas = paginas.size,
                onAnterior = { pagina = pagina - 1 },
                onSiguiente = { pagina = pagina + 1 }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun InicioCompactoPreview() {
    AppSanaesencia_Grupo8Theme {
        val usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE)
        PantallaBase(
            titulo = "Inicio",
            usuario = usuario,
            rutaActual = Screen.Inicio,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            InicioScreen(usuario, CatalogoRepository().obtenerServicios(), WindowWidthSizeClass.Compact, MainViewModel(), modifier)
        }
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 900)
@Composable
fun InicioMedianoPreview() {
    AppSanaesencia_Grupo8Theme {
        val usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE)
        PantallaBase(
            titulo = "Inicio",
            usuario = usuario,
            rutaActual = Screen.Inicio,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            InicioScreen(usuario, CatalogoRepository().obtenerServicios(), WindowWidthSizeClass.Medium, MainViewModel(), modifier)
        }
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 800)
@Composable
fun InicioExpandidoPreview() {
    AppSanaesencia_Grupo8Theme {
        val usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE)
        PantallaBase(
            titulo = "Inicio",
            usuario = usuario,
            rutaActual = Screen.Inicio,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            InicioScreen(usuario, CatalogoRepository().obtenerServicios(), WindowWidthSizeClass.Expanded, MainViewModel(), modifier)
        }
    }
}