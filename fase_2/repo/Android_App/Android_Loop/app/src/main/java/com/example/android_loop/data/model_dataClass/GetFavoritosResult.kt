package com.example.android_loop.data.model_dataClass

import kotlinx.serialization.Serializable

@Serializable
data class GetFavoritosResult (
    val result: List<Favorito>
)

@Serializable
data class Favorito(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val ubicacion: String,
    val imagenes: List<String>
)