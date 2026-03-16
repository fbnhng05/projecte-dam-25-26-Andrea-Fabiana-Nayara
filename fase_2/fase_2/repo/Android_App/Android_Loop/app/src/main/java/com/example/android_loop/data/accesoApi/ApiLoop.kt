package com.example.android_loop.data.accesoApi

import com.example.android_loop.data.model_dataClass.GetUserDataResult
import com.example.android_loop.data.model_dataClass.LoginResult
import com.example.android_loop.data.model_dataClass.RpcResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ApiLoop(private val cliente: HttpClient = HttpClientProvider.cliente) {

    suspend fun login(username: String, password: String): Result<LoginResult> {
        return try {

            val response: RpcResponse<LoginResult> =
                cliente.post("${Servidor.BASE_URL}/api/v1/loop/auth") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("method", "call")
                            put("params", buildJsonObject {
                                put("username", username)
                                put("password", password)
                            })
                        }
                    )
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun registro(name: String, username: String, email: String, password: String): Result<Boolean> {

        return try {
            val response: RpcResponse<Boolean> =
                cliente.post("${Servidor.BASE_URL}/api/v1/loop/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        buildJsonObject {
                            put("jsonrpc", "2.0")
                            put("method", "call")
                            put("params", buildJsonObject {
                                put("data", buildJsonObject {
                                    put("name", name)
                                    put("username", username)
                                    put("email", email)
                                    put("password", password)
                                })
                            })
                        }
                    )
                }.body()

            Result.success(response.result)

        } catch (ex: Exception) {
            Result.failure(ex)
        }

    }

    suspend fun getUserData(token: String): Result<GetUserDataResult> {
        return try {
            val response: RpcResponse<GetUserDataResult> =
                cliente.get( "${Servidor.BASE_URL}/api/v1/loop/me" ) {
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
            Result.success(response.result)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

}