package com.example.android_loop.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_loop.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository = UserRepository()) : ViewModel() {

    var loginState by mutableStateOf<Result<String>?>(null)

    fun login(username : String, passwd : String) {
        viewModelScope.launch {
            loginState = repository.login(username, passwd)
        }
    }

}