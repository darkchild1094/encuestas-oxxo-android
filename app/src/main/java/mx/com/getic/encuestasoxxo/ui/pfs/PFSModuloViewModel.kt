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
                // 1. Intentar cargar desde la API
                val response = apiService.obtenerEncuestasPendientesPFS("Bearer $token")
                
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    encuestas = response.encuestas,
                    totalEncuestas = response.total_encuestas,
                    ultimaActualizacion = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando encuestas de API: ${e.message}, intentando local...")
                
                // 2. Si falla la API (offline), cargar desde la base de datos local
                try {
                    val locales = db.encuestaDao().pendientesDeSincronizar()
                    
                    val mapeadas = locales.map { e ->
                        val tienda = db.tiendaDao().obtener(e.tiendaId)
                        val log = db.encuestaSyncLogDao().obtenerUltimoPorEncuesta(e.id)
                        
                        EncuestaPFSDto(
                            id = e.id,
                            tienda_id = e.tiendaId,
                            tienda_nombre = tienda?.nombre ?: "Tienda ${e.tiendaId}",
                            folio = e.folio,
                            fecha_creacion_local = e.fechaCreacionLocal,
                            comentario = e.comentario,
                            sincronizado = false,
                            estado = log?.estado ?: "pendiente",
                            intento_numero = log?.intento_numero ?: 0,
                            mensaje_error = log?.mensaje_error,
                            fecha_intento = null,
                            fecha_confirmacion = null,
                            total_respuestas = 0 // No crítico para esta vista
                        )
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        encuestas = mapeadas,
                        totalEncuestas = mapeadas.size,
                        ultimaActualizacion = System.currentTimeMillis()
                    )
                } catch (localE: Exception) {
                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        error = "Sin conexión y error al leer local: ${localE.message}"
                    )
                }
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
