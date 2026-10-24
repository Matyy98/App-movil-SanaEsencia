package cl.sanaesencia.app.ui.screens.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.CitaItem
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.viewmodel.AgendaViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Citas del paciente: próximas e historial, de 2 en 2, con opción de cancelar
@Composable
fun MisCitasScreen(
    agendaViewModel: AgendaViewModel,
    usuario: Usuario,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val citas by agendaViewModel.citas.collectAsState()
    // false = próximas, true = historial
    var verHistorial by remember { mutableStateOf(false) }
    var pagina by remember { mutableStateOf(0) }
    // id de la cita que se está por cancelar (0 = ninguna). Reemplaza al AlertDialog, que no vimos.
    var citaPorCancelar by remember { mutableStateOf(0) }

    val lista = if (verHistorial) {
        agendaViewModel.citasHistorial(citas, usuario.correo)
    } else {
        agendaViewModel.citasProximas(citas, usuario.correo)
    }
    val paginas = lista.chunked(2)
    // Si al cancelar sobra una página, se vuelve a la primera
    val paginaActual = if (pagina < paginas.size) pagina else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dos botones para cambiar de lista
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonOpcion(
                texto = "Próximas",
                seleccionado = !verHistorial,
                onClick = {
                    verHistorial = false
                    pagina = 0
                }
            )
            BotonOpcion(
                texto = "Historial",
                seleccionado = verHistorial,
                onClick = {
                    verHistorial = true
                    pagina = 0
                }
            )
        }

        if (paginas.isEmpty()) {
            Text(
                if (verHistorial) "Aún no tienes citas en tu historial" else "No tienes citas agendadas",
                style = MaterialTheme.typography.bodyLarge
            )
            if (!verHistorial) {
                Button(onClick = {
                    mainViewModel.navigateTo(Screen.Agendar, popUpToRoute = Screen.Inicio, singleTop = true)
                }) {
                    Text("Agendar hora")
                }
            }
        } else {
            paginas[paginaActual].forEach { cita ->
                CitaItem(
                    cita = cita,
                    servicio = agendaViewModel.nombreServicio(cita.servicioId),
                    profesional = agendaViewModel.nombreTerapeuta(cita.terapeutaId)
                )
                // Solo se pueden cancelar las agendadas
                if (cita.estado == EstadoCita.AGENDADA) {
                    if (citaPorCancelar == cita.id) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("¿Cancelar?", style = MaterialTheme.typography.labelLarge)
                            Button(
                                onClick = {
                                    agendaViewModel.cancelarCita(cita.id)
                                    citaPorCancelar = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Sí, cancelar")
                            }
                            Button(onClick = { citaPorCancelar = 0 }) { Text("No") }
                        }
                    } else {
                        Button(onClick = { citaPorCancelar = cita.id }) { Text("Cancelar cita") }
                    }
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
