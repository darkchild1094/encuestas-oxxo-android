package com.kernel94.pulsoti.ui.areas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel94.pulsoti.data.remote.dto.AdministracionDto
import com.kernel94.pulsoti.data.repository.EncuestaRepository
import kotlinx.coroutines.launch
import timber.log.Timber

data class AreasUiState(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val areas: List<AdministracionDto> = emptyList(),
    val error: String? = null,
    val operacionExitosa: Boolean = false,
)

// Catalogo de areas administrativas (encuesta de oficina). El ATI puede
// consultarlas y darlas de alta desde aqui; editar/desactivar/eliminar
// sigue siendo solo desde el panel web (modulo Administracion, webmaster).
class AreasViewModel(
    private val repository: EncuestaRepository,
) : ViewModel() {
    var estado by mutableStateOf(AreasUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            try {
                val areas = repository.administraciones()
                estado = estado.copy(cargando = false, areas = areas)
            } catch (e: Exception) {
                Timber.e(e, "Error al cargar areas administrativas")
                estado = estado.copy(
                    cargando = false,
                    error = "No se pudieron cargar las areas. Revisa tu conexion.",
                )
            }
        }
    }

    fun agregarArea(nombre: String) {
        val limpio = nombre.trim()
        if (limpio.isEmpty()) {
            estado = estado.copy(error = "Escribe un nombre para el area.")
            return
        }
        estado = estado.copy(guardando = true, error = null)
        viewModelScope.launch {
            try {
                repository.crearAdministracion(limpio)
                estado = estado.copy(guardando = false, operacionExitosa = true)
                cargar()
            } catch (e: retrofit2.HttpException) {
                Timber.e(e, "Error al crear area (HTTP ${e.code()})")
                val mensaje = if (e.code() == 409) {
                    "Ya existe un area con ese nombre."
                } else {
                    "No se pudo crear el area."
                }
                estado = estado.copy(guardando = false, error = mensaje)
            } catch (e: Exception) {
                Timber.e(e, "Error al crear area")
                estado = estado.copy(guardando = false, error = "No se pudo crear el area. Revisa tu conexion.")
            }
        }
    }

    fun resetOperacionExitosa() {
        estado = estado.copy(operacionExitosa = false)
    }
}
