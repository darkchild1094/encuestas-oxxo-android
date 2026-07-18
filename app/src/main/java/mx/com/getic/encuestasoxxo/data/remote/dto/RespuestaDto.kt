package mx.com.getic.encuestasoxxo.data.remote.dto

data class RespuestaFilaDto(
    val encuesta_id: String,
    val fecha_creacion_local: String,
    val comentario: String?,
    val tienda: String,
    val tienda_codigo: String,
    val pregunta: String,
    val calificacion: Int
)