package com.cuatroraices.appnets

data class DashboardResponse(
    val total_postulaciones: Int,
    val ultima_postulacion: Postulacion?,
    val total_ganado: Int
)

data class Postulacion(
    val titulo: String,
    val empresa: String,
    val sueldo: Int
)
