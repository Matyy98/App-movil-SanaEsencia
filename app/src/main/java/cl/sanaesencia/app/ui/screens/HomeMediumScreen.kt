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

// Diseño para pantallas medianas (tablet pequeña o celular en horizontal):
// los servicios se reparten en dos columnas.
@Composable
fun HomeMediumScreen(
    servicios: List<Servicio>,
    onAgendar: () -> Unit,
    onProfesionales: () -> Unit,
    modifier: Modifier = Modifier
) {
    // La lista se reparte en dos columnas con filter (ids impares a la izquierda, pares a la derecha).
    val columnaIzquierda = servicios.filter { it.id % 2 == 1 }
    val columnaDerecha = servicios.filter { it.id % 2 == 0 }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Centro Integrativo Sanaesencia",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Acompañamos tu bienestar integral",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.width(260.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    columnaIzquierda.forEach { ServicioItem(servicio = it) }
                }
                // La segunda columna ocupa el ancho que queda.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    columnaDerecha.forEach { ServicioItem(servicio = it) }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAgendar) {
                    Text("Agendar hora")
                }
                Button(onClick = onProfesionales) {
                    Text("Profesionales")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 900)
@Composable
fun HomeMediumScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        HomeMediumScreen(
            servicios = ServicioRepository().obtenerServicios(),
            onAgendar = {},
            onProfesionales = {}
        )
    }
}
