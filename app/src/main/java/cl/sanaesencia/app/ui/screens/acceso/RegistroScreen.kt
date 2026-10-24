package cl.sanaesencia.app.ui.screens.acceso

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
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
import cl.sanaesencia.app.model.RegistroErrores
import cl.sanaesencia.app.model.RegistroUiState
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.CampoTexto
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.RegistroViewModel

// Pantalla de registro: lee el estado del ViewModel y le avisa lo que se escribe
@Composable
fun RegistroScreen(
    registroViewModel: RegistroViewModel,
    anchoPantalla: WindowWidthSizeClass,
    onVolverALogin: () -> Unit
) {
    val estado by registroViewModel.estado.collectAsState()

    RegistroContenido(
        estado = estado,
        anchoPantalla = anchoPantalla,
        onNombreChange = { registroViewModel.onNombreChange(it) },
        onCorreoChange = { registroViewModel.onCorreoChange(it) },
        onTelefonoChange = { registroViewModel.onTelefonoChange(it) },
        onClaveChange = { registroViewModel.onClaveChange(it) },
        onConfirmarClaveChange = { registroViewModel.onConfirmarClaveChange(it) },
        onTerminosChange = { registroViewModel.onTerminosChange() },
        onSiguiente = { registroViewModel.siguientePaso() },
        onAnterior = { registroViewModel.pasoAnterior() },
        onRegistrar = { registroViewModel.registrar() },
        // Al volver al login el formulario queda vacío
        onVolver = {
            registroViewModel.limpiar()
            onVolverALogin()
        }
    )
}

// Diseño del registro. Celular vertical: una columna. Pantalla ancha: pasos a la izquierda y formulario a la derecha (guía 9).
@Composable
fun RegistroContenido(
    estado: RegistroUiState,
    anchoPantalla: WindowWidthSizeClass,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onConfirmarClaveChange: (String) -> Unit,
    onTerminosChange: () -> Unit,
    onSiguiente: () -> Unit,
    onAnterior: () -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    if (anchoPantalla == WindowWidthSizeClass.Compact) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoRegistro(estado.paso, onAnterior, onSiguiente)
            Spacer(modifier = Modifier.height(16.dp))
            CuerpoRegistro(
                estado, onNombreChange, onCorreoChange, onTelefonoChange, onClaveChange,
                onConfirmarClaveChange, onTerminosChange, onSiguiente, onAnterior, onRegistrar, onVolver,
                Modifier.fillMaxWidth()
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EncabezadoRegistro(estado.paso, onAnterior, onSiguiente)
            CuerpoRegistro(
                estado, onNombreChange, onCorreoChange, onTelefonoChange, onClaveChange,
                onConfirmarClaveChange, onTerminosChange, onSiguiente, onAnterior, onRegistrar, onVolver,
                Modifier.width(340.dp)
            )
        }
    }
}

// Título y los dos pasos. El paso actual se ve pintado (BotonOpcion con color animado).
@Composable
fun EncabezadoRegistro(paso: Int, onPaso1: () -> Unit, onPaso2: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Paso $paso de 2", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonOpcion(texto = "1. Datos", seleccionado = paso == 1, onClick = onPaso1)
            BotonOpcion(texto = "2. Clave", seleccionado = paso == 2, onClick = onPaso2)
        }
    }
}

// Muestra el paso que corresponde, o el mensaje de éxito si ya se creó la cuenta
@Composable
fun CuerpoRegistro(
    estado: RegistroUiState,
    onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    onConfirmarClaveChange: (String) -> Unit,
    onTerminosChange: () -> Unit,
    onSiguiente: () -> Unit,
    onAnterior: () -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (estado.registroExitoso) {
            MensajeAnimado(texto = "Cuenta creada. Ya puedes iniciar sesión.", esError = false)
            Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a iniciar sesión")
            }
        } else if (estado.paso == 1) {
            // Paso 1: datos personales
            CampoTexto(estado.nombre, onNombreChange, "Nombre", Icons.Default.Person, estado.errores.nombre)
            CampoTexto(estado.correo, onCorreoChange, "Correo", Icons.Default.Email, estado.errores.correo)
            CampoTexto(estado.telefono, onTelefonoChange, "Teléfono (+569...)", Icons.Default.Phone, estado.errores.telefono)
            MensajeAnimado(texto = estado.errorGeneral, esError = true)
            Button(onClick = onSiguiente, modifier = Modifier.fillMaxWidth()) {
                Text("Siguiente")
            }
            TextButton(onClick = onVolver) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        } else {
            // Paso 2: contraseña y términos
            CampoTexto(estado.clave, onClaveChange, "Contraseña", Icons.Default.Lock, estado.errores.clave, esClave = true)
            CampoTexto(
                estado.confirmarClave, onConfirmarClaveChange, "Repetir contraseña",
                Icons.Default.Lock, estado.errores.confirmarClave, esClave = true
            )
            BotonOpcion(
                texto = "Acepto usar solo datos ficticios",
                seleccionado = estado.aceptaTerminos,
                onClick = onTerminosChange,
                modifier = Modifier.fillMaxWidth()
            )
            MensajeAnimado(texto = estado.errores.terminos, esError = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onAnterior) {
                    Text("Atrás")
                }
                Button(onClick = onRegistrar) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun RegistroPaso1Preview() {
    AppSanaesencia_Grupo8Theme {
        RegistroContenido(
            estado = RegistroUiState(
                nombre = "Jo",
                errores = RegistroErrores(
                    nombre = "Usa al menos 3 letras, sin números ni símbolos",
                    correo = "Correo no válido (ejemplo: nombre@correo.cl)",
                    telefono = "Usa el formato +569XXXXXXXX"
                )
            ),
            anchoPantalla = WindowWidthSizeClass.Compact,
            onNombreChange = {}, onCorreoChange = {}, onTelefonoChange = {}, onClaveChange = {},
            onConfirmarClaveChange = {}, onTerminosChange = {}, onSiguiente = {}, onAnterior = {},
            onRegistrar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun RegistroPaso2Preview() {
    AppSanaesencia_Grupo8Theme {
        RegistroContenido(
            estado = RegistroUiState(
                paso = 2,
                errores = RegistroErrores(
                    clave = "Debe tener al menos 8 caracteres",
                    confirmarClave = "Las contraseñas no coinciden",
                    terminos = "Debes aceptar el uso de datos ficticios"
                )
            ),
            anchoPantalla = WindowWidthSizeClass.Compact,
            onNombreChange = {}, onCorreoChange = {}, onTelefonoChange = {}, onClaveChange = {},
            onConfirmarClaveChange = {}, onTerminosChange = {}, onSiguiente = {}, onAnterior = {},
            onRegistrar = {}, onVolver = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun RegistroMediumPreview() {
    AppSanaesencia_Grupo8Theme {
        RegistroContenido(
            estado = RegistroUiState(),
            anchoPantalla = WindowWidthSizeClass.Medium,
            onNombreChange = {}, onCorreoChange = {}, onTelefonoChange = {}, onClaveChange = {},
            onConfirmarClaveChange = {}, onTerminosChange = {}, onSiguiente = {}, onAnterior = {},
            onRegistrar = {}, onVolver = {}
        )
    }
}