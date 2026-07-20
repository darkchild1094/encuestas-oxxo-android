package mx.com.getic.encuestasoxxo.ui.preguntas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.remote.dto.NegocioDto
import mx.com.getic.encuestasoxxo.data.remote.dto.PlazaDto
import mx.com.getic.encuestasoxxo.data.remote.dto.RegionDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository
import timber.log.Timber

data class PreguntasUiState(
    val cargandoCatalogo: Boolean = true,
    val plazaFija: Boolean = false,
    val negocios: List<NegocioDto> = emptyList(),
    val regiones: List<RegionDto> = emptyList(),
    val plazas: List<PlazaDto> = emptyList(),
    val negocioId: Int? = null,
    val regionId: Int? = null,
    val plazaId: Int? = null,
    val cuestionarioId: Int? = null,
    val preguntas: List<PreguntaEntity> = emptyList(),
    val cargandoPreguntas: Boolean = false,
    val error: String? = null,
    val operacionExitosa: Boolean = false
)

class PreguntasViewModel(
    private val repository: EncuestaRepository,
    private val sesion: mx.com.getic.encuestasoxxo.data.Sesion
) : ViewModel() {
    var estado by mutableStateOf(PreguntasUiState())
        private set

    init {
        val plazaAsignada = sesion.plazaId
        if (plazaAsignada != null) {
            estado = estado.copy(plazaFija = true, plazaId = plazaAsignada, cargandoCatalogo = false)
            cargarPreguntas(plazaAsignada)
        } else {
            cargarNegocios()
        }
    }

    private fun cargarNegocios() {
        viewModelScope.launch {
            try {
                val negocios = repository.negocios()
                val negocioDefault = negocios.firstOrNull { it.es_default } ?: negocios.firstOrNull()
                estado = estado.copy(negocios = negocios, negocioId = negocioDefault?.id, cargandoCatalogo = false)
                negocioDefault?.let { cargarRegiones(it.id) }
            } catch (e: Exception) {
                estado = estado.copy(cargandoCatalogo = false, error = "Error al cargar negocios.")
            }
        }
    }

    private fun cargarRegiones(negocioId: Int) {
        viewModelScope.launch {
            try {
                val regiones = repository.regiones(negocioId)
                val regionDefault = regiones.firstOrNull { it.es_default } ?: regiones.firstOrNull()
                estado = estado.copy(regiones = regiones, regionId = regionDefault?.id, plazas = emptyList())
                regionDefault?.let { cargarPlazas(it.id) }
            } catch (e: Exception) {
                estado = estado.copy(error = "Error al cargar regiones.")
            }
        }
    }

    private fun cargarPlazas(regionId: Int) {
        viewModelScope.launch {
            try {
                val plazas = repository.plazas(regionId)
                val plazaDefault = plazas.firstOrNull { it.es_default } ?: plazas.firstOrNull()
                estado = estado.copy(plazas = plazas, plazaId = plazaDefault?.id)
                plazaDefault?.let { cargarPreguntas(it.id) }
            } catch (e: Exception) {
                estado = estado.copy(error = "Error al cargar plazas.")
            }
        }
    }

    fun onNegocioSeleccionado(id: Int) {
        estado = estado.copy(negocioId = id)
        cargarRegiones(id)
    }

    fun onRegionSeleccionada(id: Int) {
        estado = estado.copy(regionId = id)
        cargarPlazas(id)
    }

    fun onPlazaSeleccionada(id: Int) {
        estado = estado.copy(plazaId = id)
        cargarPreguntas(id)
    }

    fun cargarPreguntas(plazaId: Int) {
        estado = estado.copy(cargandoPreguntas = true, error = null)
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerPreguntas(plazaId)
                if (resultado != null) {
                    estado = estado.copy(
                        cuestionarioId = resultado.cuestionario.id,
                        preguntas = resultado.preguntas,
                        cargandoPreguntas = false
                    )
                } else {
                    estado = estado.copy(cargandoPreguntas = false, error = "No hay cuestionario para esta plaza.")
                }
            } catch (e: Exception) {
                estado = estado.copy(cargandoPreguntas = false, error = "Error al cargar preguntas.")
            }
        }
    }

    fun agregarPregunta(texto: String, orden: Int) {
        val cuestionarioId = estado.cuestionarioId ?: return
        val plazaId = estado.plazaId ?: return
        viewModelScope.launch {
            try {
                repository.crearPregunta(cuestionarioId, texto, orden)
                estado = estado.copy(operacionExitosa = true)
                cargarPreguntas(plazaId)
            } catch (e: Exception) {
                Timber.e(e, "Error al agregar pregunta")
                estado = estado.copy(error = "Error al agregar pregunta.")
            }
        }
    }

    fun editarPregunta(id: Int, texto: String, orden: Int) {
        val plazaId = estado.plazaId ?: return
        viewModelScope.launch {
            try {
                repository.editarPregunta(id, texto, orden)
                estado = estado.copy(operacionExitosa = true)
                cargarPreguntas(plazaId)
            } catch (e: Exception) {
                Timber.e(e, "Error al editar pregunta")
                estado = estado.copy(error = "Error al editar pregunta.")
            }
        }
    }

    fun eliminarPregunta(id: Int) {
        val plazaId = estado.plazaId ?: return
        viewModelScope.launch {
            try {
                repository.eliminarPregunta(id)
                estado = estado.copy(operacionExitosa = true)
                cargarPreguntas(plazaId)
            } catch (e: Exception) {
                Timber.e(e, "Error al eliminar pregunta")
                estado = estado.copy(error = "Error al eliminar pregunta.")
            }
        }
    }
    
    fun resetOperacionExitosa() {
        estado = estado.copy(operacionExitosa = false)
    }
}
