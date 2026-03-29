package com.example.android_loop.ui.comentarios

import kotlinx.serialization.Serializable

@Serializable
data class Comentario(
    val id: Int,
    val contenido: String,
    val fecha_creacion: String,
    val comentador: String,
    val comentador_partner_id: Int? = null,
    val imagen_comentador: String? = null,
    val valoracion: Float? = null,
    val estado: String,
    val moderador: String? = null,
    val fecha_moderacion: String? = null
)