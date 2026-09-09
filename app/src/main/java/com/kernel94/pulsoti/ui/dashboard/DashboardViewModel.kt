package com.kernel94.pulsoti.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel94.pulsoti.data.Sesion
import com.kernel94.pulsoti.data.remote.dto.PromedioPreguntaDto
import com.kernel94.pulsoti.data.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class DashboardUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val desde: String? = null,
    val hasta: String? = null,
    val atisPlaza: List<PromedioPreguntaDto> = emptyList(),
    val tiendasPlaza: List<PromedioPreguntaDto> = emptyList(),
    val atisRegion: List<PromedioPreguntaDto> = emptyList(),
    val pfsDesempeno: List<PromedioPreguntaDto> = emptyList(),
    val oficina: List<PromedioPreguntaDto> = emptyList(),
) {
    // KPIs derivados de "tiendas de la plaza" -- es el dato mas cercano
    // a "como esta el servicio de TI en mi plaza ahorita".
    val promedioGeneral: Double
        get() {
            val totalEncuestas = tiendasPlaza.sumOf { it.total_encuestas }
            if (totalEncuestas == 0) return 0.0
            return tiendasPlaza.sumOf { it.promedio * it.total_encuestas } / totalEncuestas
        }
    val encuestasTotales: Int get() = tiendasPlaza.sumOf { it.total_encuestas }
    val huboDatos: Boolean get() =
        atisPlaza.isNotEmpty() || tiendasPlaza.isNotEmpty() || atisRegion.isNotEmpty() ||
            pfsDesempeno.isNotEmpty() || oficina.isNotEmpty()
}

// Dashboard de ATI: una sola pantalla con todas las secciones (antes
// eran 4 pestañas separadas) -- vista completa de un vistazo: KPIs de
// la plaza, ranking de ATIs/tiendas/PFS y encuesta de oficina.
class DashboardViewModel(
    private val repository: DashboardRepository,
    private val sesion: Sesion
) : ViewModel() {

    var state by mutableStateOf(DashboardUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        val plazaId = sesion.plazaId
        if (plazaId == null) {
            state = state.copy(cargando = false, error = "El usuario no tiene una plaza asignada.")
            return
        }

        viewModelScope.launch {
            state = state.copy(cargando = true, error = null)
            val desde = state.desde
            val hasta = state.hasta

            // Las 5 secciones se piden en paralelo -- son consultas
            // independientes, esperar una por una tardaria 5x mas.
            val atisPlazaDef = async { repository.obtenerEstadisticasPlazaAtis(plazaId, desde, hasta) }
            val tiendasPlazaDef = async { repository.obtenerEstadisticasPlazaTiendas(plazaId, desde, hasta) }
            val atisRegionDef = async { repository.obtenerEstadisticasRegionAtis(plazaId, desde, hasta) }
            val pfsDef = async { repository.obtenerEstadisticasPfsIndividual(plazaId, desde, hasta) }
            val oficinaDef = async { repository.obtenerEstadisticasOficina(desde, hasta) }

            val nuevoEstado = state.copy(
                cargando = false,
                atisPlaza = atisPlazaDef.await(),
                tiendasPlaza = tiendasPlazaDef.await(),
                atisRegion = atisRegionDef.await(),
                pfsDesempeno = pfsDef.await(),
                oficina = oficinaDef.await(),
            )
            state = nuevoEstado.copy(
                error = if (!nuevoEstado.huboDatos) "No se encontraron datos para este filtro." else null
            )
        }
    }

    fun establecerDesde(fecha: String?) {
        state = state.copy(desde = fecha)
        cargar()
    }

    fun establecerHasta(fecha: String?) {
        state = state.copy(hasta = fecha)
        cargar()
    }

    fun limpiarFechas() {
        state = state.copy(desde = null, hasta = null)
        cargar()
    }
}
