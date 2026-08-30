package mx.com.getic.encuestasoxxo.ui.encuesta

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.remote.dto.AtiDto
import mx.com.getic.encuestasoxxo.data.remote.dto.NegocioDto
import mx.com.getic.encuestasoxxo.data.remote.dto.PlazaDto
import mx.com.getic.encuestasoxxo.data.remote.dto.RegionDto
import mx.com.getic.encuestasoxxo.data.remote.dto.TiendaDto
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository
import mx.com.getic.encuestasoxxo.data.repository.GuardadoEncuestaResult
import mx.com.getic.encuestasoxxo.sync.SincronizacionWorker

data class EncuestaUiState(
    val folio: String = "",
    val cargandoCatalogo: Boolean = true,
    val plazaFija: Boolean = false, // true si el usuario ya tiene plaza asignada -- se salta negocio/region/plaza
    val negocios: List<NegocioDto> = emptyList(),
    val regiones: List<RegionDto> = emptyList(),
    val plazas: List<PlazaDto> = emptyList(),
    val tiendas: List<TiendaDto> = emptyList(),
    val negocioId: Int? = null,
    val regionId: Int? = null,
    val plazaId: Int? = null,
    val tiendaId: Int? = null,
    val tiendaSeleccionada: TiendaDto? = null,
    val atisDisponibles: List<AtiDto> = emptyList(),
    val cargandoAtis: Boolean = false,
    val asignandoAti: Boolean = false,
    val cuestionarioId: Int? = null,
    val preguntas: List<PreguntaEntity> = emptyList(),
    val calificaciones: Map<Int, Int> = emptyMap(), // preguntaId -> 1..10
    val comentario: String = "",
    val cargandoPreguntas: Boolean = false,
    val enviando: Boolean = false,
    val enviadoOk: Boolean = false,
    val ultimoIdGenerado: String = "",
    val datosEnCache: Boolean = false,
    val encuestasPendientes: Int = 0,
    val error: String? = null,
)

class EncuestaViewModel(
    private val repository: EncuestaRepository,
    private val sesion: Sesion,
) : ViewModel() {
    var estado by mutableStateOf(EncuestaUiState())
        private set

    init {
        val plazaAsignada = sesion.plazaId
        val esAdminOAti = sesion.rol == "ATI" || sesion.rol == "WEBMASTER"

        if (plazaAsignada != null || esAdminOAti) {
            // Para ATI y Webmaster (o cualquiera con plaza fija), forzamos su plaza
            // y no cargamos el resto de negocios/regiones.
            estado = estado.copy(
                plazaFija = true, 
                plazaId = plazaAsignada, 
                cargandoCatalogo = false
            )
            plazaAsignada?.let { 
                cargarTiendas(it)
                cargarAtisDisponibles(it)
            }
        } else {
            cargarNegocios()
        }
        
        // Observar encuestas pendientes de sincronización
        viewModelScope.launch {
            repository.contarPendientes().collect { count ->
                estado = estado.copy(encuestasPendientes = count)
            }
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
                estado = estado.copy(cargandoCatalogo = false, error = "No se pudo cargar el catalogo. Revisa tu conexion.")
            }
        }
    }

    private fun cargarRegiones(negocioId: Int) {
        viewModelScope.launch {
            try {
                val regiones = repository.regiones(negocioId)
                val regionDefault = regiones.firstOrNull { it.es_default } ?: regiones.firstOrNull()
                estado = estado.copy(regiones = regiones, regionId = regionDefault?.id, plazas = emptyList(), tiendas = emptyList())
                regionDefault?.let { cargarPlazas(it.id) }
            } catch (e: Exception) {
                estado = estado.copy(error = "No se pudo cargar regiones. Revisa tu conexion.")
            }
        }
    }

    private fun cargarPlazas(regionId: Int) {
        viewModelScope.launch {
            try {
                val plazas = repository.plazas(regionId)
                val plazaDefault = plazas.firstOrNull { it.es_default } ?: plazas.firstOrNull()
                estado = estado.copy(plazas = plazas, plazaId = plazaDefault?.id, tiendas = emptyList())
                plazaDefault?.let { cargarTiendas(it.id) }
            } catch (e: Exception) {
                estado = estado.copy(error = "No se pudo cargar plazas. Revisa tu conexion.")
            }
        }
    }

    private fun cargarTiendas(plazaId: Int) {
        viewModelScope.launch {
            try {
                val tiendas = repository.tiendas(plazaId)
                estado = estado.copy(tiendas = tiendas, tiendaId = null, preguntas = emptyList(), cuestionarioId = null)
            } catch (e: Exception) {
                estado = estado.copy(error = "No se pudo cargar tiendas. Revisa tu conexion.")
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
        cargarTiendas(id)
        cargarAtisDisponibles(id)
    }

    fun onTiendaSeleccionada(id: Int) {
        if (id == -1) {
            estado = estado.copy(
                tiendaId = null,
                tiendaSeleccionada = null,
                atisDisponibles = emptyList(),
                preguntas = emptyList(),
                cuestionarioId = null,
                calificaciones = emptyMap(),
            )
            return
        }
        val tienda = estado.tiendas.firstOrNull { it.id == id }
        estado = estado.copy(tiendaId = id, tiendaSeleccionada = tienda)
        cargarPreguntas()

        // Si la tienda no trae ATI asignado (comun fuera de Valles),
        // cargamos el catalogo de ATIs de su plaza para el selector.
        if (tienda != null && tienda.ati_usuario_id == null) {
            cargarAtisDisponibles(tienda.plaza_id)
        }
    }

    private fun cargarAtisDisponibles(plazaId: Int) {
        viewModelScope.launch {
            // Si ya tenemos los ATIs de esta plaza en el estado, no re-cargamos para evitar parpadeos
            if (estado.atisDisponibles.isNotEmpty() && estado.plazaId == plazaId) return@launch

            estado = estado.copy(cargandoAtis = true)
            val atis = repository.atisDisponibles(plazaId)
            estado = estado.copy(atisDisponibles = atis, cargandoAtis = false)
        }
    }

    // El tecnico elige manualmente el ATI de la tienda (cuando no
    // tenia uno asignado). Se guarda PERMANENTE en la tienda: la
    // proxima encuesta ahi ya no vuelve a preguntar.
    fun onAtiSeleccionado(usuarioId: Int) {
        val ati = estado.atisDisponibles.firstOrNull { it.id == usuarioId } ?: return
        val tiendaActual = estado.tiendaSeleccionada ?: return

        estado = estado.copy(asignandoAti = true)
        viewModelScope.launch {
            val exito = repository.asignarAti(tiendaActual.id, usuarioId)
            val tiendaActualizada = tiendaActual.copy(
                ati_usuario_id = ati.id,
                ati_nombre = ati.nombre_completo,
                ati_foto = ati.foto_perfil,
            )
            estado = estado.copy(
                asignandoAti = false,
                tiendaSeleccionada = tiendaActualizada,
                atisDisponibles = emptyList(),
                // Aunque falle guardarlo en el servidor (sin señal), la
                // encuesta sigue -- solo no quedara memorizado para la
                // proxima vez y se le volvera a preguntar.
                error = if (!exito) "No se pudo guardar el ATI en el servidor, pero puedes continuar." else null,
            )
        }
    }

    private fun cargarPreguntas() {
        val plazaId = estado.plazaId ?: return
        estado = estado.copy(cargandoPreguntas = true, calificaciones = emptyMap(), comentario = "")
        viewModelScope.launch {
            val resultado = repository.obtenerPreguntas(plazaId)
            if (resultado == null) {
                estado = estado.copy(cargandoPreguntas = false, error = "No se pudieron cargar las preguntas de esta plaza.")
                return@launch
            }
            val (cuestionario, preguntas, esCacheado) = resultado
            estado = estado.copy(
                cuestionarioId = cuestionario.id,
                preguntas = preguntas,
                cargandoPreguntas = false,
                datosEnCache = esCacheado,
            )
        }
    }

    fun onCalificar(preguntaId: Int, calificacion: Int) {
        estado = estado.copy(calificaciones = estado.calificaciones + (preguntaId to calificacion))
    }

    fun onComentarioChange(valor: String) {
        estado = estado.copy(comentario = valor)
    }

    fun onFolioChange(valor: String) {
        estado = estado.copy(folio = valor.uppercase().take(11))
    }

    val faltanPorCalificar: Int get() = estado.preguntas.count { estado.calificaciones[it.id] == null }

    fun enviar(context: Context, onListo: () -> Unit) {
        val tiendaId = estado.tiendaId
        val cuestionarioId = estado.cuestionarioId
        if (tiendaId == null || cuestionarioId == null) return
        if (estado.folio.isBlank()) {
            estado = estado.copy(error = "Escribe el número de folio antes de continuar.")
            return
        }
        if (faltanPorCalificar > 0) {
            estado = estado.copy(error = "Falta calificar ${faltanPorCalificar} pregunta(s).")
            return
        }

        estado = estado.copy(enviando = true, error = null)
        viewModelScope.launch {
            val result = repository.guardarYIntentarSincronizar(
                usuarioId = sesion.usuarioId,
                tiendaId = tiendaId,
                cuestionarioId = cuestionarioId,
                folio = estado.folio.trim(),
                comentario = estado.comentario,
                calificaciones = estado.calificaciones,
            )

            when (result) {
                is GuardadoEncuestaResult.Exito -> {
                    SincronizacionWorker.agendar(context) // por si el intento inmediato no tuvo señal
                    estado = estado.copy(enviando = false, enviadoOk = true, ultimoIdGenerado = result.id)
                    onListo()
                }
                GuardadoEncuestaResult.FolioDuplicado -> {
                    estado = estado.copy(enviando = false, error = "Este número de folio ya fue registrado para esta tienda hoy.")
                }
                GuardadoEncuestaResult.YaRealizadaReciente -> {
                    estado = estado.copy(enviando = false, error = "Ya se realizó una encuesta en esta tienda hace menos de 30 minutos.")
                }
                is GuardadoEncuestaResult.Error -> {
                    estado = estado.copy(enviando = false, error = result.mensaje)
                }
            }
        }
    }

    fun reintentarSincronizacion() {
        estado = estado.copy(enviando = true, error = null)
        viewModelScope.launch {
            val exito = repository.intentarSincronizarPendientes()
            estado = estado.copy(
                enviando = false,
                error = if (!exito) "No se pudo sincronizar. Reintentando en background..." else null
            )
        }
    }

    fun reiniciarParaNuevaEncuesta() {
        estado = estado.copy(
            folio = "",
            tiendaId = null,
            tiendaSeleccionada = null,
            atisDisponibles = emptyList(),
            cuestionarioId = null,
            preguntas = emptyList(),
            calificaciones = emptyMap(),
            comentario = "",
            enviadoOk = false,
        )
    }
}
