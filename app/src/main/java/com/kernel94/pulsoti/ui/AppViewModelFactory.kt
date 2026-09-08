package com.kernel94.pulsoti.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kernel94.pulsoti.AppContainer
import com.kernel94.pulsoti.data.Sesion
import com.kernel94.pulsoti.ui.encuesta.EncuestaViewModel
import com.kernel94.pulsoti.ui.historial.HistorialViewModel
import com.kernel94.pulsoti.ui.login.ChangePasswordViewModel
import com.kernel94.pulsoti.ui.login.LoginViewModel
import com.kernel94.pulsoti.ui.preguntas.PreguntasViewModel
import com.kernel94.pulsoti.ui.areas.AreasViewModel
import com.kernel94.pulsoti.ui.usuarios.UsuariosViewModel
import com.kernel94.pulsoti.ui.tiendas.TiendasViewModel
import com.kernel94.pulsoti.ui.perfil.PerfilViewModel
import com.kernel94.pulsoti.ui.dashboard.DashboardViewModel
import com.kernel94.pulsoti.ui.pfs.PFSModuloViewModel
import com.kernel94.pulsoti.ui.sync.SyncViewModel
import com.kernel94.pulsoti.ui.soporte.SoporteViewModel
import com.kernel94.pulsoti.ui.soporte.SoporteDetalleViewModel

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
            AreasViewModel::class.java -> {
                AreasViewModel(container.encuestaRepository) as T
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
                    container.encuestaRepository,
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
