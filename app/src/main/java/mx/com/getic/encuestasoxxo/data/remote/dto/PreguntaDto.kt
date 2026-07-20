package mx.com.getic.encuestasoxxo.data.remote.dto

data class CrearPreguntaRequest(
    val cuestionario_id: Int,
    val texto: String,
    val orden: Int
)

data class EditarPreguntaRequest(
    val id: Int,
    val texto: String,
    val orden: Int
)

data class PreguntaOperacionResponse(
    val id: Int? = null,
    val mensaje: String? = null,
    val error: String? = null
)
