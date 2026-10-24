package cl.sanaesencia.app.logic

import cl.sanaesencia.app.model.Cita
import java.time.LocalDate
import java.time.LocalDateTime

// Todo lo que tiene que ver con fechas y horas de las citas (sin nada de pantalla).
// Las fechas se guardan como texto "dd/MM/yyyy" y las horas como "HH:mm".
object Fechas {

    // Se puede agendar hasta 60 días hacia adelante
    const val DIAS_MAXIMOS = 60

    // Horas en punto de atención: 10 bloques al día
    val HORAS = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")

    private val nombresDia = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    // LocalDate -> "dd/MM/yyyy"
    fun formatear(fecha: LocalDate): String =
        String.format("%02d/%02d/%04d", fecha.dayOfMonth, fecha.monthValue, fecha.year)

    // "dd/MM/yyyy" -> LocalDate. Devuelve null si el texto no es una fecha real (ej. 31/02/2026)
    fun parsear(texto: String): LocalDate? {
        val partes = texto.trim().split("/")
        if (partes.size != 3) return null
        val dia = partes[0].toIntOrNull()
        val mes = partes[1].toIntOrNull()
        val anio = partes[2].toIntOrNull()
        if (dia == null || mes == null || anio == null) return null
        return try {
            LocalDate.of(anio, mes, dia)
        } catch (e: Exception) {
            null
        }
    }

    // Lunes a sábado (el domingo vale 7)
    fun esDiaHabil(fecha: LocalDate): Boolean = fecha.dayOfWeek.value != 7

    // Desde hoy hasta 60 días adelante, de lunes a sábado
    fun fechaValida(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): Boolean =
        esDiaHabil(fecha) && !fecha.isBefore(hoy) && !fecha.isAfter(hoy.plusDays(DIAS_MAXIMOS.toLong()))

    // Lista de días en que se puede agendar (hoy + 60 días, sin domingos)
    fun fechasDisponibles(hoy: LocalDate = LocalDate.now()): List<LocalDate> {
        return (0..DIAS_MAXIMOS).map { hoy.plusDays(it.toLong()) }.filter { esDiaHabil(it) }
    }

    // Para los botones: "Lun 26"
    fun diaCorto(fecha: LocalDate): String =
        nombresDia[fecha.dayOfWeek.value - 1] + " " + fecha.dayOfMonth

    // Para los títulos: "26/10"
    fun diaMes(fecha: LocalDate): String =
        String.format("%02d/%02d", fecha.dayOfMonth, fecha.monthValue)

    // Horas de un día. Si el día es hoy, solo las que aún no pasan (hora siguiente o posterior)
    fun horasDelDia(fecha: LocalDate, ahora: LocalDateTime = LocalDateTime.now()): List<String> {
        if (fecha != ahora.toLocalDate()) return HORAS
        return HORAS.filter { hora -> (hora.split(":")[0].toIntOrNull() ?: 0) > ahora.hour }
    }

    // Texto para ordenar citas por fecha y hora, ej. "2026-10-26 10:00"
    fun claveOrden(cita: Cita): String = (parsear(cita.fecha)?.toString() ?: "") + " " + cita.hora
}
