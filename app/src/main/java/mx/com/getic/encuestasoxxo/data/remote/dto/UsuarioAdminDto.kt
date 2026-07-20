package mx.com.getic.encuestasoxxo.data.remote.dto

data class RolDto(
    val id: Int,
    val nombre: String
)

data class CrearEditarUsuarioRequest(
    val id: Int? = null,
    val correo: String? = null,
    val nombre_completo: String,
    val rol_id: Int,
    val plaza_id: Int?,
    val password: String? = null
)

data class OperacionUsuarioResponse(
    val success: Boolean,
    val error: String? = null,
    val foto_perfil: String? = null
)
