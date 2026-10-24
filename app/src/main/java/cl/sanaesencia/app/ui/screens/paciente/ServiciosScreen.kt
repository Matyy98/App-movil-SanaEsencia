package cl.sanaesencia.app.ui.screens.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.ui.components.ServicioItem
import cl.sanaesencia.app.viewmodel.AgendaViewModel
import cl.sanaesencia.app.viewmodel.CatalogoViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Cuántos servicios se ven por página (no usamos scroll). Si sobra espacio se puede subir a 2.
private const val SERVICIOS_POR_PAGINA = 1

// Catálogo de servicios con dos filtros (cada botón cambia al siguiente valor al tocarlo)
@Composable
fun ServiciosScreen(
    catalogoViewModel: CatalogoViewModel,
    agendaViewModel: AgendaViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val estado by catalogoViewModel.estado.collectAsState()
    // Página que se está mostrando (parte en 0)
    var pagina by remember { mutableStateOf(0) }
    val paginas = estado.servicios.chunked(SERVICIOS_POR_PAGINA)
    // Si un filtro deja menos páginas, se vuelve a la primera
    val paginaActual = if (pagina < paginas.size) pagina else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Filtro de especialidad
        Button(
            onClick = {
                catalogoViewModel.siguienteEspecialidad()
                pagina = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Especialidad: ${estado.especialidadElegida}")
        }
        // Filtro de modalidad (null = todas)
        Button(
            onClick = {
                catalogoViewModel.siguienteModalidad()
                pagina = 0
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Modalidad: ${estado.modalidadElegida?.etiqueta ?: "Todas"}")
        }

        if (paginas.isEmpty()) {
            Text("No hay servicios con esos filtros", style = MaterialTheme.typography.bodyLarge)
        } else {
            paginas[paginaActual].forEach { servicio ->
                ServicioItem(servicio)
                Text(
                    "${servicio.duracionMinutos} min · $" + String.format("%,d", servicio.precio),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
                // Deja el servicio ya elegido y salta al paso 2 del formulario
                Button(onClick = {
                    agendaViewModel.agendarServicio(servicio.id)
                    mainViewModel.navigateTo(Screen.Agendar, popUpToRoute = Screen.Inicio, singleTop = true)
                }) {
                    Text("Agendar este servicio")
                }
            }
            Paginador(
                pagina = paginaActual,
                totalPaginas = paginas.size,
                onAnterior = { pagina = paginaActual - 1 },
                onSiguiente = { pagina = paginaActual + 1 }
            )
        }
    }
}
