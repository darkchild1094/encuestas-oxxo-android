package mx.com.getic.encuestasoxxo.ui.perfil

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.repository.UsuarioRepository
import timber.log.Timber

data class PerfilUiState(
    val nombre: String = "",
    val fotoUri: Uri? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val error: String? = null
)

class PerfilViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: SessionManager,
    private val sesion: Sesion
) : ViewModel() {

    var estado by mutableStateOf(PerfilUiState(nombre = sesion.nombreCompleto))
        private set

    fun onNombreChange(v: String) { estado = estado.copy(nombre = v) }
    fun onFotoSelected(uri: Uri?) { estado = estado.copy(fotoUri = uri) }
    fun onPasswordChange(v: String) { estado = estado.copy(password = v) }
    fun onConfirmChange(v: String) { estado = estado.copy(confirmPassword = v) }

    fun guardar(context: Context) {
        if (estado.password.isNotEmpty() && estado.password != estado.confirmPassword) {
            estado = estado.copy(error = "Las contraseñas no coinciden")
            return
        }

        estado = estado.copy(cargando = true, error = null, exito = false)
        viewModelScope.launch {
            try {
                // 1. Foto y Nombre
                val fotoFile = estado.fotoUri?.let { uri ->
                    repository.uriToFile(context.contentResolver, uri, context.cacheDir)
                }
                
                val resPerfil = repository.actualizarPerfil(estado.nombre, fotoFile)
                
                // 2. Password si se llenó
                if (estado.password.isNotBlank()) {
                    repository.cambiarPassword(estado.password)
                }

                // Actualizar sesión local (DataStore) con los nuevos datos
                val sesionActual = sessionManager.sesionActualBloqueante()
                if (sesionActual != null) {
                    // Nota: el DTO de UsuarioDto requiere todos los campos, pero solo actualizamos lo que cambió
                    // Una forma más limpia sería tener un método 'actualizarSesion' parcial en SessionManager
                    // Pero usaremos lo que hay.
                    sessionManager.guardarSesion(
                        token = sesionActual.token,
                        usuario = mx.com.getic.encuestasoxxo.data.remote.dto.UsuarioDto(
                            id = sesionActual.usuarioId,
                            correo = sesionActual.correo,
                            nombre_completo = estado.nombre,
                            foto_perfil = resPerfil.foto_perfil ?: sesionActual.fotoPerfil,
                            rol = sesionActual.rol,
                            gestiona_preguntas = sesionActual.gestionaPreguntas,
                            gestiona_usuarios = sesionActual.gestionaUsuarios,
                            es_encuestable = sesionActual.esEncuestable,
                            ve_resultados_tiendas = sesionActual.veResultadosTiendas,
                            plaza_id = sesionActual.plazaId,
                            plaza_nombre = sesionActual.plazaNombre
                        )
                    )
                }

                estado = estado.copy(cargando = false, exito = true, password = "", confirmPassword = "", fotoUri = null)
            } catch (e: Exception) {
                Timber.e(e, "Error al actualizar perfil")
                estado = estado.copy(cargando = false, error = "Error al actualizar: ${e.message}")
            }
        }
    }
}
