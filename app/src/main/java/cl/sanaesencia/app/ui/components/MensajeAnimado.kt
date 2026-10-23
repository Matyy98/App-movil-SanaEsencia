package cl.sanaesencia.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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

// Mensaje de éxito o error que aparece y desaparece con animación (guía 12)
@Composable
fun MensajeAnimado(texto: String?, esError: Boolean, modifier: Modifier = Modifier) {
    // Rojo si es error, turquesa si es éxito
    val color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary

    // Si texto es null el mensaje se esconde; si tiene algo, aparece animado
    AnimatedVisibility(visible = texto != null, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (esError) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = color
            )
            Text(texto ?: "", color = color, style = MaterialTheme.typography.bodyMedium)
        }
    }
}