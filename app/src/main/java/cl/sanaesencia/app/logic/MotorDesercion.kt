package cl.sanaesencia.app.logic

import cl.sanaesencia.app.model.AlertaDesercion
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.NivelRiesgo
import java.time.Duration
import java.time.LocalDate

// Detecta pacientes que podrían abandonar el tratamiento (sin nada de pantalla).
// Regla del equipo:
//  ALTO  = menos de 4 sesiones completadas, más de 21 días sin sesión y sin cita futura
//  MEDIO = más de 14 días sin sesión y sin cita futura
//  BAJO  = en otro caso
object MotorDesercion {

    const val SESIONES_MINIMAS = 4
    const val DIAS_ALTO = 21
    const val DIAS_MEDIO = 14

    // Aplica la regla con los datos ya calculados
    fun nivel(sesionesCompletadas: Int, diasSinSesion: Int, tieneCitaFutura: Boolean): NivelRiesgo = when {
        sesionesCompletadas < SESIONES_MINIMAS && diasSinSesion > DIAS_ALTO && !tieneCitaFutura -> NivelRiesgo.ALTO
        diasSinSesion > DIAS_MEDIO && !tieneCitaFutura -> NivelRiesgo.MEDIO
        else -> NivelRiesgo.BAJO
    }

    // Qué hacer con el paciente según su riesgo
    fun recomendacion(nivel: NivelRiesgo): String = when (nivel) {
        NivelRiesgo.ALTO -> "Contactar hoy y ofrecer una promoción para retomar"
        NivelRiesgo.MEDIO -> "Enviar recordatorio para agendar la próxima sesión"
        NivelRiesgo.BAJO -> "Continuidad normal, no requiere acción"
    }

    // Días que hay entre dos fechas (Duration, Experiencia 1)
    fun diasEntre(desde: LocalDate, hasta: LocalDate): Int =
        Duration.between(desde.atStartOfDay(), hasta.atStartOfDay()).toDays().toInt()

    // true si la cita está agendada para hoy o después
    private fun esFutura(cita: Cita, hoy: LocalDate): Boolean {
        val fecha = Fechas.parsear(cita.fecha) ?: return false
        return cita.estado == EstadoCita.AGENDADA && !fecha.isBefore(hoy)
    }

    // true si la cita ya pasó (hoy o antes)
    private fun yaPaso(cita: Cita, hoy: LocalDate): Boolean {
        val fecha = Fechas.parsear(cita.fecha) ?: return false
        return !fecha.isAfter(hoy)
    }

    // Calcula la alerta de un paciente a partir de todas sus citas
    fun alertaDe(citas: List<Cita>, correo: String, hoy: LocalDate = LocalDate.now()): AlertaDesercion {
        val delPaciente = citas.filter { it.correoPaciente == correo }
        val completadas = delPaciente.filter { it.estado == EstadoCita.COMPLETADA }
        val tieneCitaFutura = delPaciente.count { esFutura(it, hoy) } > 0

        // Se cuenta desde la última sesión completada.
        // Si nunca completó una, desde su última cita que ya pasó.
        val ultimaCompletada = completadas.maxByOrNull { Fechas.claveOrden(it) }
        val referencia = ultimaCompletada ?: delPaciente.filter { yaPaso(it, hoy) }.maxByOrNull { Fechas.claveOrden(it) }
        val fechaReferencia = if (referencia == null) null else Fechas.parsear(referencia.fecha)
        val dias = if (fechaReferencia == null) 0 else diasEntre(fechaReferencia, hoy)

        val nivel = nivel(completadas.size, dias, tieneCitaFutura)
        return AlertaDesercion(
            nombrePaciente = delPaciente.firstOrNull()?.nombrePaciente ?: correo,
            correoPaciente = correo,
            sesionesCompletadas = completadas.size,
            diasSinSesion = dias,
            tieneCitaFutura = tieneCitaFutura,
            nivel = nivel,
            recomendacion = recomendacion(nivel)
        )
    }

    // Para ordenar: primero ALTO, después MEDIO y al final BAJO
    private fun orden(nivel: NivelRiesgo): Int = when (nivel) {
        NivelRiesgo.ALTO -> 0
        NivelRiesgo.MEDIO -> 1
        NivelRiesgo.BAJO -> 2
    }

    // Una alerta por paciente, los de más riesgo primero
    fun alertas(citas: List<Cita>, hoy: LocalDate = LocalDate.now()): List<AlertaDesercion> {
        // Correos sin repetir (cada paciente una sola vez)
        val correos = mutableListOf<String>()
        citas.forEach { cita ->
            if (!correos.contains(cita.correoPaciente)) {
                correos.add(cita.correoPaciente)
            }
        }
        return correos
            .map { alertaDe(citas, it, hoy) }
            // Primero los que llevan más días sin venir...
            .sortedBy { it.diasSinSesion }
            .reversed()
            // ...y después se agrupan por nivel (sortedBy mantiene el orden anterior dentro de cada nivel)
            .sortedBy { orden(it.nivel) }
    }

    // Alertas solo de los pacientes de un profesional (para el terapeuta)
    fun alertasDeTerapeuta(citas: List<Cita>, terapeutaId: Int, hoy: LocalDate = LocalDate.now()): List<AlertaDesercion> =
        alertas(citas.filter { it.terapeutaId == terapeutaId }, hoy)

    // Cuántos pacientes hay en un nivel
    fun contar(alertas: List<AlertaDesercion>, nivel: NivelRiesgo): Int = alertas.count { it.nivel == nivel }
}