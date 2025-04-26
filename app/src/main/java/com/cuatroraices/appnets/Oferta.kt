package com.cuatroraices.appnets

data class Oferta(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val descripcion_larga: String,
    val habilidades: String,
    val ubicacion: String,
    val modalidad: String,
    val estado: String,
    val fecha_publicacion: String,
    val remuneracion: Int,  // 💰 Remuneración en CLP
    val nombre_empresa: String,
    val categoria: String,  // 🔹 Agregado
    val en_top3: Int        //// 🏢 Nombre de la empresa
)
