package com.example.android_loop.ui.favoritos

import com.example.android_loop.data.model_dataClass.Favorito

sealed class FavoritosUiState {

    object Idle : FavoritosUiState()

    object Loading : FavoritosUiState()

    data class SuccessGet(val result: List<Favorito>) : FavoritosUiState()

    data class ErrorGet(val message: String) : FavoritosUiState()

    data class SuccessAdd(val result: Boolean) : FavoritosUiState()

    data class ErrorAdd(val message: String) : FavoritosUiState()

}