package cl.sanaesencia.app.repository

import cl.sanaesencia.app.data.PromocionDao
import cl.sanaesencia.app.data.PromocionEntity
import cl.sanaesencia.app.model.Promocion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Intermediario entre el ViewModel y la tabla de promociones.
// El ViewModel trabaja con Promocion (model) y no sabe que por debajo está Room.
class PromocionRepository(private val dao: PromocionDao) {

    // Convierte cada fila de la tabla (Entity) en el modelo Promocion
    fun obtenerPromociones(): Flow<List<Promocion>> {
        return dao.obtenerTodas().map { filas ->
            filas.map { fila ->
                Promocion(fila.id, fila.titulo, fila.descripcion, fila.descuento, fila.segmento)
            }
        }
    }

    // Guarda una promoción nueva (el id lo pone Room)
    suspend fun agregar(titulo: String, descripcion: String, descuento: Int, segmento: String) {
        dao.insertar(
            PromocionEntity(
                titulo = titulo,
                descripcion = descripcion,
                descuento = descuento,
                segmento = segmento
            )
        )
    }
}