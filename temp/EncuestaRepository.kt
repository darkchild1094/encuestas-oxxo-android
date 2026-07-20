package mx.com.getic.encuestasoxxo.data.repository

import kotlinx.coroutines.flow.Flow
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity
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
    private val sessionManager: SessionManager,
) {
    private suspend fun token(): String = "Bearer " + (sessionManager.sesionActualBloqueante()?.token ?: "")

    // --- Catalogo (negocio/region/plaza/tienda), siempre en linea --
    // el selector solo se usa con señal, no forma parte del flujo
    // offline (eso es exclusivo de la parte de contestar y subir).
    suspend fun negocios(): List<NegocioDto> = api.negocios(token())
    suspend fun regiones(negocioId: Int): List<RegionDto> = api.regiones(token(), negocioId)
    suspend fun plazas(regionId: Int): List<PlazaDto> = api.plazas(token(), regionId)
    suspend fun tiendas(plazaId: Int): List<TiendaDto> = api.tiendas(token(), plazaId)

    // --- Cuestionario: se refresca de red y se cachea en Room para
    // poder re-contestar en la misma tienda aunque se caiga la señal
    // a medio checklist. ---
    suspend fun obtenerPreguntas(plazaId: Int): PreguntasResult? {
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
        val pendientes = encuestaDao.pendientesDeSincronizar()
        if (pendientes.isEmpty()) return true

        return try {
            Timber.d("Sincronizando ${pendientes.size} encuestas pendientes")
            val dto = pendientes.map { e ->
                EncuestaSyncDto(
                    id = e.id,
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
}
