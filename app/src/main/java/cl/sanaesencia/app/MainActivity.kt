package cl.sanaesencia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.sanaesencia.app.repository.ServicioRepository
import cl.sanaesencia.app.ui.screens.HomeScreen
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.ui.utils.obtenerAnchoPantalla

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSanaesencia_Grupo8Theme {
                val anchoPantalla = obtenerAnchoPantalla(this)
                HomeScreen(
                    anchoPantalla = anchoPantalla,
                    servicios = ServicioRepository().obtenerServicios()
                )
            }
        }
    }
}
