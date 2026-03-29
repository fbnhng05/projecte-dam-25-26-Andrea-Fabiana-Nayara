package com.example.android_loop.data.repository


import com.example.android_loop.data.accesoApi.ApiProductoLoop
import com.example.android_loop.data.model_dataClass.ProductosResult

class ProductoRepository (private val api: ApiProductoLoop = ApiProductoLoop()) {

    suspend fun getProductos(token: String): Result<ProductosResult> {
        return api.getProductos(token)
    }
}