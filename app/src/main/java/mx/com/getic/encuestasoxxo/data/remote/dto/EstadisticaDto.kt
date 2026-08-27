package mx.com.getic.encuestasoxxo.data.remote.dto

/**
 * Representa el promedio de calificación para una pregunta específica
 * en un contexto determinado (PFS, Plaza, Región).
 */
data class PromedioPreguntaDto(
    val pregunta_id: Int,
    val pregunta_texto: String,
    val promedio: Double,
    val total_encuestas: Int
)

/**
 * Agrupa los promedios por una entidad (por ejemplo, por un ATI o por una Plaza)
 */
data class EstadisticaEntidadDto(
    val entidad_id: Int,
    val entidad_nombre: String,
    val promedios: List<PromedioPreguntaDto>
)

/**
 * Respuesta genérica para los dashboards de estadísticas
 */
data class EstadisticasResponse(
    val titulo: String,
    val items: List<PromedioPreguntaDto>
)
