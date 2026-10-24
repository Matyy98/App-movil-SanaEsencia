package cl.sanaesencia.app.ui.screens.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.components.TerapeutaItem
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.CatalogoViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Lista de profesionales del centro, de 3 en 3 (no usamos scroll)
@Composable
fun ProfesionalesScreen(catalogoViewModel: CatalogoViewModel, modifier: Modifier = Modifier) {
    // Lee el estado del catálogo; si cambia, la pantalla se vuelve a dibujar
    val estado by catalogoViewModel.estado.collectAsState()
    // Página que se está mostrando (parte en 0)
    var pagina by remember { mutableStateOf(0) }
    // Divide la lista en grupos de 3
    val paginas = estado.terapeutas.chunked(3)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Conoce a nuestro equipo", style = MaterialTheme.typography.titleLarge)

        if (paginas.isEmpty()) {
            Text("No hay profesionales para mostrar")
        } else {
            paginas[pagina].forEach { terapeuta ->
                TerapeutaItem(terapeuta)
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
fun ProfesionalesScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        PantallaBase(
            titulo = "Profesionales",
            usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE),
            rutaActual = Screen.Profesionales,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            ProfesionalesScreen(catalogoViewModel = CatalogoViewModel(), modifier = modifier)
        }
    }
}