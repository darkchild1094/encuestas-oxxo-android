package mx.com.getic.encuestasoxxo.ui.pfs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mx.com.getic.encuestasoxxo.data.local.AppDatabase
import mx.com.getic.encuestasoxxo.data.remote.EncuestaPFSDto
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.domain.EncuestaSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PFSUiState(
    val cargando: Boolean = false,
    val encuestas: List<EncuestaPFSDto> = emptyList(),
    val totalEncuestas: Int = 0,
    val tiendaId: Int? = null,
    val error: String? = null,
    val ultimaActualizacion: Long = 0L
)

class PFSModuloViewModel(
    private val db: AppDatabase,
    private val apiService: ApiService,
    private val syncManager: EncuestaSyncManager,
    private val token: String
) : ViewModel() {
    
    companion object {
        private const val TAG = "PFSModuloViewModel"
    }
    
    private val _uiState = MutableStateFlow(PFSUiState())
    val uiState: StateFlow<PFSUiState> = _uiState
    
    fun cargarEncuestasPendientes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, error = null)
            
            try {
                val response = apiService.obtenerEncuestasPendientesPFS("Bearer $token")
                
                Log.d(TAG, "Cargadas ${response.total_encuestas} encuestas de tienda ${response.tienda_id}")
                
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    encuestas = response.encuestas,
                    totalEncuestas = response.total_encuestas,
                    tiendaId = response.tienda_id,
                    ultimaActualizacion = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando encuestas: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun reintentar(encuestaId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Reintentando envío de encuesta: $encuestaId")
            
            val resultadoReintentos = syncManager.reintentar(
                token = token,
                encuestaId = encuestaId
            ) { _ ->
                false
            }
            
            if (resultadoReintentos.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = "Error en reintento: ${resultadoReintentos.exceptionOrNull()?.message}"
                )
            } else {
                cargarEncuestasPendientes()
            }
        }
    }
    
    fun verDetallesError(encuestaId: String): String? {
        Log.d(TAG, "Viendo detalles de error para: $encuestaId")
        return null
    }
}
