package mx.com.getic.encuestasoxxo.data.remote

import mx.com.getic.encuestasoxxo.data.remote.dto.*
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("negocios")
    suspend fun negocios(@Header("Authorization") token: String): List<NegocioDto>

    @GET("regiones")
    suspend fun regiones(@Header("Authorization") token: String, @Query("negocio_id") negocioId: Int): List<RegionDto>

    @GET("plazas")
    suspend fun plazas(@Header("Authorization") token: String, @Query("region_id") regionId: Int): List<PlazaDto>

    @GET("tiendas")
    suspend fun tiendas(@Header("Authorization") token: String, @Query("plaza_id") plazaId: Int): List<TiendaDto>

    @GET("cuestionario")
    suspend fun obtenerCuestionario(
        @Header("Authorization") token: String,
        @Query("plaza_id") plazaId: Int
    ): CuestionarioResponse

    @POST("encuestas")
    suspend fun subirEncuestas(
        @Header("Authorization") token: String,
        @Body body: SubirEncuestasRequest
    ): SubirEncuestasResponse

    @GET("respuestas")
    suspend fun respuestas(@Header("Authorization") token: String): List<RespuestaFilaDto>
}
