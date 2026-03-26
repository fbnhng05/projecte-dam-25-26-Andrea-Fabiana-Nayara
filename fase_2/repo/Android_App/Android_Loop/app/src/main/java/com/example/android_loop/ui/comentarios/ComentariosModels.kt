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
    val estado: String
)

@Serializable
data class CreateComentarioResponse(
    val success: Boolean? = null,
    val comentario_id: Int? = null,
    val error: String? = null
)
