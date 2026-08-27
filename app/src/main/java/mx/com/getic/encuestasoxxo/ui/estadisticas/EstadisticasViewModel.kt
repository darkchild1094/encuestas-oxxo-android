package mx.com.getic.encuestasoxxo.ui.estadisticas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import mx.com.getic.encuestasoxxo.data.repository.EstadisticasRepository

import timber.log.Timber

enum class TipoEstadistica {
    PFS, REGION_ATI, REGION_PLAZA
}

data class EstadisticasUiState(
    val cargando: Boolean = false,
    val datos: List<PromedioPreguntaDto> = emptyList(),
    val error: String? = null,
    val tipoSeleccionado: TipoEstadistica = TipoEstadistica.PFS,
    val desde: String? = null,
    val hasta: String? = null,
)

class EstadisticasViewModel(
    private val repository: EstadisticasRepository,
    private val sesion: Sesion
) : ViewModel() {

    var state by mutableStateOf(EstadisticasUiState())
        private set

    init {
        cargar(TipoEstadistica.PFS)
    }

    fun cargar(tipo: TipoEstadistica = state.tipoSeleccionado) {
        val plazaId = sesion.plazaId
        
        if (plazaId == null) {
            Timber.w("Intento de cargar estadísticas sin plazaId. Rol: ${sesion.rol}")
            state = state.copy(error = "El usuario no tiene una plaza asignada.")
            return
        }
        
        viewModelScope.launch {
            Timber.d("Cargando estadísticas tipo $tipo para plaza $plazaId")
            state = state.copy(cargando = true, error = null, tipoSeleccionado = tipo)
            
            val result = when (tipo) {
                TipoEstadistica.PFS -> repository.obtenerEstadisticasPfs(plazaId, state.desde, state.hasta)
                TipoEstadistica.REGION_ATI -> repository.obtenerEstadisticasRegionAtis(plazaId, state.desde, state.hasta)
                TipoEstadistica.REGION_PLAZA -> repository.obtenerEstadisticasRegionPlazas(plazaId, state.desde, state.hasta)
            }
            
            Timber.d("Estadísticas cargadas: ${result.size} items")
            
            state = state.copy(
                cargando = false,
                datos = result,
                error = if (result.isEmpty()) "No se encontraron datos de estadísticas para esta plaza." else null
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
