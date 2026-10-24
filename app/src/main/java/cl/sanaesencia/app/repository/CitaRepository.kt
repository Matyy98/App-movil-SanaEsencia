package cl.sanaesencia.app.repository

import cl.sanaesencia.app.data.CitaDao
import cl.sanaesencia.app.data.CitaEntity
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Intermediario entre los ViewModels y la tabla de citas.
// Los ViewModels trabajan con Cita (model) y no saben que por debajo está Room.
class CitaRepository(private val dao: CitaDao) {

    // Convierte una fila de la tabla (Entity) en el modelo Cita
    private fun aCita(fila: CitaEntity): Cita {
        return Cita(
            fila.id, fila.correoPaciente, fila.nombrePaciente, fila.servicioId, fila.terapeutaId,
            fila.fecha, fila.hora, fila.modalidad, fila.box, fila.estado
        )
    }

    // Todas las citas del centro
    fun obtenerCitas(): Flow<List<Cita>> {
        return dao.obtenerTodas().map { filas -> filas.map { fila -> aCita(fila) } }
    }

    // Solo las citas de un profesional (la usa la agenda del terapeuta)
    fun obtenerPorTerapeuta(terapeutaId: Int): Flow<List<Cita>> {
        return obtenerCitas().map { citas -> citas.filter { it.terapeutaId == terapeutaId } }
    }

    // Guarda una cita nueva (el id lo pone Room, por eso no se usa el de la cita)
    suspend fun agregar(cita: Cita) {
        dao.insertar(
            CitaEntity(
                correoPaciente = cita.correoPaciente,
                nombrePaciente = cita.nombrePaciente,
                servicioId = cita.servicioId,
                terapeutaId = cita.terapeutaId,
                fecha = cita.fecha,
                hora = cita.hora,
                modalidad = cita.modalidad,
                box = cita.box,
                estado = cita.estado
            )
        )
    }

    // Cambia el estado de una cita (cancelar o marcar como completada)
    suspend fun actualizarEstado(id: Int, estado: EstadoCita) {
        dao.actualizarEstado(id, estado.name)
    }
}
