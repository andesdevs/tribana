package com.cuatroraices.appnets

import UsuarioResponse
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface ApiService {

    @Multipart
    @POST("guardarPerfilConDocumentos.php")
    fun guardarPerfilConDocumentos(
        @Part("nombre") nombre: RequestBody,
        @Part("rut") rut: RequestBody,
        @Part("direccion") direccion: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part("etiquetas") etiquetas: RequestBody,
        @Part("email") email: RequestBody,
        @Part("ciudad") ciudad: RequestBody,
        @Part carnetIdentidad: MultipartBody.Part?,
        @Part carnetCuidadora: MultipartBody.Part?
    ): Call<PerfilResponse>

    @FormUrlEncoded
    @POST("verificar_perfil.php")
    fun verificarPerfil(@Field("email") email: String): Call<PerfilResponse>

    @Multipart
    @POST("subir_documento.php")
    fun subirDocumento(
        @Part("email") email: RequestBody,
        @Part("tipo") tipo: RequestBody,
        @Part archivo: MultipartBody.Part
    ): Call<SubidaResponse>

    @GET("obtener_etiquetas.php")
    fun obtenerEtiquetas(): Call<EtiquetasResponse>

    // No se agregan nuevos parámetros aquí
    @FormUrlEncoded
    @POST("guardar_perfil.php")
    fun guardarPerfil(
        @Field("nombre") nombre: String,
        @Field("rut") rut: String,
        @Field("direccion") direccion: String,
        @Field("telefono") telefono: String,
        @Field("etiquetas") etiquetas: String,
        @Field("email") email: String
    ): Call<PerfilResponse>

    @GET("getOfertas.php")
    fun getOfertas(): Call<List<Oferta>>

    @FormUrlEncoded
    @POST("postularOferta.php")
    fun postularOferta(
        @Field("id_oferta") idOferta: Int,
        @Field("rut_postulante") rut: String,
        @Field("email_postulante") email: String
    ): Call<PostulacionResponse>

    @GET("obtenerUsuario.php")
    fun obtenerPerfil(@Query("email") email: String): Call<UsuarioResponse>

    @GET("verificarPostulaciones.php")
    fun verificarPostulaciones(
        @Query("email") email: String,
        @Query("rut") rut: String
    ): Call<PostulacionesResponse>

    @GET("verificarPostulaciones.php")
    fun obtenerPostulacionesUsuario(@Query("email") email: String): Call<PostulacionesResponse>

    // Nueva función para cargar ciudades (opcional)
    @GET("obtener_ciudades.php")
    fun obtenerCiudades(): Call<List<String>>

    @GET("get_dashboard.php")
    fun getDashboard(@Query("rut") rut: String): Call<DashboardResponse>

    @GET("get_user.php")
    fun getUser(@Query("rut") rut: String): Call<UserResponse>

    @Multipart
    @POST("update_foto_perfil.php")
    fun updateFotoPerfil(
        @Part("rut") rut: RequestBody,
        @Part foto: MultipartBody.Part
    ): Call<PhotoUploadResponse>

    @Multipart
    @POST("update_usuario_perfil.php")
    fun actualizarPerfil(
        @Part("rut") rut: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part("direccion") direccion: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part("ciudad") ciudad: RequestBody,
        @Part("etiquetas") etiquetas: RequestBody,
        @Part carnet_identidad: MultipartBody.Part?,
        @Part carnet_cuidadora: MultipartBody.Part?
    ): Call<UpdateUsuarioResponse>

    @GET("get_usuario_perfil.php")
    fun obtenerPerfilPorRut(@Query("rut") rut: String): Call<UsuarioResponse>

}



