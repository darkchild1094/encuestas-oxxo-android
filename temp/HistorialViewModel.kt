package mx.com.getic.encuestasoxxo.ui.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.remote.dto.RespuestaFilaDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository

data class HistorialUiState(
    val cargando: Boolean = true,
    val respuestas: List<RespuestaFilaDto> = emptyList(),
    val error: String? = null,
)

class HistorialViewModel(
    private val repository: EncuestaRepository,
    private val sessionManager: SessionManager,  // Para filtrar si es necesario
) : ViewModel() {
    var estado by mutableStateOf(HistorialUiState())
        private set

    init {
        cargarRespuestas()
    }

    private fun cargarRespuestas() {
        viewModelScope.launch {
            try {
                val respuestas = repository.obtenerRespuestas()
                estado = estado.copy(respuestas = respuestas, cargando = false)
            } catch (e: Exception) {
                estado = estado.copy(
                    cargando = false,
                    error = "No se pudieron cargar las respuestas. Revisa tu conexion."
                )
            }
        }
    }

    fun reintentar() {
        estado = estado.copy(cargando = true, error = null)
        cargarRespuestas()
    }
}
