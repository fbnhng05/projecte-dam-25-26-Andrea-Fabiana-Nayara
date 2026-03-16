package com.example.android_loop.data.accesoApi

import com.example.android_loop.data.Producto.CreateEtiquetaRequest
import com.example.android_loop.data.Producto.CreateEtiquetaResponse
import com.example.android_loop.data.Producto.CreateProductRequest
import com.example.android_loop.data.Producto.CreateProductResponse
import com.example.android_loop.data.Producto.Etiqueta
import com.example.android_loop.data.Producto.EtiquetasResponse
import com.example.android_loop.data.Producto.ImagenConDatos
import com.example.android_loop.data.Producto.ImagenesProductoResponse
import com.example.android_loop.data.Producto.JsonRpcRequest
import com.example.android_loop.data.model_dataClass.ProductosResult
import com.example.android_loop.data.model_dataClass.RpcResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiProductoLoop(
    private val cliente: HttpClient = HttpClientProvider.cliente
) {
    suspend fun getProductos(token: String): Result<ProductosResult> {
        return try {
            val response: RpcResponse<ProductosResult> =
                cliente.get("${Servidor.BASE_URL}/api/products") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun createProduct(
        token: String,
        request: CreateProductRequest
    ): Result<CreateProductResponse> {

        return try {

            val response: RpcResponse<CreateProductResponse> =
                cliente.post("${Servidor.BASE_URL}/api/productos") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(JsonRpcRequest(params = request))
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun createEtiqueta(
        token: String,
        request: CreateEtiquetaRequest
    ): Result<CreateEtiquetaResponse> {

        return try {
            val response: RpcResponse<CreateEtiquetaResponse> =
                cliente.post("${Servidor.BASE_URL}/api/v1/loop/etiquetas") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(JsonRpcRequest(params = request))
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun getProductImages(
        token: String,
        productId: Int): Result<List<ImagenConDatos>> {
        return try {

            val response: ImagenesProductoResponse =
                cliente.get("${Servidor.BASE_URL}/api/v1/loop/productos/$productId/imagenes") {
                    header("Authorization", "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

            Result.success(response.imagenes)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun getEtiquetas(token: String,): Result<List<Etiqueta>> {
        return try {

            val response: EtiquetasResponse =
                cliente.get("${Servidor.BASE_URL}/api/v1/loop/etiquetas") {
                    header("Authorization", "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

            Result.success(response.etiquetas)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}