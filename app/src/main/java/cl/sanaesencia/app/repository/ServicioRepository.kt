package cl.sanaesencia.app.repository

import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Servicio

// Simula el origen de datos (guía 7: repository opcional). Más adelante se reemplaza por la API.
class ServicioRepository {

    private val servicios = listOf(
        Servicio(1, "Psicoterapia infanto juvenil", "Psicología", "Acompañamiento emocional para niños y adolescentes.", Modalidad.AMBAS),
        Servicio(2, "Psicoterapia adultos", "Psicología", "Espacio de escucha y herramientas para el día a día.", Modalidad.AMBAS),
        Servicio(3, "Fonoaudiología", "Fonoaudiología", "Evaluación y terapia de lenguaje y comunicación.", Modalidad.PRESENCIAL),
        Servicio(4, "Consulta de salud mental", "Medicina", "Evaluación y seguimiento médico integral.", Modalidad.ONLINE),
        Servicio(5, "Terapia floral", "Terapias integrativas", "Esencias florales personalizadas.", Modalidad.PRESENCIAL)
    )

    fun obtenerServicios(): List<Servicio> = servicios
}
