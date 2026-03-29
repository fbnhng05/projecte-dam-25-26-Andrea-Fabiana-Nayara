package com.example.android_loop.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var loginState by mutableStateOf<LoginUiState>(LoginUiState.Idle)

    fun login(username : String, passwd : String) {
        viewModelScope.launch {
            loginState = LoginUiState.Loading

            val result = repository.login(username, passwd)

            loginState = result.fold(
                onSuccess = { LoginUiState.Success(it) },
                onFailure = { LoginUiState.Error("Usuario o contraseña incorrectos") }
            )
        }
    }

}