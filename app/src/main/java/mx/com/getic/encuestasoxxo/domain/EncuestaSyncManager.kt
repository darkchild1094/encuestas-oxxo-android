package mx.com.getic.encuestasoxxo.domain

import android.util.Log
import mx.com.getic.encuestasoxxo.data.local.AppDatabase
import mx.com.getic.encuestasoxxo.data.local.entities.*
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class EncuestaSyncManager(
    private val db: AppDatabase,
    private val apiService: ApiService
) {
    companion object {
        private const val TAG = "EncuestaSyncManager"
        private const val MAX_REINTENTOS = 5
        private const val DELAY_INICIAL_MS = 2000L
    }

    suspend fun iniciarHandshake(
        token: String,
        encuestaId: String,
        detalles: List<Map<String, Any>>? = null
    ): Result<String> {
        return try {
            val body = mapOf(
                "encuesta_id" to encuestaId,
                "detalles" to (detalles ?: emptyList<Any>())
            )
            val response = apiService.iniciarHandshake("Bearer $token", body)
            
            Log.d(TAG, "Handshake iniciado para $encuestaId - ID: ${response.handshake_id}")
            
            val log = EncuestaSyncLogEntity(
                encuesta_id = encuestaId,
                estado = "pendiente",
                handshake_id = response.handshake_id
            )
            db.encuestaSyncLogDao().insertar(log)
            
            Result.success(response.handshake_id)
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar handshake: ${e.message}")
            registrarErrorLocal(encuestaId, 0, "Error iniciando handshake: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun enviarEncuestaConReintentos(
        token: String,
        encuestaId: String,
        handshakeId: String,
        enviarFuncion: suspend (String) -> Boolean
    ): Result<Boolean> {
        var intentoActual = 1
        
        while (intentoActual <= MAX_REINTENTOS) {
            try {
                Log.d(TAG, "Intento $intentoActual de $MAX_REINTENTOS para $encuestaId")
                
                actualizarEstado(encuestaId, "enviando", intentoActual)
                
                val exito = enviarFuncion(token)
                
                if (exito) {
                    Log.d(TAG, "Envío exitoso en intento $intentoActual")
                    actualizarEstado(encuestaId, "exito", intentoActual)
                    return Result.success(true)
                }
                
            } catch (e: HttpException) {
                registrarErrorDetallado(
                    encuestaId,
                    e.code(),
                    "HTTP ${e.code()}: ${e.message()}",
                    intentoActual
                )
            } catch (e: IOException) {
                registrarErrorDetallado(
                    encuestaId,
                    null,
                    "Error de conexión: ${e.message}",
                    intentoActual
                )
            } catch (e: Exception) {
                registrarErrorDetallado(
                    encuestaId,
                    null,
                    "Error: ${e.message}",
                    intentoActual
                )
            }
            
            if (intentoActual < MAX_REINTENTOS) {
                val delayMs = DELAY_INICIAL_MS * (1 shl (intentoActual - 1))
                Log.d(TAG, "Reintentando en ${delayMs}ms...")
                delay(delayMs)
                intentoActual++
            } else {
                break
            }
        }
        
        Log.e(TAG, "Máximo de reintentos alcanzado para $encuestaId")
        actualizarEstado(encuestaId, "error", MAX_REINTENTOS)
        return Result.failure(Exception("Máximo de reintentos alcanzado"))
    }

    suspend fun confirmarHandshake(
        token: String,
        handshakeId: String
    ): Result<Boolean> {
        return try {
            val body = mapOf("handshake_id" to handshakeId)
            val response = apiService.confirmarHandshake("Bearer $token", body)
            
            if (response.confirmado) {
                Log.d(TAG, "Handshake confirmado para $handshakeId")
                Result.success(true)
            } else {
                Result.failure(Exception("Confirmación no exitosa"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error confirmando handshake: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun obtenerStatus(
        token: String,
        encuestaId: String
    ): Result<SyncStatusResponse> {
        return try {
            val status = apiService.obtenerStatus("Bearer $token", encuestaId)
            Result.success(status)
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo status: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun registrarErrorDetallado(
        encuestaId: String,
        codigoRespuesta: Int?,
        mensajeError: String,
        intentoNumero: Int
    ) {
        Log.w(TAG, "Error en intento $intentoNumero para $encuestaId: $mensajeError")
        
        actualizarEstado(
            encuestaId,
            "error",
            intentoNumero,
            codigoRespuesta,
            mensajeError
        )
    }

    private suspend fun registrarErrorLocal(
        encuestaId: String,
        codigoRespuesta: Int?,
        mensajeError: String
    ) {
        val log = EncuestaSyncLogEntity(
            encuesta_id = encuestaId,
            estado = "error",
            codigo_respuesta = codigoRespuesta,
            mensaje_error = mensajeError
        )
        db.encuestaSyncLogDao().insertar(log)
    }

    private suspend fun actualizarEstado(
        encuestaId: String,
        nuevoEstado: String,
        intentoNumero: Int,
        codigoRespuesta: Int? = null,
        mensajeError: String? = null
    ) {
        val logActual = db.encuestaSyncLogDao().obtenerUltimoPorEncuesta(encuestaId)
        
        if (logActual != null) {
            val logActualizado = logActual.copy(
                estado = nuevoEstado,
                intento_numero = intentoNumero,
                codigo_respuesta = codigoRespuesta,
                mensaje_error = mensajeError
            )
            db.encuestaSyncLogDao().actualizar(logActualizado)
        } else {
            val nuevoLog = EncuestaSyncLogEntity(
                encuesta_id = encuestaId,
                estado = nuevoEstado,
                intento_numero = intentoNumero,
                codigo_respuesta = codigoRespuesta,
                mensaje_error = mensajeError
            )
            db.encuestaSyncLogDao().insertar(nuevoLog)
        }
    }

    suspend fun reintentar(
        token: String,
        encuestaId: String,
        enviarFuncion: suspend (String) -> Boolean
    ): Result<Boolean> {
        val logActual = db.encuestaSyncLogDao().obtenerUltimoPorEncuesta(encuestaId)
        val nuevoIntento = (logActual?.intento_numero ?: 0) + 1
        
        if (nuevoIntento > MAX_REINTENTOS) {
            return Result.failure(Exception("Máximo de reintentos excedido"))
        }
        
        val handshakeId = logActual?.handshake_id ?: UUID.randomUUID().toString()
        
        return enviarEncuestaConReintentos(
            token,
            encuestaId,
            handshakeId,
            enviarFuncion
        )
    }
}
