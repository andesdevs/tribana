package com.cuatroraices.appnets

data class SubidaResponse(
    val success: Boolean,
    val message: String,
    val mensaje: String,
    val url: String?,
    val urlFoto: String? = null,
    val error: Boolean
)
