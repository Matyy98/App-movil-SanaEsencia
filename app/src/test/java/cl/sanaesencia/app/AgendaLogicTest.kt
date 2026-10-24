package cl.sanaesencia.app

import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.logic.GestorBoxes
import cl.sanaesencia.app.logic.Validaciones
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.model.Terapeuta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

// Pruebas de las reglas de agenda (corren en el PC, sin emulador)
class AgendaLogicTest {

    // Crea una cita de prueba. Solo cambian los datos que importan en cada test.
    private fun cita(
        terapeutaId: Int = 2,
        fecha: String = "26/10/2026",
        hora: String = "10:00",
        modalidad: Modalidad = Modalidad.PRESENCIAL,
        box: Int = 1,
        estado: EstadoCita = EstadoCita.AGENDADA
    ) = Cita(0, "paciente@sanaesencia.cl", "Paciente Demo", 1, terapeutaId, fecha, hora, modalidad, box, estado)

    // ---------- Fechas ----------

    @Test
    fun formatear_agregaCerosALaIzquierda() {
        assertEquals("05/10/2026", Fechas.formatear(LocalDate.of(2026, 10, 5)))
    }

    @Test
    fun parsear_fechaCorrecta() {
        assertEquals(LocalDate.of(2026, 10, 26), Fechas.parsear("26/10/2026"))
    }

    @Test
    fun parsear_fechaInexistenteOTextoDevuelveNull() {
        assertNull(Fechas.parsear("31/02/2026"))
        assertNull(Fechas.parsear("hola"))
    }

    @Test
    fun domingoNoEsDiaHabil() {
        // 25/10/2026 es domingo y 26/10/2026 es lunes
        assertFalse(Fechas.esDiaHabil(LocalDate.of(2026, 10, 25)))
        assertTrue(Fechas.esDiaHabil(LocalDate.of(2026, 10, 26)))
    }

    @Test
    fun fechasDisponibles_sinDomingosYHastaSesentaDias() {
        val hoy = LocalDate.of(2026, 10, 24)
        val fechas = Fechas.fechasDisponibles(hoy)
        assertEquals(hoy, fechas[0])
        assertTrue(fechas.all { Fechas.esDiaHabil(it) })
        assertTrue(!fechas[fechas.size - 1].isAfter(hoy.plusDays(60)))
    }

    @Test
    fun fechaValida_rechazaPasadoYMasDeSesentaDias() {
        val hoy = LocalDate.of(2026, 10, 26)
        assertFalse(Fechas.fechaValida(LocalDate.of(2026, 10, 24), hoy))
        assertFalse(Fechas.fechaValida(LocalDate.of(2027, 1, 30), hoy))
        assertTrue(Fechas.fechaValida(LocalDate.of(2026, 10, 27), hoy))
    }

    @Test
    fun horasDelDia_hoyNoMuestraLasQueYaPasaron() {
        val ahora = LocalDateTime.of(2026, 10, 26, 14, 30)
        val horas = Fechas.horasDelDia(LocalDate.of(2026, 10, 26), ahora)
        assertEquals(listOf("15:00", "16:00", "17:00", "18:00"), horas)
    }

    @Test
    fun horasDelDia_otroDiaMuestraLosDiezBloques() {
        val ahora = LocalDateTime.of(2026, 10, 26, 14, 30)
        assertEquals(10, Fechas.horasDelDia(LocalDate.of(2026, 10, 27), ahora).size)
    }

    // ---------- GestorBoxes ----------

    @Test
    fun profesionalOcupado_noPuedeTenerDosCitasALaMismaHora() {
        val citas = listOf(cita(terapeutaId = 2))
        assertTrue(GestorBoxes.profesionalOcupado(citas, 2, "26/10/2026", "10:00"))
        assertFalse(GestorBoxes.profesionalOcupado(citas, 3, "26/10/2026", "10:00"))
        assertFalse(GestorBoxes.profesionalOcupado(citas, 2, "26/10/2026", "11:00"))
    }

    @Test
    fun citaCancelada_liberaLaHora() {
        val citas = listOf(cita(estado = EstadoCita.CANCELADA))
        assertFalse(GestorBoxes.profesionalOcupado(citas, 2, "26/10/2026", "10:00"))
    }

    @Test
    fun boxLibre_entregaElPrimeroDisponible() {
        val citas = listOf(cita(box = 1), cita(box = 2))
        assertEquals(3, GestorBoxes.boxLibre(citas, "26/10/2026", "10:00"))
    }

    @Test
    fun boxLibre_conLosOchoOcupadosDevuelveNull() {
        val citas = listOf(1, 2, 3, 4, 5, 6, 7, 8).map { cita(terapeutaId = it + 10, box = it) }
        assertNull(GestorBoxes.boxLibre(citas, "26/10/2026", "10:00"))
        // Online no necesita box, así que sigue disponible
        assertFalse(GestorBoxes.horaDisponible(citas, 2, "26/10/2026", "10:00", Modalidad.PRESENCIAL))
        assertTrue(GestorBoxes.horaDisponible(citas, 2, "26/10/2026", "10:00", Modalidad.ONLINE))
    }

    @Test
    fun asignarBox_onlineEsCero() {
        assertEquals(0, GestorBoxes.asignarBox(listOf(), "26/10/2026", "10:00", Modalidad.ONLINE))
        assertEquals(1, GestorBoxes.asignarBox(listOf(), "26/10/2026", "10:00", Modalidad.PRESENCIAL))
    }

    @Test
    fun modalidadesPosibles_cruzaServicioYProfesional() {
        val servicio = Servicio(1, "S", "E", "D", 50, 30000, Modalidad.AMBAS, listOf(3))
        val soloPresencial = Terapeuta(3, "T", "E", "D", Modalidad.PRESENCIAL, true)
        val ambas = Terapeuta(2, "T", "E", "D", Modalidad.AMBAS, true)
        assertEquals(listOf(Modalidad.PRESENCIAL), GestorBoxes.modalidadesPosibles(servicio, soloPresencial))
        assertEquals(listOf(Modalidad.PRESENCIAL, Modalidad.ONLINE), GestorBoxes.modalidadesPosibles(servicio, ambas))
    }

    @Test
    fun ocupacionPorBox_cincoCitasDeDiezEsCincuentaPorCiento() {
        val citas = listOf("09:00", "10:00", "11:00", "12:00", "13:00").map { cita(hora = it, box = 1) }
        val ocupacion = GestorBoxes.ocupacionPorBox(citas, "26/10/2026")
        assertEquals(8, ocupacion.size)
        assertEquals(50, ocupacion[0].porcentaje)
        assertEquals(0, ocupacion[1].porcentaje)
        assertEquals(6, GestorBoxes.promedioOcupacion(ocupacion))
    }

    @Test
    fun bajaDemanda_bajoElCuarentaPorCiento() {
        assertTrue(GestorBoxes.esBajaDemanda(39))
        assertFalse(GestorBoxes.esBajaDemanda(40))
    }

    // ---------- Validaciones del formulario de agendar ----------

    @Test
    fun motivo_debeTenerEntreDiezYCienCaracteres() {
        assertNotNull(Validaciones.motivoCita(""))
        assertNotNull(Validaciones.motivoCita("dolor"))
        assertNull(Validaciones.motivoCita("Ansiedad antes de dormir"))
    }

    @Test
    fun servicioYProfesional_ceroSignificaSinElegir() {
        assertNotNull(Validaciones.servicioElegido(0))
        assertNull(Validaciones.servicioElegido(2))
        assertNotNull(Validaciones.profesionalElegido(0))
        assertNotNull(Validaciones.modalidadElegida(null))
        assertNull(Validaciones.modalidadElegida(Modalidad.ONLINE))
    }

    @Test
    fun fechaCita_rechazaFormatoMalo() {
        assertNotNull(Validaciones.fechaCita(""))
        assertNotNull(Validaciones.fechaCita("mañana"))
    }
}
