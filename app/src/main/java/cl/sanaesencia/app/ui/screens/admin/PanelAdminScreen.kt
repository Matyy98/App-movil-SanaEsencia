package cl.sanaesencia.app.ui.screens.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.logic.GestorBoxes
import cl.sanaesencia.app.model.NivelRiesgo
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.viewmodel.AdminViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Panel de Administración: ocupación de los 8 boxes por día y acceso a promociones y alertas
@Composable
fun PanelAdminScreen(adminViewModel: AdminViewModel, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    val citas by adminViewModel.citas.collectAsState()
    val dia by adminViewModel.dia.collectAsState()

    val ocupaciones = adminViewModel.ocupacion(citas, dia)
    val promedio = adminViewModel.promedio(ocupaciones)
    val habil = adminViewModel.esDiaHabil(dia)
    // Los domingos no se atiende, así que no cuentan como baja demanda
    val bajaDemanda = habil && adminViewModel.esBajaDemanda(promedio)
    val altas = adminViewModel.contar(adminViewModel.alertas(citas), NivelRiesgo.ALTO)

    // El promedio pasa a rojo con animación si hay baja demanda (guía 12)
    val colorPromedio by animateColorAsState(
        targetValue = if (bajaDemanda) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(500),
        label = ""
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Día que se está mirando, con flechas para cambiarlo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { adminViewModel.cambiarDia(-1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Día anterior")
            }
            Text(
                (if (adminViewModel.esHoy(dia)) "Hoy · " else "") + Fechas.formatear(dia),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { adminViewModel.cambiarDia(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Día siguiente")
            }
        }

        Text("Ocupación de boxes", style = MaterialTheme.typography.titleMedium)

        if (!habil) {
            Text("Domingo: el centro no atiende", style = MaterialTheme.typography.bodyLarge)
        } else {
            // Una fila por box: citas del día y porcentaje (citas / 10 bloques)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ocupaciones.forEach { ocupacion ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Box ${ocupacion.box}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${ocupacion.citas} de ${GestorBoxes.BLOQUES_POR_DIA} horas · ${ocupacion.porcentaje}%",
                            color = if (ocupacion.citas == 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Text("Promedio del día: $promedio%", color = colorPromedio, style = MaterialTheme.typography.titleMedium)

        // Aviso animado de baja demanda (regla: promedio bajo 40%)
        MensajeAnimado(
            texto = if (bajaDemanda) "Baja demanda: conviene crear una promoción" else null,
            esError = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                // Abre Promociones directo en el formulario
                adminViewModel.abrirFormulario()
                mainViewModel.navigateTo(Screen.Promociones, popUpToRoute = Screen.PanelAdmin, singleTop = true)
            }) {
                Text("Crear promoción")
            }
            Button(onClick = {
                mainViewModel.navigateTo(Screen.Alertas, popUpToRoute = Screen.PanelAdmin, singleTop = true)
            }) {
                Text("Alertas ($altas)")
            }
        }
    }
}