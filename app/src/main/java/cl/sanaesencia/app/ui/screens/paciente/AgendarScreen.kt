package cl.sanaesencia.app.ui.screens.paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.model.AgendaUiState
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.Usuario
import cl.sanaesencia.app.navigation.Screen
import cl.sanaesencia.app.ui.components.BotonOpcion
import cl.sanaesencia.app.ui.components.CampoTexto
import cl.sanaesencia.app.ui.components.MensajeAnimado
import cl.sanaesencia.app.ui.components.Paginador
import cl.sanaesencia.app.viewmodel.AgendaViewModel
import cl.sanaesencia.app.viewmodel.MainViewModel

// Cuántas fechas se ven por página en el paso 2 (3 por fila). Si no cabe en el celular, bajar a 3.
private const val FECHAS_POR_PAGINA = 6

// Formulario para agendar una hora, en 4 pasos (guía 11). Toda la validación está en el ViewModel.
@Composable
fun AgendarScreen(
    agendaViewModel: AgendaViewModel,
    usuario: Usuario,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val estado by agendaViewModel.estado.collectAsState()
    // Se lee aquí para que la pantalla se redibuje si cambian las horas ocupadas
    val citas by agendaViewModel.citas.collectAsState()
    val confirmada = estado.citaConfirmada

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (confirmada != null) {
            CitaAgendada(confirmada, agendaViewModel, mainViewModel)
        } else {
            Text(
                "Paso ${estado.paso} de 4: ${tituloPaso(estado.paso)}",
                style = MaterialTheme.typography.titleMedium
            )
            when (estado.paso) {
                1 -> PasoServicio(estado, agendaViewModel)
                2 -> PasoProfesionalYFecha(estado, agendaViewModel)
                3 -> PasoHoraYMotivo(estado, agendaViewModel, citas)
                else -> PasoConfirmar(estado, agendaViewModel)
            }
            MensajeAnimado(texto = estado.errorGeneral, esError = true)
            BotonesPaso(estado, agendaViewModel, usuario)
        }
    }
}

private fun tituloPaso(paso: Int): String = when (paso) {
    1 -> "Servicio"
    2 -> "Profesional y fecha"
    3 -> "Hora y motivo"
    else -> "Confirmar"
}

// Mensaje rojo bajo un grupo de botones cuando falta elegir algo
@Composable
private fun TextoError(mensaje: String?) {
    mensaje?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
    }
}

// Paso 1: lista de servicios, de 3 en 3
@Composable
private fun PasoServicio(estado: AgendaUiState, agendaViewModel: AgendaViewModel) {
    var pagina by remember { mutableStateOf(0) }
    val paginas = agendaViewModel.servicios().chunked(3)

    paginas[pagina].forEach { servicio ->
        BotonOpcion(
            texto = servicio.nombre,
            seleccionado = servicio.id == estado.servicioId,
            onClick = { agendaViewModel.elegirServicio(servicio.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    Paginador(
        pagina = pagina,
        totalPaginas = paginas.size,
        onAnterior = { pagina = pagina - 1 },
        onSiguiente = { pagina = pagina + 1 }
    )
    TextoError(estado.errores.servicio)
}

// Paso 2: profesional, modalidad y fecha
@Composable
private fun PasoProfesionalYFecha(estado: AgendaUiState, agendaViewModel: AgendaViewModel) {
    // Las fechas se calculan una sola vez y se reparten en páginas
    val paginasFechas = remember { Fechas.fechasDisponibles().chunked(FECHAS_POR_PAGINA) }
    val profesionales = agendaViewModel.profesionalesDisponibles()
    val modalidades = agendaViewModel.modalidadesPosibles()

    Text("Profesional", style = MaterialTheme.typography.labelLarge)
    if (profesionales.isEmpty()) {
        Text("Este servicio no tiene profesionales con agenda abierta")
    }
    profesionales.forEach { terapeuta ->
        BotonOpcion(
            texto = terapeuta.nombre,
            seleccionado = terapeuta.id == estado.terapeutaId,
            onClick = { agendaViewModel.elegirProfesional(terapeuta.id) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    TextoError(estado.errores.profesional)

    Text("Modalidad", style = MaterialTheme.typography.labelLarge)
    if (modalidades.isEmpty()) {
        Text("Elige primero un profesional")
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modalidades.forEach { modalidad ->
                BotonOpcion(
                    texto = modalidad.etiqueta,
                    seleccionado = modalidad == estado.modalidad,
                    onClick = { agendaViewModel.elegirModalidad(modalidad) }
                )
            }
        }
    }
    TextoError(estado.errores.modalidad)

    // Fechas de la página actual, 3 por fila
    val semana = paginasFechas[estado.paginaFechas]
    Text(
        "Fecha (del ${Fechas.diaMes(semana[0])} al ${Fechas.diaMes(semana[semana.size - 1])})",
        style = MaterialTheme.typography.labelLarge
    )
    semana.chunked(3).forEach { fila ->
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            fila.forEach { dia ->
                val texto = Fechas.formatear(dia)
                BotonOpcion(
                    texto = Fechas.diaCorto(dia),
                    seleccionado = texto == estado.fecha,
                    onClick = { agendaViewModel.elegirFecha(texto) }
                )
            }
        }
    }
    Paginador(
        pagina = estado.paginaFechas,
        totalPaginas = paginasFechas.size,
        onAnterior = { agendaViewModel.cambiarPaginaFechas(-1) },
        onSiguiente = { agendaViewModel.cambiarPaginaFechas(1) }
    )
    TextoError(estado.errores.fecha)
}

// Paso 3: horas libres del día elegido y motivo de la consulta
@Composable
private fun PasoHoraYMotivo(estado: AgendaUiState, agendaViewModel: AgendaViewModel, citas: List<Cita>) {
    val horas = agendaViewModel.horasLibres(citas)

    Text("Horas libres del ${estado.fecha}", style = MaterialTheme.typography.labelLarge)
    if (horas.isEmpty()) {
        Text("No quedan horas libres ese día. Vuelve atrás y elige otra fecha.")
    }
    horas.chunked(3).forEach { fila ->
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            fila.forEach { hora ->
                BotonOpcion(
                    texto = hora,
                    seleccionado = hora == estado.hora,
                    onClick = { agendaViewModel.elegirHora(hora) }
                )
            }
        }
    }
    TextoError(estado.errores.hora)

    CampoTexto(
        valor = estado.motivo,
        onValorChange = { agendaViewModel.onMotivoChange(it) },
        etiqueta = "Motivo de la consulta",
        icono = Icons.Default.Edit,
        error = estado.errores.motivo
    )
}

// Paso 4: resumen antes de guardar
@Composable
private fun PasoConfirmar(estado: AgendaUiState, agendaViewModel: AgendaViewModel) {
    val servicio = agendaViewModel.servicioElegido()
    val terapeuta = agendaViewModel.terapeutaElegido()

    if (servicio != null && terapeuta != null) {
        Text(servicio.nombre, style = MaterialTheme.typography.titleLarge)
        Text("Profesional: ${terapeuta.nombre}")
        Text("Modalidad: ${estado.modalidad?.etiqueta ?: ""}")
        Text("Fecha: ${estado.fecha} a las ${estado.hora}")
        Text("Motivo: ${estado.motivo.trim()}")
        Text(
            "Valor: $" + String.format("%,d", servicio.precio),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

// Botones de abajo: Atrás y Siguiente (en el paso 4, Confirmar)
@Composable
private fun BotonesPaso(estado: AgendaUiState, agendaViewModel: AgendaViewModel, usuario: Usuario) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (estado.paso > 1) {
            Button(onClick = { agendaViewModel.anterior() }) { Text("Atrás") }
        } else {
            // Espacio vacío para que "Siguiente" quede a la derecha
            Spacer(Modifier.width(1.dp))
        }
        if (estado.paso < 4) {
            Button(onClick = { agendaViewModel.siguiente() }) { Text("Siguiente") }
        } else {
            Button(onClick = { agendaViewModel.confirmar(usuario) }) { Text("Confirmar cita") }
        }
    }
}

// Pantalla final cuando la cita ya quedó guardada
@Composable
private fun CitaAgendada(cita: Cita, agendaViewModel: AgendaViewModel, mainViewModel: MainViewModel) {
    MensajeAnimado(texto = "Tu cita quedó agendada", esError = false)
    Text(agendaViewModel.nombreServicio(cita.servicioId), style = MaterialTheme.typography.titleLarge)
    Text("${cita.fecha} a las ${cita.hora}")
    Text("Profesional: ${agendaViewModel.nombreTerapeuta(cita.terapeutaId)}")
    Text(if (cita.box == 0) "Atención online" else "Atención presencial en el box ${cita.box}")
    Button(onClick = {
        agendaViewModel.reiniciar()
        mainViewModel.navigateTo(Screen.MisCitas, popUpToRoute = Screen.Inicio, singleTop = true)
    }) {
        Text("Ver mis citas")
    }
    Button(onClick = { agendaViewModel.reiniciar() }) {
        Text("Agendar otra hora")
    }
}
