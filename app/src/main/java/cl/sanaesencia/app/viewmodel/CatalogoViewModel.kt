package cl.sanaesencia.app.viewmodel

import androidx.lifecycle.ViewModel
import cl.sanaesencia.app.model.CatalogoUiState
import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.model.Servicio
import cl.sanaesencia.app.repository.CatalogoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Catálogo del centro con filtros por especialidad y modalidad (guía 11).
// Se crea una sola vez en AppNavigation y se pasa a las pantallas.
class CatalogoViewModel : ViewModel() {

    private val repositorio = CatalogoRepository()

    private val _estado = MutableStateFlow(
        CatalogoUiState(
            servicios = repositorio.obtenerServicios(),
            especialidades = armarEspecialidades(),
            terapeutas = repositorio.obtenerTerapeutas(),
            productos = repositorio.obtenerProductos()
        )
    )
    val estado: StateFlow<CatalogoUiState> = _estado

    // Lista de especialidades sin repetir: parte con "Todas" y agrega cada una solo una vez
    private fun armarEspecialidades(): List<String> {
        val lista = mutableListOf(CatalogoUiState.TODAS)
        repositorio.obtenerServicios().forEach { servicio ->
            if (!lista.contains(servicio.especialidad)) {
                lista.add(servicio.especialidad)
            }
        }
        return lista
    }

    // Deja solo los servicios que cumplen los dos filtros.
    // Un servicio "Presencial y online" aparece tanto al filtrar presencial como online.
    private fun filtrar(especialidad: String, modalidad: Modalidad?): List<Servicio> {
        return repositorio.obtenerServicios().filter { servicio ->
            val calzaEspecialidad = especialidad == CatalogoUiState.TODAS || servicio.especialidad == especialidad
            val calzaModalidad = modalidad == null || servicio.modalidad == modalidad || servicio.modalidad == Modalidad.AMBAS
            calzaEspecialidad && calzaModalidad
        }
    }

    fun elegirEspecialidad(especialidad: String) {
        _estado.update {
            it.copy(
                especialidadElegida = especialidad,
                servicios = filtrar(especialidad, it.modalidadElegida)
            )
        }
    }

    // modalidad = null quita el filtro
    fun elegirModalidad(modalidad: Modalidad?) {
        _estado.update {
            it.copy(
                modalidadElegida = modalidad,
                servicios = filtrar(it.especialidadElegida, modalidad)
            )
        }
    }
}
