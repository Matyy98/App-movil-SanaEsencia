package cl.sanaesencia.app.model

// Un mensaje de error por campo del formulario de agendar (null = sin error). Patrón guía 11.
data class AgendaErrores(
    val servicio: String? = null,
    val profesional: String? = null,
    val modalidad: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivo: String? = null
)

// Estado del formulario de agendar, que se llena en 4 pasos:
// 1 servicio, 2 profesional + modalidad + fecha, 3 hora + motivo, 4 confirmar
data class AgendaUiState(
    val paso: Int = 1,
    // 0 = todavía no elige
    val servicioId: Int = 0,
    val terapeutaId: Int = 0,
    val modalidad: Modalidad? = null,
    // "dd/MM/yyyy" y "HH:mm" (vacío = todavía no elige)
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    // Página del selector de fechas (parte en 0)
    val paginaFechas: Int = 0,
    val errores: AgendaErrores = AgendaErrores(),
    // Error que no es de un campo (ej. la hora se ocupó mientras confirmaba)
    val errorGeneral: String? = null,
    // Si no es null, la cita ya quedó guardada
    val citaConfirmada: Cita? = null
)
