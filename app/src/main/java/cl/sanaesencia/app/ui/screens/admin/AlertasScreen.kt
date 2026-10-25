package cl.sanaesencia.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.NivelRiesgo
import cl.sanaesencia.app.ui.components.AlertaItem
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.viewmodel.AdminViewModel

// Alertas de deserción de todo el centro, con filtro por nivel de riesgo
@Composable
fun AlertasScreen(adminViewModel: AdminViewModel, modifier: Modifier = Modifier) {
    val citas by adminViewModel.citas.collectAsState()
    val filtro by adminViewModel.filtroNivel.collectAsState()
    var pagina by remember { mutableStateOf(0) }

    val todas = adminViewModel.alertas(citas)
    val lista = adminViewModel.alertasFiltradas(citas, filtro)
    val paginas = lista.chunked(3)
    val paginaActual = if (pagina < paginas.size) pagina else 0

    // null = "Todos"
    val opciones = listOf(null, NivelRiesgo.ALTO, NivelRiesgo.MEDIO, NivelRiesgo.BAJO)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Alto: ${adminViewModel.contar(todas, NivelRiesgo.ALTO)} · " +
                    "Medio: ${adminViewModel.contar(todas, NivelRiesgo.MEDIO)} · " +
                    "Bajo: ${adminViewModel.contar(todas, NivelRiesgo.BAJO)}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        // Filtros en 2 filas de 2 para que quepan sin scroll
        opciones.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { nivel ->
                    BotonOpcion(
                        texto = nivel?.etiqueta ?: "Todos",
                        seleccionado = filtro == nivel,
                        onClick = {
                            adminViewModel.elegirFiltro(nivel)
                            pagina = 0
                        }
                    )
                }
            }
        }

        if (paginas.isEmpty()) {
            Text("No hay pacientes en este nivel", style = MaterialTheme.typography.bodyLarge)
        } else {
            paginas[paginaActual].forEach { alerta ->
                AlertaItem(alerta)
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