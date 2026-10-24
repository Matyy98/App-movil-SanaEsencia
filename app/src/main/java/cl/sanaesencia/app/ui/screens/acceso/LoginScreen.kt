package cl.sanaesencia.app.ui.screens.acceso

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.LoginErrores
import cl.sanaesencia.app.model.LoginUiState
import cl.sanaesencia.app.ui.components.CampoTexto
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.SesionViewModel

// Pantalla de login: lee el estado del ViewModel y le avisa lo que se escribe
@Composable
fun LoginScreen(
    sesionViewModel: SesionViewModel,
    anchoPantalla: WindowWidthSizeClass,
    onIrARegistro: () -> Unit
) {
    val estado by sesionViewModel.estado.collectAsState()

    LoginContenido(
        estado = estado,
        anchoPantalla = anchoPantalla,
        onCorreoChange = { sesionViewModel.onCorreoChange(it) },
        onClaveChange = { sesionViewModel.onClaveChange(it) },
        onEntrar = { sesionViewModel.iniciarSesion() },
        onIrARegistro = onIrARegistro
    )
}

// Diseño del login. Se separa del ViewModel para poder verlo en la Preview.
// Celular vertical: todo en una columna. Pantalla ancha: logo a la izquierda y formulario a la derecha (guía 9).
@Composable
fun LoginContenido(
    estado: LoginUiState,
    anchoPantalla: WindowWidthSizeClass,
    onCorreoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onEntrar: () -> Unit,
    onIrARegistro: () -> Unit
) {
    if (anchoPantalla == WindowWidthSizeClass.Compact) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoLogin()
            Spacer(modifier = Modifier.height(24.dp))
            FormularioLogin(estado, onCorreoChange, onClaveChange, onEntrar, onIrARegistro, Modifier.fillMaxWidth())
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EncabezadoLogin()
            FormularioLogin(estado, onCorreoChange, onClaveChange, onEntrar, onIrARegistro, Modifier.width(340.dp))
        }
    }
}

// Logo, nombre del centro y cuentas de prueba
@Composable
fun EncabezadoLogin() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(56.dp)
        )
        Text("Sanaesencia", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Centro Integrativo", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Cuenta de prueba: paciente@sanaesencia.cl / Paciente123", style = MaterialTheme.typography.bodySmall)
    }
}

// Campos de correo y clave, mensaje de error y botones
@Composable
fun FormularioLogin(
    estado: LoginUiState,
    onCorreoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onEntrar: () -> Unit,
    onIrARegistro: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CampoTexto(
            valor = estado.correo,
            onValorChange = onCorreoChange,
            etiqueta = "Correo",
            icono = Icons.Default.Email,
            error = estado.errores.correo
        )
        CampoTexto(
            valor = estado.clave,
            onValorChange = onClaveChange,
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock,
            error = estado.errores.clave,
            esClave = true
        )
        // Aparece con animación si el correo o la clave no coinciden
        MensajeAnimado(texto = estado.errorGeneral, esError = true)
        Button(
            onClick = onEntrar,
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mientras se revisan los datos se muestra la ruedita
            if (estado.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Entrar")
            }
        }
        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginCompactPreview() {
    AppSanaesencia_Grupo8Theme {
        LoginContenido(
            estado = LoginUiState(
                correo = "paciente@",
                errorGeneral = "Correo o contraseña incorrectos",
                errores = LoginErrores(correo = "Correo no válido (ejemplo: nombre@correo.cl)", clave = "Ingresa tu contraseña")
            ),
            anchoPantalla = WindowWidthSizeClass.Compact,
            onCorreoChange = {},
            onClaveChange = {},
            onEntrar = {},
            onIrARegistro = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun LoginMediumPreview() {
    AppSanaesencia_Grupo8Theme {
        LoginContenido(
            estado = LoginUiState(),
            anchoPantalla = WindowWidthSizeClass.Medium,
            onCorreoChange = {},
            onClaveChange = {},
            onEntrar = {},
            onIrARegistro = {}
        )
    }
}