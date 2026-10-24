package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.sanaesencia.app.model.Servicio

// Componente reutilizable: muestra un servicio. Lo usan Inicio y Servicios.
@Composable
fun ServicioItem(servicio: Servicio, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = servicio.nombre, style = MaterialTheme.typography.titleMedium)
        Text(text = servicio.descripcion, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Modalidad: ${servicio.modalidad.etiqueta}",
            style = MaterialTheme.typography.labelMedium
        )
    }
}
