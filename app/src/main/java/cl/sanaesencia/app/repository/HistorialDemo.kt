package cl.sanaesencia.app.repository

import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad
import java.time.LocalDate

// Historial ficticio de sesiones pasadas (pacientes inventados, Ley 19.628).
// En la app solo se agendan fechas futuras, así que sin esto el terapeuta y
// Administración no tendrían pacientes antiguos para calcular la deserción.
// Las fechas se cuentan hacia atrás desde hoy, así el resultado siempre es el mismo.
object HistorialDemo {

    // Arma una cita del historial. box 0 = online
    private fun cita(id: Int, correo: String, nombre: String, servicioId: Int, terapeutaId: Int,
                     hoy: LocalDate, diasAtras: Int, hora: String, box: Int, estado: EstadoCita): Cita {
        val modalidad = if (box == 0) Modalidad.ONLINE else Modalidad.PRESENCIAL
        val fecha = hoy.minusDays(diasAtras.toLong())
        // El centro no atiende los domingos: si cae en domingo, la sesión fue el sábado
        val fechaHabil = if (Fechas.esDiaHabil(fecha)) fecha else fecha.minusDays(1)
        return Cita(id, correo, nombre, servicioId, terapeutaId, Fechas.formatear(fechaHabil), hora, modalidad, box, estado)
    }

    // Los id son negativos para que nunca choquen con los que pone Room
    fun citas(hoy: LocalDate = LocalDate.now()): List<Cita> {
        val c = EstadoCita.COMPLETADA
        return listOf(
            // Sofía (Ps. Matías Soto): 2 sesiones, la última hace 30 días -> riesgo ALTO
            cita(-1, "sofia.perez@correo.cl", "Sofía Pérez", 2, 2, hoy, 44, "10:00", 1, c),
            cita(-2, "sofia.perez@correo.cl", "Sofía Pérez", 2, 2, hoy, 30, "10:00", 1, c),
            // Tomás (Ps. Matías Soto): 5 sesiones, la última hace 18 días -> riesgo MEDIO
            cita(-3, "tomas.rivas@correo.cl", "Tomás Rivas", 1, 2, hoy, 46, "15:00", 2, c),
            cita(-4, "tomas.rivas@correo.cl", "Tomás Rivas", 1, 2, hoy, 39, "15:00", 2, c),
            cita(-5, "tomas.rivas@correo.cl", "Tomás Rivas", 1, 2, hoy, 32, "15:00", 0, c),
            cita(-6, "tomas.rivas@correo.cl", "Tomás Rivas", 1, 2, hoy, 25, "15:00", 2, c),
            cita(-7, "tomas.rivas@correo.cl", "Tomás Rivas", 1, 2, hoy, 18, "15:00", 2, c),
            // Valentina (Ps. Matías Soto): 3 sesiones, la última hace 7 días -> riesgo BAJO
            cita(-8, "valentina.ruiz@correo.cl", "Valentina Ruiz", 2, 2, hoy, 21, "11:00", 0, c),
            cita(-9, "valentina.ruiz@correo.cl", "Valentina Ruiz", 2, 2, hoy, 14, "11:00", 0, c),
            cita(-10, "valentina.ruiz@correo.cl", "Valentina Ruiz", 2, 2, hoy, 7, "11:00", 3, c),
            // Martina (Fga. Valentina Muñoz): 1 sesión hace 25 días y después canceló -> riesgo ALTO
            cita(-11, "martina.lagos@correo.cl", "Martina Lagos", 3, 3, hoy, 25, "09:00", 4, c),
            cita(-12, "martina.lagos@correo.cl", "Martina Lagos", 3, 3, hoy, 18, "09:00", 4, EstadoCita.CANCELADA),
            // Benjamín (T.O. Javiera Pizarro): 6 sesiones, la última hace 16 días -> riesgo MEDIO
            cita(-13, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 51, "12:00", 5, c),
            cita(-14, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 44, "12:00", 5, c),
            cita(-15, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 37, "12:00", 5, c),
            cita(-16, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 30, "12:00", 5, c),
            cita(-17, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 23, "12:00", 5, c),
            cita(-18, "benjamin.fuentes@correo.cl", "Benjamín Fuentes", 4, 4, hoy, 16, "12:00", 5, c),
            // Isidora (Ter. Francisca Vera): 2 sesiones, la última hace 3 días -> riesgo BAJO
            cita(-19, "isidora.vega@correo.cl", "Isidora Vega", 5, 5, hoy, 10, "16:00", 6, c),
            cita(-20, "isidora.vega@correo.cl", "Isidora Vega", 5, 5, hoy, 3, "16:00", 6, c)
        )
    }
    // Horas ocupadas en cada box (Box 1 al Box 8) según el día de la semana.
    // Algunos días quedan con buena ocupación y otros con baja demanda (bajo 40%).
    private val ocupacionSemana = listOf(
        listOf(8, 7, 6, 5, 6, 4, 3, 5), // lunes: 55%
        listOf(6, 5, 5, 4, 3, 4, 2, 3), // martes: 40%
        listOf(3, 2, 2, 1, 2, 1, 0, 1), // miércoles: 15%
        listOf(7, 6, 6, 5, 5, 4, 4, 3), // jueves: 50%
        listOf(5, 4, 4, 3, 3, 2, 2, 1), // viernes: 30%
        listOf(2, 1, 1, 1, 0, 0, 1, 0)  // sábado: 7%
    )

    // Citas ficticias solo para el panel deatrás hasta dos semanas adelante)
    fun citasDelPanel(hoy: LocalDate = LocalDate.now()): List<Cita> {
        val lista = mutableListOf<Cita>()
        var id = -1000
        for (dias in -7..14) {
            val fecha = hoy.plusDays(dias.toLong())
            if (Fechas.esDiaHabil(fecha)) {
                // dayOfWeek.value: 1 = lunes ... 6 = sábado
                val delDia = ocupacionSemana[fecha.dayOfWeek.value - 1]
                for (box in 1..8) {
                    // Cada box se llena desde las 09:00 con la cantidad de horas de la tabla
                    for (i in 0 until delDia[box - 1]) {
                        lista.add(Cita(id, "ficticio@correo.cl", "Paciente ficticio", 1, 1, Fechas.formatear(fecha),
                            Fechas.HORAS[i], Modalidad.PRESENCIAL, box, EstadoCita.AGENDADA))
                        id--
                    }
                }
            }
        }
        return lista
    }
    // Junta las citas de Room con el historial ficticio (lo usan el terapeuta y Administración)
    fun juntarCon(citas: List<Cita>): List<Cita> {
        val todas = mutableListOf<Cita>()
        citas.forEach { todas.add(it) }
        citas().forEach { todas.add(it) }
        return todas
    }
}