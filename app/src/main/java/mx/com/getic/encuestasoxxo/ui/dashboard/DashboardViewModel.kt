package mx.com.getic.encuestasoxxo.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import mx.com.getic.encuestasoxxo.data.repository.DashboardRepository

enum class TipoDashboard {
    ATI_PLAZA, TIENDAS_PLAZA, ATI_REGION, PFS_PERFORMANCE
}

data class DashboardUiState(
    val cargando: Boolean = false,
    val datos: List<PromedioPreguntaDto> = emptyList(),
    val error: String? = null,
    val tipoSeleccionado: TipoDashboard = TipoDashboard.ATI_PLAZA,
    val desde: String? = null,
    val hasta: String? = null,
)

class DashboardViewModel(
    private val repository: DashboardRepository,
    private val sesion: Sesion
) : ViewModel() {

    var state by mutableStateOf(DashboardUiState())
        private set

    init {
        cargar(TipoDashboard.ATI_PLAZA)
    }

    fun cargar(tipo: TipoDashboard = state.tipoSeleccionado) {
        val plazaId = sesion.plazaId
        
        if (plazaId == null) {
            state = state.copy(error = "El usuario no tiene una plaza asignada.")
            return
        }
        
        viewModelScope.launch {
            state = state.copy(cargando = true, error = null, tipoSeleccionado = tipo)
            
            val result = when (tipo) {
                TipoDashboard.ATI_PLAZA -> repository.obtenerEstadisticasPlazaAtis(plazaId, state.desde, state.hasta)
                TipoDashboard.TIENDAS_PLAZA -> repository.obtenerEstadisticasPlazaTiendas(plazaId, state.desde, state.hasta)
                TipoDashboard.ATI_REGION -> repository.obtenerEstadisticasRegionAtis(plazaId, state.desde, state.hasta)
                TipoDashboard.PFS_PERFORMANCE -> repository.obtenerEstadisticasPfsIndividual(plazaId, state.desde, state.hasta)
            }
            
            state = state.copy(
                cargando = false,
                datos = result,
                error = if (result.isEmpty()) "No se encontraron datos para este reporte." else null
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
