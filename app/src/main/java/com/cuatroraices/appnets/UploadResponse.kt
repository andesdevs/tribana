package com.cuatroraices.appnets

data class UploadResponse(
    val success: Boolean,
    val message: String,
    val url: String? // URL del archivo subido
)