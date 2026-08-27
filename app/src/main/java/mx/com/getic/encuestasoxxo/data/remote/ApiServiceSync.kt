package mx.com.getic.encuestasoxxo.data.remote

import mx.com.getic.encuestasoxxo.data.local.entities.*
import retrofit2.http.*

interface ApiServiceSync {
    
    @POST("encuestas/sync/init-handshake")
    suspend fun iniciarHandshake(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): SyncInitResponse
    
    @POST("encuestas/sync/confirm-handshake")
    suspend fun confirmarHandshake(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): SyncConfirmResponse
    
    @POST("encuestas/sync/registrar-error")
    suspend fun registrarError(
        @Header("Authorization") token: String,
        @Body body: ErrorRegistroRequest
    ): Map<String, Any>
    
    @GET("encuestas/sync/status")
    suspend fun obtenerStatus(
        @Header("Authorization") token: String,
        @Query("encuesta_id") encuestaId: String
    ): SyncStatusResponse
    
    @GET("encuestas/pfs/pendientes")
    suspend fun obtenerEncuestasPendientesPFS(
        @Header("Authorization") token: String
    ): PFSPendientesResponse

    @GET("check-update")
    suspend fun verificarActualizacion(): UpdateResponse
}

data class UpdateResponse(
    val version_code: Int,
    val version_name: String,
    val url: String,
    val obligatoria: Boolean,
    val novedades: String? = null
)

data class PFSPendientesResponse(
    val total_encuestas: Int,
    val encuestas: List<EncuestaPFSDto>
)

data class EncuestaPFSDto(
    val id: String,
    val tienda_id: Int,
    val tienda_nombre: String,
    val folio: String?,
    val fecha_creacion_local: String,
    val comentario: String?,
    val sincronizado: Boolean,
    val estado: String?,
    val intento_numero: Int?,
    val mensaje_error: String?,
    val fecha_intento: String?,
    val fecha_confirmacion: String?,
    val total_respuestas: Int
)
