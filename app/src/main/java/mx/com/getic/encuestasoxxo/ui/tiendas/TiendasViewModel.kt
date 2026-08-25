package mx.com.getic.encuestasoxxo.ui.tiendas

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.AtiDto
import mx.com.getic.encuestasoxxo.data.remote.dto.PlazaDto
import mx.com.getic.encuestasoxxo.data.remote.dto.TiendaDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository
import timber.log.Timber

class TiendasViewModel(
    private val repo: EncuestaRepository,
    private val sesion: Sesion
) : ViewModel() {

    var tiendas by mutableStateOf<List<TiendaDto>>(emptyList())
    var plazas by mutableStateOf<List<PlazaDto>>(emptyList())
    var plazaSeleccionadaId by mutableStateOf<Int?>(sesion.plazaId)
    
    var cargando by mutableStateOf(false)
    var guardando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var query by mutableStateOf("")

    var atisPlaza by mutableStateOf<List<AtiDto>>(emptyList())

    init {
        if (sesion.rol == "WEBMASTER") {
            cargarPlazas()
        } else if (sesion.plazaId != null) {
            cargarTiendas(sesion.plazaId)
            cargarAtisPlaza(sesion.plazaId)
        }
    }

    private fun cargarAtisPlaza(plazaId: Int) {
        viewModelScope.launch {
            try {
                atisPlaza = repo.atisDisponibles(plazaId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun cargarPlazas() {
        viewModelScope.launch {
            try {
                val negocios = repo.negocios()
                if (negocios.isNotEmpty()) {
                    val regiones = repo.regiones(negocios.first().id)
                    if (regiones.isNotEmpty()) {
                        plazas = repo.plazas(regiones.first().id)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error cargando plazas para tiendas")
            }
        }
    }

    fun cargarTiendas(plazaId: Int) {
        plazaSeleccionadaId = plazaId
        cargando = true
        error = null
        cargarAtisPlaza(plazaId)
        viewModelScope.launch {
            try {
                tiendas = repo.tiendas(plazaId)
            } catch (e: Exception) {
                error = "Error al cargar tiendas"
                Timber.e(e)
            } finally {
                cargando = false
            }
        }
    }

    fun actualizarTienda(tienda: TiendaDto, onExito: () -> Unit) {
        guardando = true
        error = null
        viewModelScope.launch {
            try {
                val exito = repo.actualizarTienda(tienda)
                if (exito) {
                    query = "" // Limpiamos búsqueda para regresar a la lista completa
                    onExito()
                    if (plazaSeleccionadaId != null) cargarTiendas(plazaSeleccionadaId!!)
                } else {
                    error = "No se pudo actualizar la tienda"
                }
            } catch (e: Exception) {
                error = "Error al guardar"
                Timber.e(e)
            } finally {
                guardando = false
            }
        }
    }

    val tiendasFiltradas: List<TiendaDto>
        get() = if (query.isBlank()) {
            tiendas
        } else {
            tiendas.filter { 
                it.nombre.contains(query, ignoreCase = true) || 
                it.codigo.contains(query, ignoreCase = true) 
            }
        }
}
