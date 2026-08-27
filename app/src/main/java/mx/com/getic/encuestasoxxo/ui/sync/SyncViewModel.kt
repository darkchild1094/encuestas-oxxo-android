package mx.com.getic.encuestasoxxo.ui.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.domain.GeneralSyncManager

class SyncViewModel(
    private val syncManager: GeneralSyncManager,
    private val sessionManager: SessionManager,
    private val sesion: Sesion
) : ViewModel() {
    
    var estado by mutableStateOf("Sincronizando datos...")
        private set
        
    var terminado by mutableStateOf(false)
        private set

    init {
        iniciarSincronizacion()
    }

    fun iniciarSincronizacion() {
        viewModelScope.launch {
            try {
                estado = "Descargando catálogos y tiendas..."
                syncManager.sincronizarTodo(sesion.token, sesion.plazaId)
                sessionManager.marcarSyncRealizado()
                terminado = true
            } catch (e: Exception) {
                estado = "Error: ${e.message}. Toca para reintentar."
            }
        }
    }
}
