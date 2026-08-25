package mx.com.getic.encuestasoxxo.data.remote.dto

data class RespuestaFilaDto(
    val encuesta_id: String,
    val fecha_creacion_local: String,
    val comentario: String?,
    val tienda: String,
    val tienda_codigo: String,
    val ati_id: Int?,
    val ati_nombre: String?,
    val pregunta: String,
    val calificacion: Int
)

data class EncuestaNuevaDto(
    val id: String,
    val fecha_creacion_local: String,
    val tienda: String,
    val tienda_codigo: String,
)

data class EncuestasNuevasResponse(
    val encuestas: List<EncuestaNuevaDto>,
    val ultima_fecha: String,
)