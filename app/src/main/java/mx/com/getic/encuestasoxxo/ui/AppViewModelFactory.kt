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
import mx.com.getic.encuestasoxxo.ui.perfil.PerfilViewModel

/**
 * Factory para crear ViewModels con dependencias personalizadas.
 * Evita memory leaks y garantiza que cada ViewModel reciba las dependencias correctas.
 */
class AppViewModelFactory(
    private val container: AppContainer,
    private val sesion: Sesion? = null,
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            LoginViewModel::class.java -> {
                LoginViewModel(container.authRepository, container.sessionManager) as T
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
            PerfilViewModel::class.java -> {
                requireNotNull(sesion)
                PerfilViewModel(container.usuarioRepository, container.sessionManager, sesion) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
