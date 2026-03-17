package com.example.android_loop.data.Producto

import kotlinx.serialization.Serializable

//----------RESPUESTAS ------------------
@Serializable
data class Producto_Respuesta(
    val ok: Boolean,
    val count: Int,
    val products: List<Product>
)

@Serializable
data class RespuestaJSON<T>(
    val jsonrpc: String,
    val id: String?,
    val result: T?
)


@Serializable
data class CreateProductResponse(
    val ok: Boolean? = null,
    val product_id: Int? = null
)

@Serializable
data class JsonRpcRequest<T>(
    val jsonrpc: String = "2.0",
    val method: String = "call",
    val params: T
)

@Serializable
data class CreateProductRequest(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val estado: String,
    val ubicacion: String,
    val antiguedad: String,
    val categoria_id: Int,
    val imagenes: List<ImageRequest>
)

@Serializable
data class ImageRequest(
    val imagen: String,
    val is_principal: Boolean,
    val sequence: Int
)

@Serializable
data class ImagenesProductoResponse(
    val ok: Boolean,
    val imagenes: List<ImagenConDatos>
)
