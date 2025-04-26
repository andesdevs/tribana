package com.cuatroraices.appnets

data class PerfilResponse(
    val tienePerfil: Boolean,
    val nombre: String?,
    val rut: String?,
    val email: String?,
    val direccion: String?,
    val telefono: String?,
    val ciudad: String?,
    val carnetIdentidad: String?
)
