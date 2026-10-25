package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// Botones para cambiar de página en las listas (reemplaza al scroll, que no se vio en clase)
@Composable
fun Paginador(
    pagina: Int,
    totalPaginas: Int,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Con una sola página no hace falta mostrarlo
    if (totalPaginas > 1) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // En la primera página "Anterior" queda desactivado
            Button(onClick = onAnterior, enabled = pagina > 0) { Text("Pag. anterior") }
            // pagina parte en 0, por eso se muestra pagina + 1
            Text(" ${pagina + 1} de $totalPaginas")
            // En la última página "Siguiente" queda desactivado
            Button(onClick = onSiguiente, enabled = pagina < totalPaginas - 1) { Text("Pag. siguiente") }
        }
    }
}