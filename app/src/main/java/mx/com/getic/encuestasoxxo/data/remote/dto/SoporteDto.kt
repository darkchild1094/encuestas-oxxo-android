package mx.com.getic.encuestasoxxo.data.remote.dto

data class TicketSoporteDto(
    val id: Int,
    val usuario_id: Int,
    val usuario_nombre: String? = null,
    val asunto: String,
    val descripcion: String,
    val estatus: String,
    val notas_cierre: String? = null,
    val fecha_creacion: String,
    val fecha_actualizacion: String
)

data class MensajeSoporteDto(
    val id: Int,
    val ticket_id: Int,
    val usuario_id: Int,
    val usuario_nombre: String,
    val mensaje: String,
    val evidencia_ruta: String? = null,
    val fecha: String
)

data class DetalleTicketResponse(
    val ticket: TicketSoporteDto,
    val mensajes: List<MensajeSoporteDto>
)
