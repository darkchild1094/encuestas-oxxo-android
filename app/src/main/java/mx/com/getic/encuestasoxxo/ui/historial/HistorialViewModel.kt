package mx.com.getic.encuestasoxxo.ui.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.remote.dto.RespuestaFilaDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository

data class EncuestaResumen(
    val encuestaId: String,
    val fecha: String,
    val tienda: String,
    val tiendaCodigo: String,
    val comentario: String?,
    val calificaciones: List<Pair<String, Int>>, // pregunta -> calificacion
)

data class HistorialUiState(
    val cargando: Boolean = true,
    val encuestas: List<EncuestaResumen> = emptyList(),
    val error: String? = null,
)

class HistorialViewModel(private val repository: EncuestaRepository) : ViewModel() {
    var estado by mutableStateOf(HistorialUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            try {
                val filas = repository.obtenerRespuestas()
                estado = estado.copy(cargando = false, encuestas = agrupar(filas))
            } catch (e: Exception) {
                estado = estado.copy(cargando = false, error = "No se pudo cargar el historial. Revisa tu conexion.")
            }
        }
    }

    private fun agrupar(filas: List<RespuestaFilaDto>): List<EncuestaResumen> {
        return filas.groupBy { it.encuesta_id }.map { (id, grupo) ->
            val primera = grupo.first()
            EncuestaResumen(
                encuestaId = id,
                fecha = primera.fecha_creacion_local,
                tienda = primera.tienda,
                tiendaCodigo = primera.tienda_codigo,
                comentario = primera.comentario,
                calificaciones = grupo.map { it.pregunta to it.calificacion },
            )
        }
    }
}