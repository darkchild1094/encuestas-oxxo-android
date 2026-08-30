package mx.com.getic.encuestasoxxo.ui.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.domain.GeneralSyncManager
import mx.com.getic.encuestasoxxo.utils.UpdateManager

data class ActualizacionDisponible(
    val versionName: String,
    val url: String,
    val obligatoria: Boolean,
    val novedades: String,
)

class SyncViewModel(
    private val syncManager: GeneralSyncManager,
    private val sessionManager: SessionManager,
    private val updateManager: UpdateManager,
    private val sesion: Sesion
) : ViewModel() {
    
    var estado by mutableStateOf("Iniciando...")
        private set
        
    var terminado by mutableStateOf(false)
        private set
        
    var updateAvailable by mutableStateOf<ActualizacionDisponible?>(null)
        private set

    init {
        iniciarSincronizacion()
    }

    fun iniciarSincronizacion() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    // Lanzamos búsqueda de actualización en paralelo con la sincronización
                    val updateJob = async {
                        updateManager.checarYDescargar { versionName, url, obligatoria, novedades ->
                            updateAvailable = ActualizacionDisponible(versionName, url, obligatoria, novedades)
                        }
                    }

                    // Solo empezamos la descarga de datos si no hay actualización crítica detectada aún
                    val syncJob = async {
                        estado = "Sincronizando datos en segundo plano..."
                        syncManager.sincronizarTodo(sesion.token, sesion.plazaId)
                    }

                    updateJob.await()
                    
                    // Si hay actualización obligatoria, nos detenemos aquí
                    if (updateAvailable?.obligatoria == true) {
                        estado = "Actualización obligatoria disponible"
                        return@coroutineScope
                    }

                    syncJob.await()
                    sessionManager.marcarSyncRealizado()
                    terminado = true
                }
            } catch (e: Exception) {
                estado = "Error: ${e.message}. Toca para reintentar."
            }
        }
    }
    
    fun descargarActualizacion() {
        updateAvailable?.let { updateManager.descargarEInstalar(it.url) }
    }
    
    fun ignorarActualizacion() {
        updateAvailable = null
        // Si no se terminó la sync por esperar a la actualización opcional, la marcamos como terminada
        // o dejamos que el flujo normal la termine. 
        // Como la lanzamos en paralelo, es probable que ya esté casi lista.
        viewModelScope.launch {
            if (!terminado) {
                try {
                    estado = "Finalizando descarga de catálogos..."
                    syncManager.sincronizarTodo(sesion.token, sesion.plazaId)
                    sessionManager.marcarSyncRealizado()
                    terminado = true
                } catch (e: Exception) {
                    estado = "Error al finalizar sync"
                }
            }
        }
    }
}
