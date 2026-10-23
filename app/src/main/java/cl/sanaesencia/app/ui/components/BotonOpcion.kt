package cl.sanaesencia.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

// Botón para elegir una opción. Al seleccionarlo cambia de color con animación (guía 12).
@Composable
fun BotonOpcion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // El color no cambia de golpe: pasa de uno a otro en medio segundo (500 ms)
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = tween(500),
        label = ""
    )
    val colorTexto by animateColorAsState(
        targetValue = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
        animationSpec = tween(500),
        label = ""
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = colorFondo, contentColor = colorTexto),
        modifier = modifier
    ) {
        // Si está elegido, muestra un ✓ antes del texto
        if (seleccionado) {
            Icon(Icons.Default.Check, contentDescription = "Seleccionado")
        }
        Text(texto)
    }
}