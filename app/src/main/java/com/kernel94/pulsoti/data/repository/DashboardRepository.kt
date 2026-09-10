package com.kernel94.pulsoti.data.repository

import com.kernel94.pulsoti.data.SessionManager
import com.kernel94.pulsoti.data.remote.ApiService
import com.kernel94.pulsoti.data.remote.dto.PromedioPreguntaDto
import com.kernel94.pulsoti.data.remote.dto.ResumenSistemaDto
import timber.log.Timber

class DashboardRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    private suspend fun token(): String = "Bearer " + (sessionManager.sesionActualBloqueante()?.token ?: "")

    suspend fun obtenerEstadisticasPfs(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasPfs(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasRegionAtis(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasRegionAtis(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasRegionPlazas(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasRegionPlazas(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasPlazaAtis(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasPlazaAtis(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasPlazaTiendas(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasPlazaTiendas(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasPfsIndividual(plazaId: Int, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasPfsIndividual(token(), plazaId, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerEstadisticasOficina(por: String, desde: String?, hasta: String?): List<PromedioPreguntaDto> = try {
        api.estadisticasOficina(token(), por, desde, hasta)
    } catch (e: Exception) {
        emptyList()
    }

    suspend fun obtenerResumenSistema(): ResumenSistemaDto? = try {
        api.estadisticasResumen(token())
    } catch (e: Exception) {
        Timber.e(e, "Error obteniendo resumen del sistema")
        null
    }
}
