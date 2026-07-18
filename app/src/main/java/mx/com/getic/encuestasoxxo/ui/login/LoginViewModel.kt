package mx.com.getic.encuestasoxxo.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.repository.AuthRepository
import mx.com.getic.encuestasoxxo.data.repository.ResultadoLogin

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var estado by mutableStateOf(LoginUiState())
        private set

    fun onCorreoChange(valor: String) {
        estado = estado.copy(correo = valor, error = null)
    }

    fun onPasswordChange(valor: String) {
        estado = estado.copy(password = valor, error = null)
    }

    // onExito recibe el rol para que la navegacion decida a que
    // pantalla mandar al usuario (mismo criterio que el panel web).
    fun login(onExito: (rol: String) -> Unit) {
        if (estado.correo.isBlank() || estado.password.isBlank()) {
            estado = estado.copy(error = "Pon tu correo y password.")
            return
        }
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            when (val resultado = authRepository.login(estado.correo.trim(), estado.password)) {
                is ResultadoLogin.Ok -> {
                    estado = estado.copy(cargando = false)
                    onExito(resultado.rol)
                }
                is ResultadoLogin.Error -> {
                    estado = estado.copy(cargando = false, error = resultado.mensaje)
                }
            }
        }
    }
}
