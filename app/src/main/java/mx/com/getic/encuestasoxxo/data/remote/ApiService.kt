package mx.com.getic.encuestasoxxo.data.remote

import mx.com.getic.encuestasoxxo.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    // --- Gestión de Usuarios (Admin) ---
    @GET("usuarios")
    suspend fun obtenerUsuarios(@Header("Authorization") token: String): List<UsuarioDto>

    @GET("roles")
    suspend fun obtenerRoles(@Header("Authorization") token: String): List<RolDto>

    @Multipart
    @POST("usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") token: String,
        @Part("correo") correo: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part("rol_id") rolId: RequestBody,
        @Part("plaza_id") plazaId: RequestBody?,
        @Part("password") password: RequestBody,
        @Part foto: MultipartBody.Part?
    ): OperacionUsuarioResponse

    @Multipart
    @POST("usuarios/edit")
    suspend fun editarUsuario(
        @Header("Authorization") token: String,
        @Part("id") id: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part("rol_id") rolId: RequestBody,
        @Part("plaza_id") plazaId: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part foto: MultipartBody.Part?
    ): OperacionUsuarioResponse

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): OperacionUsuarioResponse

    // --- Perfil y Password ---
    @POST("perfil/password")
    suspend fun cambiarPassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): OperacionUsuarioResponse

    @Multipart
    @POST("perfil/update")
    suspend fun actualizarPerfil(
        @Header("Authorization") token: String,
        @Part("nombre") nombre: RequestBody,
        @Part foto: MultipartBody.Part?
    ): OperacionUsuarioResponse

    // --- Gestión de Preguntas (Admin/ATI) ---
    @POST("preguntas")
    suspend fun crearPregunta(
        @Header("Authorization") token: String,
        @Body body: CrearPreguntaRequest
    )

    @POST("preguntas/edit")
    suspend fun editarPregunta(
        @Header("Authorization") token: String,
        @Body body: EditarPreguntaRequest
    )

    @DELETE("preguntas/{id}")
    suspend fun eliminarPregunta(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
}
