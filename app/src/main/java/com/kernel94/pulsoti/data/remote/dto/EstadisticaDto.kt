package com.kernel94.pulsoti.data.remote.dto

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

// Coincide con GET /api/estadisticas/resumen (WEBMASTER)
data class ResumenSistemaDto(
    val usuarios_por_rol: List<UsuariosPorRolDto>,
    val conteos: ConteosResumenDto,
    val tokens_activos: Int?,
    val ultimas_encuestas: List<EncuestaRecienteDto>,
    val version_app: VersionAppDto?,
)

data class UsuariosPorRolDto(val rol: String, val total: Int)

data class ConteosResumenDto(
    val usuarios: Int,
    val tiendas: Int,
    val plazas: Int,
    val areas: Int,
    val encuestas: Int,
    val encuestas_tienda: Int,
    val encuestas_oficina: Int,
    val encuestas_7d: Int,
    val encuestas_30d: Int,
    val pendientes_password: Int,
)

data class EncuestaRecienteDto(
    val fecha: String,
    val tipo: String, // "tienda" | "oficina"
    val lugar: String,
)

data class VersionAppDto(
    val version_code: Int? = null,
    val version_name: String? = null,
    val obligatoria: Boolean? = null,
    val novedades: String? = null,
)
