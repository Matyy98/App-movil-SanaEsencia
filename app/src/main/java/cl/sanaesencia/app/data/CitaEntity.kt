package cl.sanaesencia.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad

// Tabla "citas" de la base de datos (Room). Guarda las horas agendadas por los pacientes.
@Entity(tableName = "citas")
data class CitaEntity(
    // Room pone el número solo (1, 2, 3...)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correoPaciente: String,
    val nombrePaciente: String,
    val servicioId: Int,
    val terapeutaId: Int,
    // Fecha "dd/MM/yyyy" y hora "HH:mm"
    val fecha: String,
    val hora: String,
    val modalidad: Modalidad,
    // Box del 1 al 8. Vale 0 si la cita es online.
    val box: Int,
    val estado: EstadoCita
)
