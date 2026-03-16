package com.example.android_loop.data.repository

import com.example.android_loop.data.accesoApi.ApiLoop
import com.example.android_loop.data.model_dataClass.GetUserDataResult

class UserRepository(private val api: ApiLoop = ApiLoop()) {

    suspend fun login(username: String, password: String): Result<String> {
        return api.login(username, password).map { it.token }
    }

    suspend fun registro(name: String, username: String, email: String, password: String): Result<Boolean> {
        return api.registro(name, username, email, password)
    }

    suspend fun getUserData(token: String): Result<GetUserDataResult> {
        return api.getUserData(token)
    }

}