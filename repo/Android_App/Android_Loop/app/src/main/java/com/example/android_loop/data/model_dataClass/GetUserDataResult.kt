package com.example.android_loop.data.model_dataClass

import kotlinx.serialization.Serializable

@Serializable
data class GetUserDataResult (
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val mobile: String?,
    val idioma: String,
    val image_1920: String
)