package com.kernel94.pulsoti.data.remote.dto

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

// Una encuesta es de TIENDA (tienda_id) o de OFICINA (administracion_id):
// exactamente uno de los dos va NO nulo, nunca los dos ni ninguno.
data class EncuestaSyncDto(
    val id: String, // uuid
    val folio: String,
    val tienda_id: Int? = null,
    val administracion_id: Int? = null,
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

data class EncuestaFallidaDto(
    val id: String?,
    val folio: String?,
    val error: String?,
)

data class SubirEncuestasResponse(
    val sincronizadas: List<String>,
    val fallidas: List<EncuestaFallidaDto> = emptyList(),
)
