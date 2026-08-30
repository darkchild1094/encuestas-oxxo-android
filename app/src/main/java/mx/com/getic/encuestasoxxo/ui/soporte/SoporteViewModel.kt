package mx.com.getic.encuestasoxxo.ui.soporte

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.TicketSoporteDto
import mx.com.getic.encuestasoxxo.data.repository.SoporteRepository
import java.io.File

data class SoporteUiState(
    val cargando: Boolean = false,
    val tickets: List<TicketSoporteDto> = emptyList(),
    val error: String? = null,
    val asunto: String = "",
    val descripcion: String = "",
    val archivoEvidencia: File? = null,
    val guardando: Boolean = false,
    val exitoGuardado: Boolean = false
)

class SoporteViewModel(
    private val repository: SoporteRepository,
    private val sesion: Sesion
) : ViewModel() {

    var state by mutableStateOf(SoporteUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            state = state.copy(cargando = true, error = null)
            val result = if (sesion.rol == "WEBMASTER") {
                repository.adminTickets()
            } else {
                repository.misTickets()
            }
            state = state.copy(cargando = false, tickets = result)
        }
    }

    fun onAsuntoChange(v: String) { state = state.copy(asunto = v) }
    fun onDescripcionChange(v: String) { state = state.copy(descripcion = v) }
    fun onArchivoChange(f: File?) { state = state.copy(archivoEvidencia = f) }

    fun crearTicket(onListo: () -> Unit) {
        if (state.asunto.isBlank() || state.descripcion.isBlank()) {
            state = state.copy(error = "El asunto y descripción son requeridos.")
            return
        }

        viewModelScope.launch {
            state = state.copy(guardando = true, error = null)
            val success = repository.crearTicket(state.asunto, state.descripcion, state.archivoEvidencia)
            if (success) {
                state = state.copy(guardando = false, exitoGuardado = true, asunto = "", descripcion = "", archivoEvidencia = null)
                cargar()
                onListo()
            } else {
                state = state.copy(guardando = false, error = "Error al enviar el reporte. Revisa tu conexión.")
            }
        }
    }
    
    fun resetExito() { state = state.copy(exitoGuardado = false) }
}
