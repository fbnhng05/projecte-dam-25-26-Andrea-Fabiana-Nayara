package com.example.android_loop.data.model_dataClass

data class Categoria(val id: Int, val nombre: String)
data class Propietario(val id: Int, val nombre: String)
data class Etiqueta(val id: Int, val nombre: String)
data class Imagen(val id: Int, val principal: Boolean, val orden: Int)

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val estado: String,
    val ubicacion: String,
    val antiguedad: String?,
    val categoria: Categoria,
    val propietario: Propietario,
    val etiquetas: List<Etiqueta>,
    val imagenes: List<Imagen>,
    val thumbnail: String?
)

data class ProductosResult(
    val ok: Boolean,
    val count: Int,
    val products: List<Producto>
)