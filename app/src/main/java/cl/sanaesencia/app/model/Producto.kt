package cl.sanaesencia.app.model

// Tipos de productos naturales que vende el centro
enum class CategoriaProducto(val etiqueta: String) {
    KIT("Kit"),
    ROLL_ON("Roll-on"),
    BRUMA("Bruma")
}

// Producto de la tienda (datos ficticios)
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaProducto,
    val descripcion: String,
    val precio: Int,
    val stock: Int
)