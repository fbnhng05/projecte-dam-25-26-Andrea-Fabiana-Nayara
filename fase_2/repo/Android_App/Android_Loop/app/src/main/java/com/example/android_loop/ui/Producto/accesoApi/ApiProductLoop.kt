package com.example.android_loop.data.Producto.accesoApi

import com.example.android_loop.data.model_dataClass.RpcResponse
import com.example.android_loop.ui.comentarios.Comentario
import com.example.android_loop.ui.comentarios.ComentariosResponse
import com.example.android_loop.ui.comentarios.CreateComentarioRequest
import com.example.android_loop.ui.comentarios.CreateComentarioResponse
import com.example.android_loop.data.Producto.ProductosResponse
import com.example.android_loop.data.Producto.Product
import com.example.android_loop.data.Producto.CreateEtiquetaRequest
import com.example.android_loop.data.Producto.CreateEtiquetaResponse
import com.example.android_loop.data.Producto.CreateProductRequest
import com.example.android_loop.data.Producto.CreateProductResponse
import com.example.android_loop.data.Producto.Etiqueta
import com.example.android_loop.data.Producto.EtiquetasResponse
import com.example.android_loop.data.Producto.ImagenConDatos
import com.example.android_loop.data.Producto.ImagenesProductoResponse
import com.example.android_loop.data.Producto.JsonRpcRequest
import com.example.android_loop.data.accesoApi.HttpClientProvider
import com.example.android_loop.data.accesoApi.Servidor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ApiProductLoop(
    private val cliente: HttpClient = HttpClientProvider.cliente
) {

    suspend fun getProducts(): Result<ProductosResponse> {
        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

            val response: ProductosResponse =
                cliente.get("${Servidor.BASE_URL}/api/products") {
                    header("Authorization", "Bearer $token")
                    accept(ContentType.Application.Json)
                }.body()

            Result.success(response)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun createProduct(
        request: CreateProductRequest
    ): Result<CreateProductResponse> {

        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

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
        request: CreateEtiquetaRequest
    ): Result<CreateEtiquetaResponse> {

        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

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


    suspend fun getProductImages(productId: Int): Result<List<ImagenConDatos>> {
        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

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


    suspend fun getEtiquetas(): Result<List<Etiqueta>> {
        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

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


    suspend fun getComentarios(productId: Int): Result<List<Comentario>> {
        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

            val response: RpcResponse<ComentariosResponse> =
                cliente.get("${Servidor.BASE_URL}/api/v1/loop/usuarios/$productId/comentarios") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(
                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("method", "call")
                            put("params", buildJsonObject {})
                        }
                    )
                }.body()

            Result.success(response.result.comentarios)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }


    suspend fun crearComentario(
        request: CreateComentarioRequest
    ): Result<CreateComentarioResponse> {

        return try {

            val token = TokenManager.getToken()
                ?: return Result.failure(Exception("Token no disponible"))

            val response: RpcResponse<CreateComentarioResponse> =
                cliente.post("${Servidor.BASE_URL}/api/v1/loop/comentarios") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(JsonRpcRequest(params = request))
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
