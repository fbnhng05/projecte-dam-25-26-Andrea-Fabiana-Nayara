package com.example.android_loop.data.Producto

import kotlinx.serialization.Serializable


@Serializable
data class CreateEtiquetaRequest(
    val data: EtiquetaData
)


@Serializable
data class EtiquetasResponse(
    val success: Boolean,
    val etiquetas: List<Etiqueta>
)

@Serializable
data class EtiquetaData(
    val name: String,
    val active: Boolean = true
)

@Serializable
data class CreateEtiquetaResponse(
    val success: Boolean?,
    val etiqueta_id: Int?,
    val error: String?
)
