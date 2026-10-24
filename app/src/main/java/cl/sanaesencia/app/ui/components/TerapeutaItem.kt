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
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Terapeuta

// Muestra los datos de un profesional del centro
@Composable
fun TerapeutaItem(terapeuta: Terapeuta, modifier: Modifier = Modifier) {
    // Con agenda: color turquesa y check. Sin agenda: rojo y advertencia
    val colorAgenda = if (terapeuta.agendaDisponible) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    val textoAgenda = if (terapeuta.agendaDisponible) "Agenda disponible" else "Sin horas disponibles"

    Column(modifier = modifier.fillMaxWidth()) {
        Text(terapeuta.nombre, style = MaterialTheme.typography.titleMedium)
        Text(
            "${terapeuta.especialidad} · ${terapeuta.modalidad.etiqueta}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium
        )
        Text(terapeuta.descripcion, style = MaterialTheme.typography.bodyMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (terapeuta.agendaDisponible) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = colorAgenda
            )
            Text(textoAgenda, color = colorAgenda, style = MaterialTheme.typography.labelMedium)
        }
    }
}