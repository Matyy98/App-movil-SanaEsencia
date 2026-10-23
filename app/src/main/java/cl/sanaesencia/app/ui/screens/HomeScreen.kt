package cl.sanaesencia.app.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.sanaesencia.app.model.Servicio

// Pantalla principal: según el ancho disponible elige qué diseño mostrar (guía 9).
@Composable
fun HomeScreen(
    anchoPantalla: WindowWidthSizeClass,
    servicios: List<Servicio>,
    modifier: Modifier = Modifier
) {
    when (anchoPantalla) {
        WindowWidthSizeClass.Compact -> HomeCompactScreen(servicios, modifier)
        WindowWidthSizeClass.Medium -> HomeMediumScreen(servicios, modifier)
        else -> HomeExpandedScreen(servicios, modifier)
    }
}
