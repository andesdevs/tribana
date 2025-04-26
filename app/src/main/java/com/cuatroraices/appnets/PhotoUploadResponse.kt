package com.cuatroraices.appnets

data class PhotoUploadResponse(
    val error: Boolean,
    val mensaje: String,
    val foto: String? // La URL final de la foto actualizada
)