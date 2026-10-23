package cl.sanaesencia.app.repository

import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Servicio


class ServicioRepository {

    private val servicios = listOf(
        Servicio(1, "Psicoterapia infanto juvenil", "Psicología", "Acompañamiento emocional para niños y adolescentes.", 50, 30000, Modalidad.AMBAS, listOf(1, 2)),
        Servicio(2, "Psicoterapia adultos", "Psicología", "Espacio de escucha y herramientas para el día a día.", 50, 30000, Modalidad.AMBAS, listOf(2)),
        Servicio(3, "Fonoaudiología", "Fonoaudiología", "Evaluación y terapia de lenguaje y comunicación.", 45, 25000, Modalidad.PRESENCIAL, listOf(3)),
        Servicio(4, "Terapia ocupacional", "Terapia ocupacional", "Apoyo para la autonomía en actividades diarias.", 45, 25000, Modalidad.PRESENCIAL, listOf(4)),
        Servicio(5, "Terapia floral", "Terapias integrativas", "Esencias florales personalizadas.", 40, 20000, Modalidad.AMBAS, listOf(5))
    )

    fun obtenerServicios(): List<Servicio> = servicios
}
