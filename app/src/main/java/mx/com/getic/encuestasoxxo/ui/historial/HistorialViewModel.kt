package mx.com.getic.encuestasoxxo.ui.historial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.remote.dto.RespuestaFilaDto
import mx.com.getic.encuestasoxxo.data.remote.dto.AtiDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository

data class EncuestaResumen(
    val encuestaId: String,
    val fecha: String,
    val tienda: String,
    val tiendaCodigo: String,
    val atiId: Int?,
    val atiNombre: String?,
    val comentario: String?,
    val calificaciones: List<Pair<String, Int>>, // pregunta -> calificacion
)

data class HistorialUiState(
    val cargando: Boolean = true,
    val encuestas: List<EncuestaResumen> = emptyList(),
    val atis: List<AtiDto> = emptyList(),
    val atiSeleccionadoId: Int? = null,
    val error: String? = null,
)

class HistorialViewModel(
    private val repository: EncuestaRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    var estado by mutableStateOf(HistorialUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            try {
                val sesion = sessionManager.sesionActualBloqueante()
                val atisDeLaPlaza = sesion?.plazaId?.let { repository.atisDisponibles(it) }.orEmpty()
                val filas = repository.obtenerRespuestas()
                val atis = atisDeLaPlaza.ifEmpty {
                    filas.mapNotNull { fila ->
                        fila.ati_id?.let { AtiDto(it, fila.ati_nombre ?: "ATI", null) }
                    }.distinctBy { it.id }
                }
                estado = estado.copy(
                    cargando = false,
                    encuestas = agrupar(filas),
                    atis = atis,
                    atiSeleccionadoId = atis.firstOrNull()?.id,
                )
            } catch (e: Exception) {
                estado = estado.copy(cargando = false, error = "No se pudieron cargar las respuestas. Revisa tu conexion.")
            }
        }
    }

    fun seleccionarAti(atiId: Int) {
        estado = estado.copy(atiSeleccionadoId = atiId)
    }

    private fun agrupar(filas: List<RespuestaFilaDto>): List<EncuestaResumen> {
        return filas.groupBy { it.encuesta_id }.map { (id, grupo) ->
            val primera = grupo.first()
            EncuestaResumen(
                encuestaId = id,
                fecha = primera.fecha_creacion_local,
                tienda = primera.tienda,
                tiendaCodigo = primera.tienda_codigo,
                atiId = primera.ati_id,
                atiNombre = primera.ati_nombre,
                comentario = primera.comentario,
                calificaciones = grupo.map { it.pregunta to it.calificacion },
            )
        }
    }
}
