package com.example.android_loop.data.model_dataClass

import kotlinx.serialization.Serializable

@Serializable
data class RpcResponse<T> (
    val jsonrpc: String,
    val id: Int? = null,
    val result: T
)