package cl.sanaesencia.app.ui.screens.perfil

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.ImagenInteligente
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.MainViewModel
import cl.sanaesencia.app.viewmodel.PerfilViewModel

// Pantalla de perfil: conecta el ViewModel con la cámara y la galería (guía 13)
@Composable
fun PerfilScreen(perfilViewModel: PerfilViewModel, usuario: Usuario, modifier: Modifier = Modifier) {
    val fotoUri by perfilViewModel.fotoUri.collectAsState()
    val recordatorios by perfilViewModel.recordatorios.collectAsState()
    // Mensaje si el usuario no da permiso de cámara (null = no se muestra)
    var mensajePermiso by remember { mutableStateOf<String?>(null) }

    // Abre la cámara. Devuelve true si se tomó la foto o false si se canceló
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        perfilViewModel.onFotoTomada(exito)
    }

    // Abre la galería. Devuelve el uri de la imagen o null si se canceló
    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        perfilViewModel.onFotoElegida(uri)
    }

    // Pide el permiso de cámara mientras se usa la app
    val launcherPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            mensajePermiso = null
            // Con permiso: se crea el archivo y se abre la cámara
            launcherCamara.launch(perfilViewModel.crearUriCamara())
        } else {
            mensajePermiso = "Debes permitir el uso de la cámara para tomar la foto"
        }
    }

    PerfilContenido(
        usuario = usuario,
        fotoUri = fotoUri,
        recordatorios = recordatorios,
        mensajePermiso = mensajePermiso,
        onGaleria = { launcherGaleria.launch("image/*") },
        onCamara = { launcherPermiso.launch(Manifest.permission.CAMERA) },
        onRecordatorios = { perfilViewModel.cambiarRecordatorios() },
        modifier = modifier
    )
}

// Solo dibuja la pantalla con los datos que recibe (así se puede ver en la vista previa)
@Composable
fun PerfilContenido(
    usuario: Usuario,
    fotoUri: Uri?,
    recordatorios: Boolean?,
    mensajePermiso: String?,
    onGaleria: () -> Unit,
    onCamara: () -> Unit,
    onRecordatorios: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagenInteligente(uri = fotoUri)
        Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
        Text("${usuario.rol.etiqueta} · ${usuario.correo}", color = MaterialTheme.colorScheme.primary)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onGaleria) { Text("Galería") }
            Button(onClick = onCamara) { Text("Cámara") }
        }

        // Aparece con animación si se negó el permiso
        MensajeAnimado(texto = mensajePermiso, esError = true)

        Text("Recordatorios de citas", style = MaterialTheme.typography.titleMedium)

        // Mientras se lee DataStore se muestra la ruedita de carga (guía 12)
        if (recordatorios == null) {
            CircularProgressIndicator()
        } else {
            BotonRecordatorios(activo = recordatorios, onClick = onRecordatorios)
        }
    }
}

// Botón que cambia de color con animación según la preferencia guardada (guía 12)
@Composable
fun BotonRecordatorios(activo: Boolean, onClick: () -> Unit) {
    // El color pasa de uno a otro en medio segundo
    val colorBoton by animateColorAsState(
        targetValue = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(500),
        label = ""
    )
    // El texto se recalcula solo cuando cambia "activo"
    val textoBoton by remember(activo) {
        derivedStateOf { if (activo) "Recordatorios activados" else "Recordatorios desactivados" }
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = colorBoton)
    ) {
        Icon(Icons.Default.Notifications, contentDescription = null)
        Text(textoBoton, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PerfilScreenPreview() {
    AppSanaesencia_Grupo8Theme {
        val usuario = Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE)
        PantallaBase(
            titulo = "Mi perfil",
            usuario = usuario,
            rutaActual = Screen.Perfil,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            PerfilContenido(
                usuario = usuario,
                fotoUri = null,
                recordatorios = true,
                mensajePermiso = null,
                onGaleria = {},
                onCamara = {},
                onRecordatorios = {},
                modifier = modifier
            )
        }
    }
}