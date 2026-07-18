package mx.com.getic.encuestasoxxo.data.remote.dto

// Coincide 1 a 1 con lo que regresa POST /api/login en encuestas_web
data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val id: Int,
    val correo: String,
    val nombre_completo: String?,
    val foto_perfil: String?,
    val plaza_id: Int?,
    val plaza_nombre: String?,
    val rol: String, // "ATI" | "WEBMASTER" | "PFS"
    val gestiona_preguntas: Boolean,
    val gestiona_usuarios: Boolean,
    val es_encuestable: Boolean,
    val ve_resultados_tiendas: Boolean
)

data class ErrorResponse(
    val error: String
)
