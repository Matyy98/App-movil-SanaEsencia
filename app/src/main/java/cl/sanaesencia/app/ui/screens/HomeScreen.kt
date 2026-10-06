package cl.sanaesencia.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.repository.ServicioRepository
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Pantalla base (guía 8): Scaffold + Column + Row + Text + Button.
@Composable
fun HomeScreen(servicios: List<Servicio>, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Encabezado
            Text(
                text = "Centro Integrativo Sanaesencia",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Acompañamos tu bienestar integral",
                style = MaterialTheme.typography.bodyLarge
            )

            // Lista de servicios (colección recorrida con forEach, visto en Experiencia 1)
            servicios.forEach { servicio ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = servicio.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(text = servicio.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Modalidad: ${servicio.modalidad}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Acciones principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* pendiente: navegación a agendar */ }) {
                    Text("Agendar hora")
                }
                Button(onClick = { /* pendiente: navegación a profesionales */ }) {
                    Text("Profesionales")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        HomeScreen(servicios = ServicioRepository().obtenerServicios())
    }
}
