package cl.sanaesencia.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.model.Promocion
import cl.sanaesencia.app.model.PromocionErrores
import cl.sanaesencia.app.model.PromocionUiState
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.CampoTexto
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.ui.components.PantallaBase
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme
import cl.sanaesencia.app.viewmodel.AdminViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Promociones para baja demanda: lista o formulario según el estado (guía 11)
@Composable
fun PromocionesScreen(adminViewModel: AdminViewModel, modifier: Modifier = Modifier) {
    val promociones by adminViewModel.promociones.collectAsState()
    val formulario by adminViewModel.formulario.collectAsState()

    if (formulario.mostrarFormulario) {
        PromocionFormulario(
            estado = formulario,
            onTituloChange = { adminViewModel.onTituloChange(it) },
            onDescripcionChange = { adminViewModel.onDescripcionChange(it) },
            onDescuentoChange = { adminViewModel.onDescuentoChange(it) },
            onSegmentoChange = { adminViewModel.onSegmentoChange(it) },
            onCancelar = { adminViewModel.cerrarFormulario() },
            onGuardar = { adminViewModel.guardarPromocion() },
            modifier = modifier
        )
    } else {
        PromocionLista(
            promociones = promociones,
            mensajeExito = formulario.mensajeExito,
            onNueva = { adminViewModel.abrirFormulario() },
            modifier = modifier
        )
    }
}

// Lista de promociones guardadas, de 3 en 3
@Composable
fun PromocionLista(
    promociones: List<Promocion>,
    mensajeExito: String?,
    onNueva: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pagina by remember { mutableStateOf(0) }
    val paginas = promociones.chunked(3)
    // Si la página ya no existe (por ejemplo cambió la lista), vuelve a la primera
    val paginaActual = if (pagina < paginas.size) pagina else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Promociones activas", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onNueva) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Nueva")
            }
        }

        // Aparece con animación después de guardar
        MensajeAnimado(texto = mensajeExito, esError = false)

        if (paginas.isEmpty()) {
            Text("Aún no hay promociones. Crea la primera con el botón Nueva.")
        } else {
            paginas[paginaActual].forEach { promocion ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(promocion.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${promocion.descuento}% de descuento · ${promocion.segmento}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(promocion.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Paginador(
                pagina = paginaActual,
                totalPaginas = paginas.size,
                onAnterior = { pagina = paginaActual - 1 },
                onSiguiente = { pagina = paginaActual + 1 }
            )
        }
    }
}

// Formulario para crear una promoción, con error por campo (guía 11)
@Composable
fun PromocionFormulario(
    estado: PromocionUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onDescuentoChange: (String) -> Unit,
    onSegmentoChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Nueva promoción", style = MaterialTheme.typography.titleLarge)

        CampoTexto(
            valor = estado.titulo,
            onValorChange = onTituloChange,
            etiqueta = "Título",
            icono = Icons.Default.Star,
            error = estado.errores.titulo
        )
        CampoTexto(
            valor = estado.descripcion,
            onValorChange = onDescripcionChange,
            etiqueta = "Descripción",
            icono = Icons.Default.Edit,
            error = estado.errores.descripcion
        )
        CampoTexto(
            valor = estado.descuento,
            onValorChange = onDescuentoChange,
            etiqueta = "Descuento (%)",
            icono = Icons.Default.Info,
            error = estado.errores.descuento
        )

        Text("Dirigida a", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Un botón por segmento; el elegido cambia de color con animación
            PromocionUiState.SEGMENTOS.forEach { segmento ->
                BotonOpcion(
                    texto = segmento,
                    seleccionado = estado.segmento == segmento,
                    onClick = { onSegmentoChange(segmento) }
                )
            }
        }
        // Error del segmento con ícono (aparece animado)
        MensajeAnimado(texto = estado.errores.segmento, esError = true)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancelar) { Text("Cancelar") }
            Button(onClick = onGuardar) { Text("Guardar") }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PromocionListaPreview() {
    AppSanaesencia_Grupo8Theme {
        PantallaBase(
            titulo = "Promociones",
            usuario = Usuario("Admin Demo", "admin@sanaesencia.cl", "Admin1234", "+56933333333", Rol.ADMIN),
            rutaActual = Screen.Promociones,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            PromocionLista(
                promociones = listOf(
                    Promocion(1, "Vuelve a tu terapia", "Descuento para pacientes que no vienen hace un mes.", 20, "Inactivos"),
                    Promocion(2, "Primera sesión", "Descuento en la primera evaluación del neurodesarrollo.", 15, "Nuevos")
                ),
                mensajeExito = "Promoción \"Primera sesión\" guardada",
                onNueva = {},
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PromocionFormularioPreview() {
    AppSanaesencia_Grupo8Theme {
        PantallaBase(
            titulo = "Promociones",
            usuario = Usuario("Admin Demo", "admin@sanaesencia.cl", "Admin1234", "+56933333333", Rol.ADMIN),
            rutaActual = Screen.Promociones,
            mainViewModel = MainViewModel(),
            onCerrarSesion = {}
        ) { modifier ->
            // Formulario con errores para ver cómo se muestran
            PromocionFormulario(
                estado = PromocionUiState(
                    titulo = "Pro",
                    descuento = "80",
                    segmento = "Inactivos",
                    mostrarFormulario = true,
                    errores = PromocionErrores(
                        titulo = "El título debe tener al menos 5 caracteres",
                        descripcion = "Ingresa una descripción",
                        descuento = "El descuento debe estar entre 5% y 50%"
                    )
                ),
                onTituloChange = {},
                onDescripcionChange = {},
                onDescuentoChange = {},
                onSegmentoChange = {},
                onCancelar = {},
                onGuardar = {},
                modifier = modifier
            )
        }
    }
}