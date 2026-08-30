package mx.com.getic.encuestasoxxo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.com.getic.encuestasoxxo.AppContainer
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.ui.encuesta.EncuestaViewModel
import mx.com.getic.encuestasoxxo.ui.historial.HistorialViewModel
import mx.com.getic.encuestasoxxo.ui.login.ChangePasswordViewModel
import mx.com.getic.encuestasoxxo.ui.login.LoginViewModel
import mx.com.getic.encuestasoxxo.ui.preguntas.PreguntasViewModel
import mx.com.getic.encuestasoxxo.ui.usuarios.UsuariosViewModel
import mx.com.getic.encuestasoxxo.ui.tiendas.TiendasViewModel
import mx.com.getic.encuestasoxxo.ui.perfil.PerfilViewModel
import mx.com.getic.encuestasoxxo.ui.dashboard.DashboardViewModel
import mx.com.getic.encuestasoxxo.ui.pfs.PFSModuloViewModel
import mx.com.getic.encuestasoxxo.ui.sync.SyncViewModel
import mx.com.getic.encuestasoxxo.ui.soporte.SoporteViewModel
import mx.com.getic.encuestasoxxo.ui.soporte.SoporteDetalleViewModel

/**
 * Factory para crear ViewModels con dependencias personalizadas.
 * Evita memory leaks y garantiza que cada ViewModel reciba las dependencias correctas.
 */
class AppViewModelFactory(
    private val container: AppContainer,
    private val sesion: Sesion? = null,
    private val extraId: Int? = null,
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginViewModel::class.java -> {
                LoginViewModel(container.authRepository, container.sessionManager, container.usuariosRecordadosStore) as T
            }
            ChangePasswordViewModel::class.java -> {
                ChangePasswordViewModel(container.usuarioRepository) as T
            }
            EncuestaViewModel::class.java -> {
                requireNotNull(sesion) { "Sesion es requerido para EncuestaViewModel" }
                EncuestaViewModel(container.encuestaRepository, sesion) as T
            }
            HistorialViewModel::class.java -> {
                HistorialViewModel(container.encuestaRepository, container.sessionManager) as T
            }
            PreguntasViewModel::class.java -> {
                requireNotNull(sesion)
                PreguntasViewModel(container.encuestaRepository, sesion) as T
            }
            UsuariosViewModel::class.java -> {
                UsuariosViewModel(container.usuarioRepository, container.encuestaRepository) as T
            }
            TiendasViewModel::class.java -> {
                requireNotNull(sesion)
                TiendasViewModel(container.encuestaRepository, sesion) as T
            }
            PerfilViewModel::class.java -> {
                requireNotNull(sesion)
                PerfilViewModel(container.usuarioRepository, container.sessionManager, container.updateManager, sesion) as T
            }
            DashboardViewModel::class.java -> {
                requireNotNull(sesion)
                DashboardViewModel(container.dashboardRepository, sesion) as T
            }
            SoporteViewModel::class.java -> {
                requireNotNull(sesion)
                SoporteViewModel(container.soporteRepository, sesion) as T
            }
            SoporteDetalleViewModel::class.java -> {
                requireNotNull(sesion)
                requireNotNull(extraId)
                SoporteDetalleViewModel(container.soporteRepository, sesion, extraId) as T
            }
            PFSModuloViewModel::class.java -> {
                requireNotNull(sesion)
                PFSModuloViewModel(
                    container.database,
                    container.api,
                    container.encuestaSyncManager,
                    sesion.token
                ) as T
            }
            SyncViewModel::class.java -> {
                requireNotNull(sesion)
                SyncViewModel(
                    container.generalSyncManager,
                    container.sessionManager,
                    container.updateManager,
                    sesion
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
