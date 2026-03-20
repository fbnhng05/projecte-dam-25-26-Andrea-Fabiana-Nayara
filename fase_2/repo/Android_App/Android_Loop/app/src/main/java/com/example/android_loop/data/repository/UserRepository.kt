package com.example.android_loop.data.repository

import com.example.android_loop.data.accesoApi.ApiLoop
import com.example.android_loop.data.model_dataClass.AddFavoritosResult
import com.example.android_loop.data.model_dataClass.GetFavoritosResult
import com.example.android_loop.data.model_dataClass.GetUserDataResult
import com.example.android_loop.data.model_dataClass.RegistroResult
import com.example.android_loop.data.model_dataClass.RemoveFavoritoResult

class UserRepository(private val api: ApiLoop = ApiLoop()) {

    suspend fun login(username: String, password: String): Result<String> {
        return api.login(username, password).map { it.token }
    }

    suspend fun registro(name: String, username: String, email: String, password: String): Result<RegistroResult> {
        return api.registro(name, username, email, password)
    }

    suspend fun getUserData(token: String): Result<GetUserDataResult> {
        return api.getUserData(token)
    }

    suspend fun addFavoritos(token: String, productoId: Int): Result<AddFavoritosResult> {
        return api.addFavoritos(token, productoId)
    }

    suspend fun removeFavorito(token: String, productoId: Int): Result<RemoveFavoritoResult> {
        return api.removeFavorito(token, productoId)
    }

    suspend fun getFavoritos(token: String): Result<GetFavoritosResult> {
        return api.getUserFavoritos(token)
    }

}