package cl.sanaesencia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.sanaesencia.app.navigation.AppNavigation
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.ui.utils.obtenerAnchoPantalla

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSanaesencia_Grupo8Theme {
                // Ancho de pantalla para elegir el diseño (guía 9)
                val anchoPantalla = obtenerAnchoPantalla(this)
                // Toda la navegación de la app (guía 10)
                AppNavigation(anchoPantalla)
            }
        }
    }
}
