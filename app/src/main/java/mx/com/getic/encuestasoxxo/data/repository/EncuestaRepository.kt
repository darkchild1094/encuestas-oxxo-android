package mx.com.getic.encuestasoxxo.data.repository

import kotlinx.coroutines.flow.Flow
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.dao.AtiDao
import mx.com.getic.encuestasoxxo.data.local.dao.TiendaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.AtiEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity
import mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * Resultado de obtenerPreguntas que incluye flag indicando si los datos vienen del cache
 */
data class PreguntasResult(
    val cuestionario: CuestionarioEntity,
    val preguntas: List<PreguntaEntity>,
    val esCacheado: Boolean = false,
)

class EncuestaRepository(
    private val api: ApiService,
    private val cuestionarioDao: CuestionarioDao,
    private val encuestaDao: EncuestaDao,
    private val tiendaDao: TiendaDao,
    private val atiDao: AtiDao,
    private val sessionManager: SessionManager,
) {
    private suspend fun token(): String = "Bearer " + (sessionManager.sesionActualBloqueante()?.token ?: "")

    // --- Catalogo (negocio/region/plaza), siempre en linea -- solo se
    // usa con señal, para armar el arbol antes de llegar a la tienda. --
    suspend fun negocios(): List<NegocioDto> = api.negocios(token())
    suspend fun regiones(negocioId: Int): List<RegionDto> = api.regiones(token(), negocioId)
    suspend fun plazas(regionId: Int): List<PlazaDto> = api.plazas(token(), regionId)

    // --- Tiendas de una plaza: se refrescan de red y se cachean en
    // Room (son pocas, ya vienen acotadas a la plaza del usuario), para
    // que el selector funcione aunque el tecnico este ya sin señal
    // dentro de la tienda. ---
    suspend fun tiendas(plazaId: Int, refrescar: Boolean = false): List<TiendaDto> {
        if (!refrescar) {
            val cache = tiendaDao.obtenerPorPlaza(plazaId)
            if (cache.isNotEmpty()) {
                return cache.map { 
                    TiendaDto(
                        id = it.id,
                        nombre = it.nombre,
                        codigo = it.codigo,
                        plaza_id = it.plazaId,
                        direccion = it.direccion,
                        latitud = it.latitud,
                        longitud = it.longitud,
                        ati_usuario_id = it.atiUsuarioId,
                        ati_nombre = it.atiNombre,
                        ati_foto = it.atiFoto,
                        ati_genero = it.atiGenero,
                    )
                }
            }
        }

        return try {
            Timber.d("Iniciando descarga de tiendas para plaza $plazaId...")
            val tiendas = api.tiendas(token(), plazaId)
            Timber.d("API devolvió ${tiendas.size} tiendas para plaza $plazaId")
            
            tiendaDao.borrarDe(plazaId)
            tiendaDao.guardar(tiendas.map { 
                TiendaEntity(
                    id = it.id,
                    plazaId = plazaId,
                    nombre = it.nombre,
                    codigo = it.codigo,
                    direccion = it.direccion,
                    latitud = it.latitud,
                    longitud = it.longitud,
                    atiUsuarioId = it.ati_usuario_id,
                    atiNombre = it.ati_nombre,
                    atiFoto = it.ati_foto,
                    atiGenero = it.ati_genero,
                )
            })
            Timber.d("Tiendas de plaza $plazaId actualizadas desde servidor: ${tiendas.size}")
            tiendas
        } catch (e: Exception) {
            Timber.w(e, "Error obteniendo tiendas, usando cache local")
            tiendaDao.obtenerPorPlaza(plazaId).map { 
                TiendaDto(
                    id = it.id,
                    nombre = it.nombre,
                    codigo = it.codigo,
                    plaza_id = it.plazaId,
                    direccion = it.direccion,
                    latitud = it.latitud,
                    longitud = it.longitud,
                    ati_usuario_id = it.atiUsuarioId,
                    ati_nombre = it.atiNombre,
                    ati_foto = it.atiFoto,
                    ati_genero = it.atiGenero,
                )
            }
        }
    }

    // --- ATI de la tienda: se refresca de red y queda disponible offline. ---
    suspend fun atisDisponibles(plazaId: Int, refrescar: Boolean = false): List<AtiDto> {
        if (!refrescar) {
            val cache = atiDao.obtenerPorPlaza(plazaId)
            if (cache.isNotEmpty()) {
                return cache.map { AtiDto(it.id, it.nombreCompleto, it.fotoPerfil, it.genero) }
            }
        }

        return try {
            val atis = api.atisDisponibles(token(), plazaId)
            atiDao.borrarDe(plazaId)
            atiDao.guardar(atis.map { AtiEntity(plazaId, it.id, it.nombre_completo, it.foto_perfil, it.genero) })
            atis
        } catch (e: Exception) {
            Timber.w(e, "Error obteniendo ATIs, usando cache local")
            atiDao.obtenerPorPlaza(plazaId).map {
                AtiDto(it.id, it.nombreCompleto, it.fotoPerfil, it.genero)
            }
        }
    }

    suspend fun asignarAti(tiendaId: Int, usuarioId: Int): Boolean = try {
        val tienda = tiendaDao.obtener(tiendaId)
        val ati = tienda?.let { tiendaCache ->
            atiDao.obtenerPorPlaza(tiendaCache.plazaId).firstOrNull { it.id == usuarioId }
        }
        // La asignacion local se conserva aunque el servidor no responda.
        if (ati != null) {
            tiendaDao.guardarAsignacionPendiente(tiendaId, usuarioId, ati.nombreCompleto, ati.fotoPerfil, ati.genero)
        }
        api.asignarAti(token(), AsignarAtiRequest(tiendaId, usuarioId))
        tiendaDao.marcarAsignacionSincronizada(tiendaId)
        true
    } catch (e: Exception) {
        Timber.e(e, "Error asignando ATI a tienda $tiendaId")
        false
    }

    suspend fun actualizarTienda(tienda: TiendaDto): Boolean = try {
        api.actualizarTienda(token(), tienda)
        true
    } catch (e: Exception) {
        Timber.e(e, "Error actualizando tienda ${tienda.id}")
        false
    }

    // --- Cuestionario: se refresca de red y se cachea en Room para
    // poder re-contestar en la misma tienda aunque se caiga la señal
    // a medio checklist. ---
    suspend fun obtenerPreguntas(plazaId: Int, refrescar: Boolean = false): PreguntasResult? {
        if (!refrescar) {
            val cache = cuestionarioDao.obtenerPorPlaza(plazaId)
            if (cache != null) {
                val preguntas = cuestionarioDao.obtenerPreguntas(cache.id)
                if (preguntas.isNotEmpty()) {
                    return PreguntasResult(cache, preguntas, esCacheado = true)
                }
            }
        }

        try {
            Timber.d("Obteniendo preguntas de plaza $plazaId desde servidor")
            val respuesta = api.obtenerCuestionario(token(), plazaId)
            val cuestionarioDto = respuesta.cuestionario ?: return null

            val cuestionario = CuestionarioEntity(cuestionarioDto.id, plazaId, cuestionarioDto.nombre)
            cuestionarioDao.guardar(cuestionario)
            cuestionarioDao.borrarPreguntasDe(cuestionario.id)
            val preguntas = respuesta.preguntas.map {
                PreguntaEntity(it.id, cuestionario.id, it.texto, it.orden)
            }
            cuestionarioDao.guardarPreguntas(preguntas)
            Timber.d("Preguntas cargadas exitosamente: ${preguntas.size} items")
            return PreguntasResult(cuestionario, preguntas, esCacheado = false)
        } catch (e: Exception) {
            // Sin señal: usa lo que ya este cacheado localmente de una
            // visita anterior a esta plaza, si existe.
            Timber.w(e, "Error obteniendo preguntas, intentando desde cache")
            val cache = cuestionarioDao.obtenerPorPlaza(plazaId) ?: return null
            return PreguntasResult(
                cache,
                cuestionarioDao.obtenerPreguntas(cache.id),
                esCacheado = true  // Indicar que son datos cacheados
            )
        }
    }

    // --- Guardar y sincronizar ---
    // Nace con uuid en el dispositivo: no depende del servidor para
    // tener identidad, por eso funciona sin señal.
    suspend fun guardarYIntentarSincronizar(
        usuarioId: Int,
        tiendaId: Int,
        cuestionarioId: Int,
        folio: String,
        comentario: String?,
        calificaciones: Map<Int, Int>, // preguntaId -> 1..10
    ) {
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val encuestaId = UUID.randomUUID().toString()

        val encuesta = EncuestaEntity(
            id = encuestaId,
            usuarioId = usuarioId,
            tiendaId = tiendaId,
            cuestionarioId = cuestionarioId,
            folio = folio,
            comentario = comentario?.ifBlank { null },
            fechaCreacionLocal = formato.format(Date()),
            sincronizado = false,
        )
        encuestaDao.guardarEncuesta(encuesta)

        val respuestas = calificaciones.map { (preguntaId, calificacion) ->
            RespuestaDetalleEntity(
                id = UUID.randomUUID().toString(),
                encuestaId = encuestaId,
                preguntaId = preguntaId,
                calificacion = calificacion,
            )
        }
        encuestaDao.guardarRespuestas(respuestas)

        Timber.d("Encuesta guardada: $encuestaId con ${respuestas.size} respuestas")

        // Intento inmediato -- si hay señal, ya se sube sin esperar al
        // WorkManager. Si falla (sin señal), no pasa nada: se queda
        // marcada sincronizado=false y el Worker la reintenta despues.
        intentarSincronizarPendientes()
    }

    suspend fun intentarSincronizarPendientes(): Boolean {
        try {
            tiendaDao.asignacionesPendientes().forEach { tienda ->
                val atiId = tienda.atiPendienteUsuarioId ?: return@forEach
                api.asignarAti(token(), AsignarAtiRequest(tienda.id, atiId))
                tiendaDao.marcarAsignacionSincronizada(tienda.id)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error sincronizando asignaciones de ATI")
            return false
        }

        val pendientes = encuestaDao.pendientesDeSincronizar()
        if (pendientes.isEmpty()) return true

        return try {
            Timber.d("Sincronizando ${pendientes.size} encuestas pendientes")
            val dto = pendientes.map { e ->
                EncuestaSyncDto(
                    id = e.id,
                    folio = e.folio,
                    tienda_id = e.tiendaId,
                    cuestionario_id = e.cuestionarioId,
                    comentario = e.comentario,
                    fecha_creacion_local = e.fechaCreacionLocal,
                    respuestas = encuestaDao.respuestasDe(e.id).map {
                        RespuestaSyncDto(it.id, it.preguntaId, it.calificacion)
                    },
                )
            }
            val respuesta = api.subirEncuestas(token(), SubirEncuestasRequest(dto))
            respuesta.sincronizadas.forEach { encuestaDao.marcarSincronizada(it) }
            Timber.d("Sincronización exitosa: ${respuesta.sincronizadas.size} encuestas")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error sincronizando encuestas")
            false
        }
    }

    // Observar cantidad de encuestas pendientes de sincronizar
    fun contarPendientes(): Flow<Int> = encuestaDao.contarPendientes()

    // Historial para el ATI -- ya viene filtrado por el servidor a
    // solo las tiendas donde el es el asesor TI asignado.
    suspend fun obtenerRespuestas(): List<RespuestaFilaDto> = api.respuestas(token())

    // --- Gestion de Preguntas (ATI / Webmaster) ---
    suspend fun crearPregunta(cuestionarioId: Int, texto: String, orden: Int) {
        api.crearPregunta(token(), CrearPreguntaRequest(
            cuestionario_id = cuestionarioId,
            texto = texto,
            orden = orden
        ))
    }

    suspend fun editarPregunta(id: Int, texto: String, orden: Int) {
        api.editarPregunta(token(), EditarPreguntaRequest(
            id = id,
            texto = texto,
            orden = orden
        ))
    }

    suspend fun eliminarPregunta(id: Int) {
        api.eliminarPregunta(token(), id)
    }
}
