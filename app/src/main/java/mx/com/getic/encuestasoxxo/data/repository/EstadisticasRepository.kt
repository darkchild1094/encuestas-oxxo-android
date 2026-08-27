package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import timber.log.Timber

class EstadisticasRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    private suspend fun token(): String = "Bearer " + (sessionManager.sesionActualBloqueante()?.token ?: "")

    suspend fun obtenerEstadisticasPfs(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        Timber.d("Pidiendo estadísticas PFS para plaza $plazaId")
        api.estadisticasPfs(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        Timber.e(e, "Error al obtener estadísticas PFS")
        emptyList()
    }

    suspend fun obtenerEstadisticasRegionAtis(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        Timber.d("Pidiendo estadísticas región ATIs para plaza $plazaId")
        api.estadisticasRegionAtis(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        Timber.e(e, "Error al obtener estadísticas región por ATI")
        emptyList()
    }

    suspend fun obtenerEstadisticasRegionPlazas(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        Timber.d("Pidiendo estadísticas región Plazas para plaza $plazaId")
        api.estadisticasRegionPlazas(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        Timber.e(e, "Error al obtener estadísticas región por Plaza")
        emptyList()
    }
}
