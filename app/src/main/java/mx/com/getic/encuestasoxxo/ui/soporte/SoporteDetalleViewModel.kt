package mx.com.getic.encuestasoxxo.ui.soporte

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.MensajeSoporteDto
import mx.com.getic.encuestasoxxo.data.remote.dto.TicketSoporteDto
import mx.com.getic.encuestasoxxo.data.repository.SoporteRepository
import java.io.File

data class SoporteDetalleUiState(
    val cargando: Boolean = false,
    val ticket: TicketSoporteDto? = null,
    val mensajes: List<MensajeSoporteDto> = emptyList(),
    val error: String? = null,
    val nuevoMensaje: String = "",
    val archivoEvidencia: File? = null,
    val enviandoMensaje: Boolean = false,
    val resolviendo: Boolean = false,
    val notasResolucion: String = ""
)

class SoporteDetalleViewModel(
    private val repository: SoporteRepository,
    private val sesion: Sesion,
    private val ticketId: Int
) : ViewModel() {

    var state by mutableStateOf(SoporteDetalleUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            state = state.copy(cargando = true, error = null)
            val result = repository.detalleTicket(ticketId)
            if (result != null) {
                state = state.copy(cargando = false, ticket = result.ticket, mensajes = result.mensajes)
            } else {
                state = state.copy(cargando = false, error = "No se pudo cargar el detalle.")
            }
        }
    }

    fun onMensajeChange(v: String) { state = state.copy(nuevoMensaje = v) }
    fun onArchivoChange(f: File?) { state = state.copy(archivoEvidencia = f) }
    fun onNotasResolucionChange(v: String) { state = state.copy(notasResolucion = v) }

    fun enviarComentario() {
        if (state.nuevoMensaje.isBlank() && state.archivoEvidencia == null) return

        viewModelScope.launch {
            state = state.copy(enviandoMensaje = true)
            val success = repository.comentarTicket(ticketId, state.nuevoMensaje, state.archivoEvidencia)
            if (success) {
                state = state.copy(enviandoMensaje = false, nuevoMensaje = "", archivoEvidencia = null)
                cargar()
            } else {
                state = state.copy(enviandoMensaje = false, error = "Error al enviar mensaje.")
            }
        }
    }

    fun resolverTicket() {
        if (sesion.rol != "WEBMASTER") return
        
        viewModelScope.launch {
            state = state.copy(resolviendo = true)
            val success = repository.resolverTicket(ticketId, state.notasResolucion)
            if (success) {
                state = state.copy(resolviendo = false, notasResolucion = "")
                cargar()
            } else {
                state = state.copy(resolviendo = false, error = "Error al resolver ticket.")
            }
        }
    }
}
