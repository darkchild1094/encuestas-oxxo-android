package com.kernel94.pulsoti.ui.encuestaoficina

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel94.pulsoti.data.Sesion
import com.kernel94.pulsoti.data.remote.dto.AdministracionDto
import com.kernel94.pulsoti.data.remote.dto.PreguntaDto
import com.kernel94.pulsoti.data.repository.EncuestaRepository
import com.kernel94.pulsoti.data.repository.GuardadoEncuestaResult
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EncuestaOficinaUiState(
    val cargando: Boolean = true,
    val areas: List<AdministracionDto> = emptyList(),
    val areaId: Int? = null,
    val areaSeleccionada: AdministracionDto? = null,
    val cuestionarioId: Int? = null,
    val preguntas: List<PreguntaDto> = emptyList(),
    val calificaciones: Map<Int, Int> = emptyMap(), // preguntaId -> 0..10
    val comentario: String = "",
    val enviando: Boolean = false,
    val enviadoOk: Boolean = false,
    val ultimoIdGenerado: String = "",
    val error: String? = null,
)

// Contraparte de EncuestaViewModel (PFS/tienda) pero para el ATI
// contestando sobre un area administrativa. Mucho mas simple: sin
// cascada negocio/region/plaza, sin selector de ATI (el que responde
// ES el ATI logueado -- eso se muestra, no se elige), y el envio va
// siempre en linea (ver EncuestaRepository.enviarEncuestaOficina).
class EncuestaOficinaViewModel(
    private val repository: EncuestaRepository,
    private val sesion: Sesion,
) : ViewModel() {
    var estado by mutableStateOf(EncuestaOficinaUiState())
        private set

    init {
        cargar()
    }

    private fun cargar() {
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            try {
                val areas = repository.administraciones()
                val resultado = repository.obtenerPreguntasOficina()
                if (resultado == null) {
                    estado = estado.copy(
                        cargando = false,
                        areas = areas,
                        error = "La encuesta de oficina no esta disponible todavia. Avisa al webmaster.",
                    )
                    return@launch
                }
                estado = estado.copy(
                    cargando = false,
                    areas = areas,
                    cuestionarioId = resultado.cuestionarioId,
                    preguntas = resultado.preguntas,
                )
            } catch (e: Exception) {
                Timber.e(e, "Error al cargar encuesta de oficina")
                estado = estado.copy(cargando = false, error = "No se pudo cargar. Revisa tu conexion.")
            }
        }
    }

    fun onAreaSeleccionada(id: Int) {
        val area = estado.areas.firstOrNull { it.id == id }
        estado = estado.copy(areaId = id, areaSeleccionada = area)
    }

    // "CAMBIAR" en el encabezado, una vez ya elegida el area -- mismo
    // comportamiento que onTiendaSeleccionada(-1) en la encuesta de
    // tienda: vuelve a mostrar el selector y limpia lo que ya se habia
    // calificado (las preguntas/cuestionario no cambian, son globales).
    fun cambiarArea() {
        estado = estado.copy(
            areaId = null,
            areaSeleccionada = null,
            calificaciones = emptyMap(),
            comentario = "",
        )
    }

    fun onCalificar(preguntaId: Int, calificacion: Int) {
        estado = estado.copy(calificaciones = estado.calificaciones + (preguntaId to calificacion))
    }

    fun onComentarioChange(valor: String) {
        estado = estado.copy(comentario = valor)
    }

    val faltanPorCalificar: Int get() = estado.preguntas.count { estado.calificaciones[it.id] == null }

    fun enviar() {
        val areaId = estado.areaId
        val cuestionarioId = estado.cuestionarioId
        if (areaId == null) {
            estado = estado.copy(error = "Selecciona un área.")
            return
        }
        if (cuestionarioId == null) return
        if (faltanPorCalificar > 0) {
            estado = estado.copy(error = "Falta calificar $faltanPorCalificar pregunta(s).")
            return
        }

        estado = estado.copy(enviando = true, error = null)
        viewModelScope.launch {
            // Folio simbolico: la encuesta de oficina no tiene un numero
            // de incidente como la de tienda, pero el backend exige uno.
            // Sirve ademas para distinguir el canal en el reporte Excel.
            val folio = "OFI-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())

            when (
                val resultado = repository.enviarEncuestaOficina(
                    administracionId = areaId,
                    cuestionarioId = cuestionarioId,
                    folio = folio,
                    comentario = estado.comentario,
                    calificaciones = estado.calificaciones,
                )
            ) {
                is GuardadoEncuestaResult.Exito -> {
                    estado = estado.copy(enviando = false, enviadoOk = true, ultimoIdGenerado = resultado.id)
                }
                is GuardadoEncuestaResult.Error -> {
                    estado = estado.copy(enviando = false, error = resultado.mensaje)
                }
                else -> {
                    // FolioDuplicado / YaRealizadaReciente son de la cola
                    // offline de tienda; enviarEncuestaOficina no los usa.
                    estado = estado.copy(enviando = false, error = "No se pudo enviar la encuesta.")
                }
            }
        }
    }

    fun reiniciarParaNuevaEncuesta() {
        estado = estado.copy(
            areaId = null,
            areaSeleccionada = null,
            calificaciones = emptyMap(),
            comentario = "",
            enviadoOk = false,
        )
    }
}
