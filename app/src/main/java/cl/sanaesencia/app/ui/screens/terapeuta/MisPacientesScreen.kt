package cl.sanaesencia.app.ui.screens.terapeuta

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
import cl.sanaesencia.app.logic.MotorDesercion
import cl.sanaesencia.app.model.NivelRiesgo
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.ui.components.AlertaItem
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.CitaItem
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.viewmodel.TerapeutaViewModel

// Pantalla del terapeuta: sus pacientes con riesgo de deserción y su agenda
@Composable
fun MisPacientesScreen(terapeutaViewModel: TerapeutaViewModel, usuario: Usuario, modifier: Modifier = Modifier) {
    val citas by terapeutaViewModel.citas.collectAsState()
    // false = pacientes, true = agenda
    var verAgenda by remember { mutableStateOf(false) }
    var pagina by remember { mutableStateOf(0) }
    // id de la cita que se está por cancelar (0 = ninguna). Reemplaza al AlertDialog, que no vimos.
    var citaPorCancelar by remember { mutableStateOf(0) }

    val pacientes = terapeutaViewModel.pacientes(citas, usuario.terapeutaId)
    val agenda = terapeutaViewModel.agenda(citas, usuario.terapeutaId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dos botones para cambiar de vista (color animado)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonOpcion(
                texto = "Pacientes",
                seleccionado = !verAgenda,
                onClick = {
                    verAgenda = false
                    pagina = 0
                    citaPorCancelar = 0
                }
            )
            BotonOpcion(
                texto = "Agenda",
                seleccionado = verAgenda,
                onClick = {
                    verAgenda = true
                    pagina = 0
                    citaPorCancelar = 0
                }
            )
        }

        if (!verAgenda) {
            // ---------- Pacientes con su riesgo ----------
            Text(
                "Alto: ${MotorDesercion.contar(pacientes, NivelRiesgo.ALTO)} · " +
                        "Medio: ${MotorDesercion.contar(pacientes, NivelRiesgo.MEDIO)} · " +
                        "Bajo: ${MotorDesercion.contar(pacientes, NivelRiesgo.BAJO)}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            val paginas = pacientes.chunked(3)
            val paginaActual = if (pagina < paginas.size) pagina else 0
            if (paginas.isEmpty()) {
                Text("Aún no tienes pacientes con sesiones", style = MaterialTheme.typography.bodyLarge)
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
        } else {
            // ---------- Agenda del profesional ----------
            val paginas = agenda.chunked(2)
            val paginaActual = if (pagina < paginas.size) pagina else 0
            if (paginas.isEmpty()) {
                Text("No tienes citas agendadas", style = MaterialTheme.typography.bodyLarge)
            } else {
                paginas[paginaActual].forEach { cita ->
                    // En la agenda del terapeuta se muestra el nombre del paciente
                    CitaItem(
                        cita = cita,
                        servicio = terapeutaViewModel.nombreServicio(cita.servicioId),
                        profesional = cita.nombrePaciente
                    )
                    if (citaPorCancelar == cita.id) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("¿Cancelar?", style = MaterialTheme.typography.labelLarge)
                            Button(
                                onClick = {
                                    terapeutaViewModel.cancelar(cita.id)
                                    citaPorCancelar = 0
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Sí, cancelar")
                            }
                            Button(onClick = { citaPorCancelar = 0 }) { Text("No") }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Desactivado si la sesión todavía no llega
                            Button(
                                onClick = { terapeutaViewModel.marcarRealizada(cita.id) },
                                enabled = terapeutaViewModel.puedeMarcarRealizada(cita)
                            ) {
                                Text("Realizada")
                            }
                            Button(onClick = { citaPorCancelar = cita.id }) { Text("Cancelar") }
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
}