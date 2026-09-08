package com.kernel94.pulsoti.ui.pfs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel94.pulsoti.data.local.AppDatabase
import com.kernel94.pulsoti.data.remote.EncuestaPFSDto
import com.kernel94.pulsoti.data.remote.ApiService
import com.kernel94.pulsoti.data.repository.EncuestaRepository
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
    private val encuestaRepository: EncuestaRepository,
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
    
    // Fuerza un barrido de TODAS las encuestas pendientes por la misma
    // ruta que usa el guardado y el WorkManager (subida en lote,
    // idempotente en el servidor). El parametro se conserva por
    // compatibilidad con la pantalla, pero el reintento no es por-encuesta.
    fun reintentar(encuestaId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Reintentando envío de pendientes (disparado por $encuestaId)")

            val exito = encuestaRepository.intentarSincronizarPendientes()

            if (!exito) {
                _uiState.value = _uiState.value.copy(
                    error = "No se pudo sincronizar. Se reintentará en segundo plano."
                )
            } else {
                cargarEncuestasPendientes()
            }
        }
    }
}
