package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*
import java.text.SimpleDateFormat
import java.util.*

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
    suspend fun obtenerPreguntas(plazaId: Int): Pair<CuestionarioEntity, List<PreguntaEntity>>? {
        try {
            val respuesta = api.obtenerCuestionario(token(), plazaId)
            val cuestionarioDto = respuesta.cuestionario ?: return null

            val cuestionario = CuestionarioEntity(cuestionarioDto.id, plazaId, cuestionarioDto.nombre)
            cuestionarioDao.guardar(cuestionario)
            cuestionarioDao.borrarPreguntasDe(cuestionario.id)
            val preguntas = respuesta.preguntas.map {
                PreguntaEntity(it.id, cuestionario.id, it.texto, it.orden)
            }
            cuestionarioDao.guardarPreguntas(preguntas)
            return cuestionario to preguntas
        } catch (e: Exception) {
            // Sin señal: usa lo que ya este cacheado localmente de una
            // visita anterior a esta plaza, si existe.
            val cache = cuestionarioDao.obtenerPorPlaza(plazaId) ?: return null
            return cache to cuestionarioDao.obtenerPreguntas(cache.id)
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

        // Intento inmediato -- si hay señal, ya se sube sin esperar al
        // WorkManager. Si falla (sin señal), no pasa nada: se queda
        // marcada sincronizado=false y el Worker la reintenta despues.
        intentarSincronizarPendientes()
    }

    suspend fun intentarSincronizarPendientes(): Boolean {
        val pendientes = encuestaDao.pendientesDeSincronizar()
        if (pendientes.isEmpty()) return true

        return try {
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
            true
        } catch (e: Exception) {
            false
        }
    }

    // Historial para el ATI -- ya viene filtrado por el servidor a
    // solo las tiendas donde el es el asesor TI asignado.
    suspend fun obtenerRespuestas(): List<RespuestaFilaDto> = api.respuestas(token())
}
