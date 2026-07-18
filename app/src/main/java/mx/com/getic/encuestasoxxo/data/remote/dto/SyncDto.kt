package mx.com.getic.encuestasoxxo.data.remote.dto

// Coincide con GET /api/cuestionario?plaza_id=
data class CuestionarioResponse(
    val cuestionario: CuestionarioDto?,
    val preguntas: List<PreguntaDto>
)

data class CuestionarioDto(
    val id: Int,
    val nombre: String
)

data class PreguntaDto(
    val id: Int,
    val texto: String,
    val orden: Int
)

// Coincide con el body que espera POST /api/encuestas
data class SubirEncuestasRequest(
    val encuestas: List<EncuestaSyncDto>
)

data class EncuestaSyncDto(
    val id: String, // uuid
    val tienda_id: Int,
    val cuestionario_id: Int,
    val comentario: String?,
    val fecha_creacion_local: String,
    val respuestas: List<RespuestaSyncDto>
)

data class RespuestaSyncDto(
    val id: String, // uuid
    val pregunta_id: Int,
    val calificacion: Int // 1-10
)

data class SubirEncuestasResponse(
    val sincronizadas: List<String>
)
