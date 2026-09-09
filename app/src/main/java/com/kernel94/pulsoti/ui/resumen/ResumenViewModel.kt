package com.kernel94.pulsoti.ui.resumen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kernel94.pulsoti.data.remote.dto.ResumenSistemaDto
import com.kernel94.pulsoti.data.repository.DashboardRepository
import kotlinx.coroutines.launch

data class ResumenUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val datos: ResumenSistemaDto? = null,
)

// "Resumen del sistema" para WEBMASTER -- contraparte movil de
// ResumenController.php / views/resumen.php del panel web.
class ResumenViewModel(
    private val repository: DashboardRepository,
) : ViewModel() {
    var state by mutableStateOf(ResumenUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            state = state.copy(cargando = true, error = null)
            val datos = repository.obtenerResumenSistema()
            state = state.copy(
                cargando = false,
                datos = datos,
                error = if (datos == null) "No se pudo cargar el resumen. Revisa tu conexión." else null,
            )
        }
    }
}
