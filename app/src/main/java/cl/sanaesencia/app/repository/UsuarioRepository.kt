package cl.sanaesencia.app.repository

import cl.sanaesencia.app.data.SesionDataStore
import cl.sanaesencia.app.data.UsuarioDao
import cl.sanaesencia.app.data.UsuarioEntity
import cl.sanaesencia.app.model.Rol
import cl.sanaesencia.app.model.Usuario
import kotlinx.coroutines.flow.first

// Intermediario entre los ViewModel y los datos de usuarios:
// cuentas de prueba, usuarios registrados en Room y la sesión en DataStore
class UsuarioRepository(private val dao: UsuarioDao, private val sesion: SesionDataStore) {

    // Cuentas de prueba (datos ficticios)
    private val cuentasPrueba = listOf(
        Usuario("Paciente Demo", "paciente@sanaesencia.cl", "Paciente123", "+56911111111", Rol.PACIENTE),
        Usuario("Ps. Matías Soto", "terapeuta@sanaesencia.cl", "Terapeuta123", "+56922222222", Rol.TERAPEUTA, terapeutaId = 2),
        Usuario("Administración", "admin@sanaesencia.cl", "Admin1234", "+56933333333", Rol.ADMIN)
    )

    // Busca primero en las cuentas de prueba y después en la tabla de Room
    suspend fun buscarPorCorreo(correo: String): Usuario? {
        val buscado = correo.trim().lowercase()
        val dePrueba = cuentasPrueba.firstOrNull { it.correo == buscado }
        if (dePrueba != null) {
            return dePrueba
        }
        val fila = dao.buscarPorCorreo(buscado)
        if (fila == null) {
            return null
        }
        return Usuario(fila.nombre, fila.correo, fila.clave, fila.telefono, fila.rol, fila.terapeutaId)
    }

    // Devuelve el usuario si el correo y la clave coinciden, o null si no
    suspend fun validarCredenciales(correo: String, clave: String): Usuario? {
        val usuario = buscarPorCorreo(correo)
        if (usuario != null && usuario.clave == clave) {
            return usuario
        }
        return null
    }

    // Guarda un paciente nuevo en Room. Devuelve false si el correo ya existe
    suspend fun registrar(nombre: String, correo: String, telefono: String, clave: String): Boolean {
        if (buscarPorCorreo(correo) != null) {
            return false
        }
        dao.insertar(
            UsuarioEntity(
                nombre = nombre.trim(),
                correo = correo.trim().lowercase(),
                clave = clave,
                telefono = telefono.trim(),
                rol = Rol.PACIENTE
            )
        )
        return true
    }

    // Sesión: se guarda solo el correo del usuario conectado
    suspend fun guardarSesion(correo: String) {
        sesion.guardarSesion(correo)
    }

    suspend fun cerrarSesion() {
        sesion.cerrarSesion()
    }

    // Usuario con sesión iniciada, o null si nadie ha entrado
    suspend fun usuarioEnSesion(): Usuario? {
        val correo = sesion.obtenerSesion().first()
        if (correo.isBlank()) {
            return null
        }
        return buscarPorCorreo(correo)
    }
}