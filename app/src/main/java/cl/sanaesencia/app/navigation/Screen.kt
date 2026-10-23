package cl.sanaesencia.app.navigation

import cl.sanaesencia.app.model.Rol

// Lista de pantallas de la app (guía 10). Cada una tiene su ruta (su "dirección").
sealed class Screen(val route: String) {
    // Acceso
    data object Login : Screen("login")
    data object Registro : Screen("registro")

    // Paciente
    data object Inicio : Screen("inicio")
    data object Servicios : Screen("servicios")
    data object Profesionales : Screen("profesionales")
    data object Productos : Screen("productos")
    data object Agendar : Screen("agendar")
    data object MisCitas : Screen("mis_citas")

    // Terapeuta
    data object MisPacientes : Screen("mis_pacientes")

    // Administración
    data object PanelAdmin : Screen("panel_admin")
    data object Alertas : Screen("alertas")
    data object Promociones : Screen("promociones")

    // Todos los perfiles
    data object Perfil : Screen("perfil")
}

// Primera pantalla que ve cada perfil después de iniciar sesión
fun inicioPorRol(rol: Rol): Screen = when (rol) {
    Rol.PACIENTE -> Screen.Inicio
    Rol.TERAPEUTA -> Screen.MisPacientes
    Rol.ADMIN -> Screen.PanelAdmin
}