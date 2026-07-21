package mx.com.getic.encuestasoxxo.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.UsuarioRecordado
import mx.com.getic.encuestasoxxo.data.UsuariosRecordadosStore
import mx.com.getic.encuestasoxxo.data.repository.AuthRepository
import mx.com.getic.encuestasoxxo.data.repository.ResultadoLogin

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    // Cuando no es null, la pantalla muestra "modo cuenta elegida":
    // solo pide la contraseña de esta cuenta (no hay que volver a
    // teclear el correo).
    val cuentaSeleccionada: UsuarioRecordado? = null,
)

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val usuariosRecordadosStore: UsuariosRecordadosStore,
) : ViewModel() {
    var estado by mutableStateOf(LoginUiState())
        private set

    // Cuentas que ya han iniciado sesion en este dispositivo, mas
    // reciente primero. Sobrevive a cerrar sesion.
    val cuentasRecordadas: StateFlow<List<UsuarioRecordado>> =
        usuariosRecordadosStore.usuariosRecordados.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun onCorreoChange(valor: String) {
        estado = estado.copy(correo = valor, error = null)
    }

    fun onPasswordChange(valor: String) {
        estado = estado.copy(password = valor, error = null)
    }

    // El usuario toco una de las cuentas recordadas: se precarga el
    // correo y solo falta que teclee su contraseña.
    fun seleccionarCuenta(usuario: UsuarioRecordado) {
        estado = estado.copy(
            correo = usuario.correo,
            password = "",
            error = null,
            cuentaSeleccionada = usuario,
        )
    }

    // "No soy yo" / "Usar otra cuenta": vuelve a mostrar el campo de
    // correo en blanco para teclear una cuenta distinta.
    fun usarOtraCuenta() {
        estado = estado.copy(correo = "", password = "", error = null, cuentaSeleccionada = null)
    }

    fun olvidarCuenta(usuario: UsuarioRecordado) {
        viewModelScope.launch {
            usuariosRecordadosStore.olvidar(usuario.correo)
        }
        if (estado.cuentaSeleccionada?.correo == usuario.correo) {
            usarOtraCuenta()
        }
    }

    // onExito recibe el rol para que la navegacion decida a que
    // pantalla mandar al usuario (mismo criterio que el panel web).
    fun login(onExito: (rol: String, debeCambiar: Boolean) -> Unit) {
        if (estado.correo.isBlank() || estado.password.isBlank()) {
            estado = estado.copy(error = "Pon tu correo y password.")
            return
        }
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            when (val resultado = authRepository.login(estado.correo.trim(), estado.password)) {
                is ResultadoLogin.Ok -> {
                    estado = estado.copy(cargando = false)
                    onExito(resultado.rol, resultado.debeCambiarPassword)
                }
                is ResultadoLogin.Error -> {
                    estado = estado.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }
    }
}
