package cl.sanaesencia.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.repository.ServicioRepository
import cl.sanaesencia.app.ui.components.ServicioItem
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Diseño para pantallas grandes (tablet): panel de presentación a la izquierda
// y la lista de servicios a la derecha.
@Composable
fun HomeExpandedScreen(servicios: List<Servicio>, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            // Panel izquierdo: presentación y acciones
            Column(
                modifier = Modifier.width(360.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Centro Integrativo Sanaesencia",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Acompañamos tu bienestar integral",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(onClick = { /* pendiente: navegación a agendar */ }) {
                    Text("Agendar hora")
                }
                Button(onClick = { /* pendiente: navegación a profesionales */ }) {
                    Text("Profesionales")
                }
            }

            // Panel derecho: servicios (ocupa el ancho que queda)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                servicios.forEach { servicio ->
                    ServicioItem(servicio = servicio)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1100, heightDp = 800)
@Composable
fun HomeExpandedScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        HomeExpandedScreen(servicios = ServicioRepository().obtenerServicios())
    }
}
