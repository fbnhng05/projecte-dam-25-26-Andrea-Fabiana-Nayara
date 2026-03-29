package com.example.android_loop.ui.favoritos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.repository.UserRepository
import kotlinx.coroutines.launch

class FavoritosViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var favState by mutableStateOf<FavoritosUiState>(FavoritosUiState.Idle)

    fun favoritosGet(token : String) {
        viewModelScope.launch {
            favState = FavoritosUiState.Loading

            val result = repository.getFavoritos(token)

            favState = result.fold(
                onSuccess = { FavoritosUiState.SuccessGet(it.result) },
                onFailure = { FavoritosUiState.ErrorGet(it.message ?: "Hubo un error") }
            )
        }
    }

    fun favoritosDelete(token: String, productoId: Int) {

        viewModelScope.launch {

            repository.removeFavorito(token, productoId)

            if (favState is FavoritosUiState.SuccessGet) {
                val currentLista = (favState as FavoritosUiState.SuccessGet).result

                val nuevaLista = currentLista.filter { it.id != productoId }

                favState = FavoritosUiState.SuccessGet(nuevaLista)

            }

        }
    }

    fun favoritosAdd(token: String, productoId: Int) {

        viewModelScope.launch {
            val result = repository.addFavoritos(token, productoId)

            favState = result.fold(
                onSuccess = { FavoritosUiState.SuccessAdd(it.success) },
                onFailure = { FavoritosUiState.ErrorAdd(it.message ?: "Hubo un error") }
            )
        }

    }

}