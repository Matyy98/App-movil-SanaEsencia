package cl.sanaesencia.app.logic

import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.OcupacionBox
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.model.Terapeuta

// Reglas de negocio de la agenda: boxes, profesional ocupado y ocupación (sin nada de pantalla).
object GestorBoxes {

    // El centro tiene 8 boxes y 10 bloques de atención al día
    const val BLOQUES_POR_DIA = 10
    private val boxes = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    // Una cita cancelada libera la hora; las demás la ocupan
    private fun ocupa(cita: Cita): Boolean = cita.estado != EstadoCita.CANCELADA

    // true si el profesional ya tiene una cita a esa fecha y hora
    fun profesionalOcupado(citas: List<Cita>, terapeutaId: Int, fecha: String, hora: String): Boolean {
        val coincidencias = citas.count {
            ocupa(it) && it.terapeutaId == terapeutaId && it.fecha == fecha && it.hora == hora
        }
        return coincidencias > 0
    }

    // Primer box libre a esa fecha y hora (1 a 8). Devuelve null si están todos ocupados.
    fun boxLibre(citas: List<Cita>, fecha: String, hora: String): Int? {
        return boxes.firstOrNull { box ->
            citas.count { ocupa(it) && it.box == box && it.fecha == fecha && it.hora == hora } == 0
        }
    }

    // Se puede agendar si el profesional está libre y, si es presencial, hay un box libre
    fun horaDisponible(citas: List<Cita>, terapeutaId: Int, fecha: String, hora: String, modalidad: Modalidad): Boolean {
        if (profesionalOcupado(citas, terapeutaId, fecha, hora)) return false
        if (modalidad == Modalidad.PRESENCIAL) return boxLibre(citas, fecha, hora) != null
        return true
    }

    // Box que se le asigna a la cita: uno libre si es presencial, 0 si es online
    fun asignarBox(citas: List<Cita>, fecha: String, hora: String, modalidad: Modalidad): Int {
        if (modalidad != Modalidad.PRESENCIAL) return 0
        return boxLibre(citas, fecha, hora) ?: 0
    }

    // Modalidades que se pueden elegir: las que aceptan a la vez el servicio y el profesional
    fun modalidadesPosibles(servicio: Servicio, terapeuta: Terapeuta): List<Modalidad> {
        return listOf(Modalidad.PRESENCIAL, Modalidad.ONLINE).filter { modalidad ->
            (servicio.modalidad == Modalidad.AMBAS || servicio.modalidad == modalidad) &&
                (terapeuta.modalidad == Modalidad.AMBAS || terapeuta.modalidad == modalidad)
        }
    }

    // Ocupación de cada box en un día = citas del día / 10 bloques (para el panel de Administración)
    fun ocupacionPorBox(citas: List<Cita>, fecha: String): List<OcupacionBox> {
        return boxes.map { box ->
            val cantidad = citas.count { ocupa(it) && it.box == box && it.fecha == fecha }
            OcupacionBox(box, cantidad, cantidad * 100 / BLOQUES_POR_DIA)
        }
    }

    // Promedio de ocupación de los boxes (en %)
    fun promedioOcupacion(ocupaciones: List<OcupacionBox>): Int {
        if (ocupaciones.isEmpty()) return 0
        return ocupaciones.sumOf { it.porcentaje } / ocupaciones.size
    }

    // Bajo 40% de promedio = baja demanda (se sugiere hacer una promoción)
    fun esBajaDemanda(promedio: Int): Boolean = promedio < 40
}
