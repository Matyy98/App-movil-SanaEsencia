package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.AlertaDesercion
import cl.sanaesencia.app.model.NivelRiesgo
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Muestra un paciente con su riesgo de deserción (lo usan el terapeuta y Administración)
@Composable
fun AlertaItem(alerta: AlertaDesercion, modifier: Modifier = Modifier) {
    // Rojo = alto, azul = medio, turquesa = bajo
    val color = when (alerta.nivel) {
        NivelRiesgo.ALTO -> MaterialTheme.colorScheme.error
        NivelRiesgo.MEDIO -> MaterialTheme.colorScheme.tertiary
        NivelRiesgo.BAJO -> MaterialTheme.colorScheme.secondary
    }
    // Con cita futura se avisa al final del detalle
    val detalleCita = if (alerta.tieneCitaFutura) " · Con cita agendada" else ""

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(alerta.nombrePaciente, style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (alerta.nivel == NivelRiesgo.BAJO) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = color
                )
                Text("Riesgo ${alerta.nivel.etiqueta}", color = color, style = MaterialTheme.typography.labelLarge)
            }
        }
        Text(
            "${alerta.sesionesCompletadas} sesiones · ${alerta.diasSinSesion} días sin sesión$detalleCita",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(alerta.recomendacion, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun AlertaItemPreview() {
    AppSanaesencia_Grupo8Theme {
        AlertaItem(
            AlertaDesercion("Sofía Pérez", "sofia.perez@correo.cl", 2, 30, false, NivelRiesgo.ALTO,
                "Contactar hoy y ofrecer una promoción para retomar")
        )
    }
}