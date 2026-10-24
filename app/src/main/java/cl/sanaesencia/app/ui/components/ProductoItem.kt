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
import cl.sanaesencia.app.model.Producto

// Muestra un producto natural de la tienda
@Composable
fun ProductoItem(producto: Producto, modifier: Modifier = Modifier) {
    // Si no queda stock se avisa en rojo
    val hayStock = producto.stock > 0
    val colorStock = if (hayStock) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    val textoStock = if (hayStock) "Stock: ${producto.stock} unidades" else "Sin stock"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
            // Precio con separador de miles, ej. $18.990
            Text(
                "$" + String.format("%,d", producto.precio),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(producto.categoria.etiqueta, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
        Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (hayStock) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = colorStock
            )
            Text(textoStock, color = colorStock, style = MaterialTheme.typography.labelMedium)
        }
    }
}