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
    val areas: List<AdministracionDto> = emptyList(),
    val error: String? = null,
)

// Catalogo de areas administrativas: solo lectura (se dan de alta desde
// el panel web, modulo Administracion -- solo webmaster). Aqui el ATI
// nada mas consulta que areas existen y comparte el enlace publico de
// la encuesta de oficina.
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
}
