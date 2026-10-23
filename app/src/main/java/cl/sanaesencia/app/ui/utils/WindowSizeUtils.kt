package cl.sanaesencia.app.ui.utils

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

// Calcula el ancho de la pantalla (Compact, Medium o Expanded).
// Se reutiliza en toda la app para decidir qué diseño mostrar (guía 9).
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerAnchoPantalla(activity: Activity): WindowWidthSizeClass {
    return calculateWindowSizeClass(activity).widthSizeClass
}
