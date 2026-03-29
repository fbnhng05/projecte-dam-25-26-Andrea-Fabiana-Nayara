package com.example.android_loop.ui.perfilUsuario

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.model_dataClass.GetUserDataResult
import com.example.android_loop.data.repository.UserRepository
import kotlinx.coroutines.launch

class PerfilUsuarioViewModel(private val repository: UserRepository = UserRepository()): ViewModel() {

    var getUserDataState by mutableStateOf<Result<GetUserDataResult>?>(null)

    fun getUserData(token: String) {
        viewModelScope.launch {
            getUserDataState = repository.getUserData(token)
        }
    }

}