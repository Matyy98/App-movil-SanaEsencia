package cl.sanaesencia.app

import cl.sanaesencia.app.model.Modalidad
import cl.sanaesencia.app.viewmodel.CatalogoViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogoViewModelTest {

    @Test
    fun alInicio_muestraTodosLosServicios() {
        val vm = CatalogoViewModel()
        assertEquals(5, vm.estado.value.servicios.size)
    }

    @Test
    fun filtrarPorEspecialidad_dejaSoloEsa() {
        val vm = CatalogoViewModel()
        vm.elegirEspecialidad("Psicología")
        assertEquals(2, vm.estado.value.servicios.size)
    }

    @Test
    fun filtrarPorOnline_excluyeLosPresenciales() {
        val vm = CatalogoViewModel()
        vm.elegirModalidad(Modalidad.ONLINE)
        // quedan los 3 servicios "Presencial y online"; Fonoaudiología y TO quedan fuera
        assertEquals(3, vm.estado.value.servicios.size)
    }

    @Test
    fun combinarFiltros_puedeDejarLaListaVacia() {
        val vm = CatalogoViewModel()
        vm.elegirEspecialidad("Fonoaudiología")
        vm.elegirModalidad(Modalidad.ONLINE)
        assertEquals(0, vm.estado.value.servicios.size)
    }
}