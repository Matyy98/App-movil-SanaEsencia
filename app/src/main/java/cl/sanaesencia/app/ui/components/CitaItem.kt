package cl.sanaesencia.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.ui.theme.AppSanaesencia_Grupo8Theme

// Muestra los datos de una cita. El estado va a la derecha y cambia de color según cuál sea.
@Composable
fun CitaItem(cita: Cita, servicio: String, profesional: String, modifier: Modifier = Modifier) {
    val colorEstado = when (cita.estado) {
        EstadoCita.AGENDADA -> MaterialTheme.colorScheme.secondary
        EstadoCita.COMPLETADA -> MaterialTheme.colorScheme.primary
        EstadoCita.CANCELADA -> MaterialTheme.colorScheme.error
    }
    // Presencial dice el box; online no usa box
    val lugar = if (cita.box == 0) cita.modalidad.etiqueta else "${cita.modalidad.etiqueta} · Box ${cita.box}"

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(servicio, style = MaterialTheme.typography.titleMedium)
            Text(cita.estado.etiqueta, color = colorEstado, style = MaterialTheme.typography.labelLarge)
        }
        Text("${cita.fecha} a las ${cita.hora}", style = MaterialTheme.typography.bodyMedium)
        Text("Con $profesional", style = MaterialTheme.typography.bodyMedium)
        Text(lugar, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun CitaItemPreview() {
    AppSanaesencia_Grupo8Theme {
        CitaItem(
            cita = Cita(1, "paciente@sanaesencia.cl", "Paciente Demo", 1, 2, "26/10/2026", "10:00", Modalidad.PRESENCIAL, 3, EstadoCita.AGENDADA),
            servicio = "Psicoterapia adultos",
            profesional = "Ps. Matías Soto"
        )
    }
}
