package com.example.android_loop.ui.registro
sealed class RegistroUiState {

    object Idle : RegistroUiState()

    object Loading : RegistroUiState()

    data class Success(val resp: Boolean): RegistroUiState()

    data class Error(val message: String): RegistroUiState()

}