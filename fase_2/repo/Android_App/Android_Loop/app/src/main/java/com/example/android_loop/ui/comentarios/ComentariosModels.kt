package com.example.android_loop.ui.comentarios

import kotlinx.serialization.Serializable

@Serializable
data class ComentariosResponse(
    val comentarios: List<Comentario>
)

@Serializable
data class CreateComentarioRequest(
    val data: CreateComentarioData
)

@Serializable
data class CreateComentarioData(
    val partner_id: Int,
    val contenido: String,
    val estado: String,
    val valoracion: Float? = null
)

@Serializable
data class CreateComentarioResponse(
    val success: Boolean? = null,
    val comentario_id: Int? = null,
    val error: String? = null
)

@Serializable
data class UpdateComentarioData(
    val contenido: String,
    val estado: String,
    val valoracion: Float? = null
)

@Serializable
data class UpdateComentarioRequest(
    val data: UpdateComentarioData
)

@Serializable
data class UpdateComentarioResponse(
    val success: Boolean? = null,
    val comentario_id: Int? = null,
    val error: String? = null
)

@Serializable
data class DeleteComentarioResponse(
    val success: Boolean? = null,
    val error: String? = null
)
