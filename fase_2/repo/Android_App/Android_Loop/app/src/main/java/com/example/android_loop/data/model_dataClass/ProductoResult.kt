package com.example.android_loop.data.model_dataClass

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(val id: Int, val nombre: String)
@Serializable
data class Propietario(val id: Int, val nombre: String)
@Serializable
data class EtiquetaProducto(val id: Int, val nombre: String)
@Serializable
data class Imagen(val id: Int, val principal: Boolean, val orden: Int)

@Serializable
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
    val etiquetas: List<EtiquetaProducto>,
    val imagenes: List<Imagen>,
    val thumbnail: String? = null
)

@Serializable
data class ProductosResult(
    val ok: Boolean,
    val count: Int,
    val products: List<Producto>
)