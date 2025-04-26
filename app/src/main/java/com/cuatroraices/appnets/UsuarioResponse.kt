data class UsuarioResponse(
    val success: Boolean,
    val usuario: Usuario?,
    val nombre: String,
    val rut: String,
    val email: String
)

data class Usuario(
    val nombre: String,
    val rut: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val ciudad: String?,  // Puede ser nulo
    val etiquetas: String?,  // Puede ser nulo
    val carnet_identidad: String?,  // Puede ser nulo
    val carnet_cuidadora: String?,  // Puede ser nulo
    val foto_perfil: String? // Puede ser nulo
)