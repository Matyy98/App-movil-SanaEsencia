package cl.sanaesencia.app.repository

import cl.sanaesencia.app.model.CategoriaProducto
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Producto
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.model.Terapeuta

// Catálogo del centro: servicios, profesionales y productos (todos datos ficticios).
class CatalogoRepository {

    // Los servicios ya están definidos en ServicioRepository, aquí se reutilizan
    private val servicios = ServicioRepository().obtenerServicios()

    // Los ids de los profesionales calzan con terapeutaIds de cada servicio.
    // El profesional 2 es el de la cuenta de prueba terapeuta@sanaesencia.cl
    private val terapeutas = listOf(
        Terapeuta(1, "Ps. Camila Rojas", "Psicología", "Psicóloga infanto juvenil, trabaja con niños y adolescentes.", Modalidad.AMBAS, false),
        Terapeuta(2, "Ps. Matías Soto", "Psicología", "Psicólogo de adultos y adolescentes con enfoque integrativo.", Modalidad.AMBAS, true),
        Terapeuta(3, "Fga. Valentina Muñoz", "Fonoaudiología", "Fonoaudióloga especialista en lenguaje infantil.", Modalidad.PRESENCIAL, true),
        Terapeuta(4, "T.O. Javiera Pizarro", "Terapia ocupacional", "Terapeuta ocupacional, integración sensorial y autonomía.", Modalidad.PRESENCIAL, true),
        Terapeuta(5, "Ter. Francisca Vera", "Terapias integrativas", "Terapeuta floral y de aromaterapia.", Modalidad.AMBAS, true)
    )

    private val productos = listOf(
        Producto(1, "Kit Calma", CategoriaProducto.KIT, "Roll-on, bruma y esencia floral para momentos de ansiedad.", 18990, 12),
        Producto(2, "Kit Sueño", CategoriaProducto.KIT, "Bruma para almohada y roll-on de lavanda.", 16990, 8),
        Producto(3, "Roll-on Lavanda", CategoriaProducto.ROLL_ON, "Aceite esencial de lavanda para aplicar en las muñecas.", 6990, 25),
        Producto(4, "Roll-on Cítrico", CategoriaProducto.ROLL_ON, "Mezcla de naranja y limón para empezar el día.", 6990, 0),
        Producto(5, "Bruma Almohada", CategoriaProducto.BRUMA, "Bruma de manzanilla y lavanda para antes de dormir.", 8990, 15),
        Producto(6, "Bruma Eucalipto", CategoriaProducto.BRUMA, "Bruma ambiental de eucalipto para el hogar.", 8490, 10)
    )

    fun obtenerServicios(): List<Servicio> = servicios

    fun obtenerTerapeutas(): List<Terapeuta> = terapeutas

    fun obtenerProductos(): List<Producto> = productos

    // Busca un servicio por su id. Devuelve null si no existe.
    fun buscarServicio(id: Int): Servicio? = servicios.firstOrNull { it.id == id }

    // Busca un profesional por su id. Devuelve null si no existe.
    fun buscarTerapeuta(id: Int): Terapeuta? = terapeutas.firstOrNull { it.id == id }

    // Profesionales que atienden un servicio y tienen la agenda abierta (los que se pueden elegir al agendar)
    fun terapeutasDeServicio(servicio: Servicio): List<Terapeuta> =
        terapeutas.filter { servicio.terapeutaIds.contains(it.id) && it.agendaDisponible }
}
