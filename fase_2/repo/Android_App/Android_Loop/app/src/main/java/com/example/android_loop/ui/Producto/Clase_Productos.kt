package com.example.android_loop.data.Producto

import kotlinx.serialization.Serializable

@Serializable
data class Product(
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
    val thumbnail: String? = null
)

@Serializable
data class Categoria(
    val id: Int,
    val nombre: String
)

@Serializable
data class Propietario(
    val id: Int,
    val nombre: String
)

@Serializable
data class Etiqueta(
    val id: Int,
    val name: String,
    val active: Boolean
)

@Serializable
data class Imagen(
    val id: Int,
    val principal: Boolean,
    val orden: Int
)

@Serializable
data class ImagenConDatos(
    val id: Int,
    val imagen: String?,
    val is_principal: Boolean,
    val sequence: Int
)

@Serializable
data class ProductosResponse(
    val products: List<Product>
)
