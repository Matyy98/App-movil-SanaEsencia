package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Campo de formulario reutilizable (guía 11): ícono del campo, borde rojo,
// ícono de advertencia y mensaje bajo el campo cuando hay error
@Composable
fun CampoTexto(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    esClave: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        // Ícono a la izquierda (ej. sobre para el correo, candado para la clave)
        leadingIcon = { Icon(icono, contentDescription = null) },
        // Ícono de advertencia a la derecha, solo si hay error
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        // Borde rojo si hay error
        isError = error != null,
        // Mensaje de error debajo del campo
        supportingText = {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        // Si es clave, muestra puntitos en vez de letras
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}