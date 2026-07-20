package mx.com.getic.encuestasoxxo.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.repository.UsuarioRepository

class ChangePasswordViewModel(private val repository: UsuarioRepository) : ViewModel() {
    var password by mutableStateOf("")
    var confirmacion by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var exito by mutableStateOf(false)

    fun cambiar(onExito: () -> Unit) {
        if (password.length < 6) {
            error = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        if (password != confirmacion) {
            error = "Las contraseñas no coinciden."
            return
        }
        cargando = true
        error = null
        viewModelScope.launch {
            try {
                val res = repository.cambiarPassword(password)
                if (res.success) {
                    exito = true
                    onExito()
                } else {
                    error = res.error ?: "Error desconocido"
                }
            } catch (e: Exception) {
                error = "Error de conexión"
            } finally {
                cargando = false
            }
        }
    }
}

@Composable
fun ChangePasswordScreen(viewModel: ChangePasswordViewModel, onExito: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Text("Cambiar contraseña obligatoria", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Por seguridad, debes cambiar la contraseña temporal que se te asignó.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.confirmacion,
                onValueChange = { viewModel.confirmacion = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = { viewModel.cambiar(onExito) },
                enabled = !viewModel.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.cargando) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Actualizar y Continuar")
            }
        }
    }
}
