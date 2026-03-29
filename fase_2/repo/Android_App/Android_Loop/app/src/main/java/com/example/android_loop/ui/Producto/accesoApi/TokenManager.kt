package com.example.android_loop.data.Producto.accesoApi

object TokenManager {

    private var token: String? = null

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? {
        return token
    }

    fun clear() {
        token = null
    }
}