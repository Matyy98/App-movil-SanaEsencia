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
import cl.sanaesencia.app.ui.components.ServicioItem
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Diseño para pantallas pequeñas (celular en vertical): todo en una sola columna.
@Composable
fun HomeCompactScreen(servicios: List<Servicio>, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Centro Integrativo Sanaesencia",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Acompañamos tu bienestar integral",
                style = MaterialTheme.typography.bodyLarge
            )

            servicios.forEach { servicio ->
                ServicioItem(servicio = servicio)
            }

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

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeCompactScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        HomeCompactScreen(servicios = ServicioRepository().obtenerServicios())
    }
}
