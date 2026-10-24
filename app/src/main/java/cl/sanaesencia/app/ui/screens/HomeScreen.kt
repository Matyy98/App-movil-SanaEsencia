package cl.sanaesencia.app.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.sanaesencia.app.model.Servicio

// Pantalla principal: según el ancho disponible elige qué diseño mostrar (guía 9).
// Los dos botones avisan hacia arriba (onAgendar, onProfesionales) y AppNavigation cambia de pantalla.
@Composable
fun HomeScreen(
    anchoPantalla: WindowWidthSizeClass,
    servicios: List<Servicio>,
    onAgendar: () -> Unit,
    onProfesionales: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (anchoPantalla) {
        WindowWidthSizeClass.Compact -> HomeCompactScreen(servicios, onAgendar, onProfesionales, modifier)
        WindowWidthSizeClass.Medium -> HomeMediumScreen(servicios, onAgendar, onProfesionales, modifier)
        else -> HomeExpandedScreen(servicios, onAgendar, onProfesionales, modifier)
    }
}
