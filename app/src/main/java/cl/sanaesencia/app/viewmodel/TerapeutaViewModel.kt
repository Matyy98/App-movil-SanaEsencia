package cl.sanaesencia.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.sanaesencia.app.data.AppDatabase
import cl.sanaesencia.app.logic.Fechas
import cl.sanaesencia.app.logic.MotorDesercion
import cl.sanaesencia.app.model.AlertaDesercion
import cl.sanaesencia.app.model.Cita
import cl.sanaesencia.app.model.EstadoCita
import cl.sanaesencia.app.repository.CatalogoRepository
import cl.sanaesencia.app.repository.CitaRepository
import cl.sanaesencia.app.repository.HistorialDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

// ViewModel del terapeuta: sus pacientes con riesgo de deserción y su agenda (Room).
class TerapeutaViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = CitaRepository(AppDatabase.obtener(application).citaDao())
    private val catalogo = CatalogoRepository()

    // Citas guardadas en Room (se actualiza sola cuando un paciente agenda o se marca una sesión)
    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    val citas: StateFlow<List<Cita>> = _citas

    init {
        viewModelScope.launch {
            repositorio.obtenerCitas().collectLatest { lista ->
                _citas.value = lista
            }
        }
    }

    // Junta las citas de Room con el historial ficticio de sesiones pasadas
    private fun conHistorial(citas: List<Cita>): List<Cita> = HistorialDemo.juntarCon(citas)

    // Pacientes del profesional con su riesgo, los de más riesgo primero.
    // Recibe las citas para que la pantalla se redibuje cuando cambian.
    fun pacientes(citas: List<Cita>, terapeutaId: Int): List<AlertaDesercion> =
        MotorDesercion.alertasDeTerapeuta(conHistorial(citas), terapeutaId)

    // Citas agendadas del profesional, la más cercana primero
    fun agenda(citas: List<Cita>, terapeutaId: Int): List<Cita> =
        citas
            .filter { it.terapeutaId == terapeutaId && it.estado == EstadoCita.AGENDADA }
            .sortedBy { Fechas.claveOrden(it) }

    // Una sesión solo se puede marcar como realizada si ya llegó su día
    fun puedeMarcarRealizada(cita: Cita): Boolean {
        val fecha = Fechas.parsear(cita.fecha) ?: return false
        return !fecha.isAfter(LocalDate.now())
    }

    fun nombreServicio(id: Int): String = catalogo.buscarServicio(id)?.nombre ?: "Servicio"

    // Marca la sesión como realizada (cuenta para la continuidad del paciente)
    fun marcarRealizada(id: Int) {
        viewModelScope.launch {
            repositorio.actualizarEstado(id, EstadoCita.COMPLETADA)
        }
    }

    // El paciente no vino o avisó: se cancela y el box queda libre
    fun cancelar(id: Int) {
        viewModelScope.launch {
            repositorio.actualizarEstado(id, EstadoCita.CANCELADA)
        }
    }
}