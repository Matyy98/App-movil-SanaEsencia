package cl.sanaesencia.app.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Foto de perfil circular. Si no hay foto (o no se pudo leer) muestra un ícono (guía 13)
@Composable
fun ImagenInteligente(uri: Uri?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Lee la foto solo cuando cambia el uri, no cada vez que se redibuja la pantalla
    val imagen = remember(uri) { cargarImagen(context, uri) }

    if (imagen != null) {
        Image(
            bitmap = imagen,
            contentDescription = "Foto de perfil",
            // Recorta la foto para que llene el círculo sin deformarse
            contentScale = ContentScale.Crop,
            modifier = modifier
                .height(120.dp)
                .width(120.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "Sin foto de perfil",
            tint = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .height(120.dp)
                .width(120.dp)
        )
    }
}

// Convierte el uri en una imagen. Devuelve null si no hay foto o si falla la lectura
private fun cargarImagen(context: Context, uri: Uri?): ImageBitmap? {
    if (uri == null) return null
    return try {
        // Abre el archivo de la foto y lo transforma en imagen
        val entrada = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(entrada)
        // Cierra el archivo después de leerlo
        entrada?.close()
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        // Si la foto ya no existe o no hay permiso para leerla, se muestra el ícono
        null
    }
}

@Preview(showBackground = true)
@Composable
fun ImagenInteligentePreview() {
    AppSanaesencia_Grupo8Theme {
        // Sin foto: debe mostrar el ícono de perfil
        ImagenInteligente(uri = null)
    }
}