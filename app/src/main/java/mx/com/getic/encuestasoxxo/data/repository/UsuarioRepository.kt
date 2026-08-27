package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.local.dao.UsuarioDao
import mx.com.getic.encuestasoxxo.data.local.entities.UsuarioEntity
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class UsuarioRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager,
    private val usuarioDao: UsuarioDao? = null
) {
    private suspend fun getToken() = "Bearer ${sessionManager.sesionActualBloqueante()?.token}"

    suspend fun obtenerUsuarios(refrescar: Boolean = false): List<UsuarioDto> {
        if (!refrescar && usuarioDao != null) {
            val cache = usuarioDao.obtenerUsuarios()
            if (cache.isNotEmpty()) {
                return cache.map { u ->
                    UsuarioDto(
                        id = u.id,
                        correo = u.correo,
                        nombre_completo = u.nombreCompleto,
                        foto_perfil = u.fotoPerfil,
                        genero = u.genero,
                        plaza_id = u.plazaId,
                        plaza_nombre = u.plazaNombre,
                        rol = u.rol,
                        gestiona_preguntas = u.gestionaPreguntas,
                        gestiona_usuarios = u.gestionaUsuarios,
                        es_encuestable = u.esEncuestable,
                        ve_resultados_tiendas = u.veResultadosTiendas
                    )
                }
            }
        }
        
        val remote = api.obtenerUsuarios(getToken())
        
        usuarioDao?.let { dao ->
            dao.borrarUsuarios()
            dao.guardarUsuarios(remote.map { u ->
                UsuarioEntity(
                    id = u.id,
                    correo = u.correo,
                    nombreCompleto = u.nombre_completo,
                    fotoPerfil = u.foto_perfil,
                    genero = u.genero,
                    plazaId = u.plaza_id,
                    plazaNombre = u.plaza_nombre,
                    rol = u.rol,
                    gestionaPreguntas = u.gestiona_preguntas,
                    gestionaUsuarios = u.gestiona_usuarios,
                    esEncuestable = u.es_encuestable,
                    veResultadosTiendas = u.ve_resultados_tiendas
                )
            })
        }
        
        return remote
    }

    suspend fun obtenerRoles(): List<RolDto> {
        return api.obtenerRoles(getToken())
    }

    private fun String.toPart() = this.toRequestBody("text/plain".toMediaTypeOrNull())

    suspend fun crearUsuario(
        correo: String,
        nombre: String,
        rolId: Int,
        plazaId: Int?,
        password: String,
        fotoFile: File?
    ): OperacionUsuarioResponse {
        val fotoPart = fotoFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto_perfil", it.name, requestFile)
        }

        return api.crearUsuario(
            getToken(),
            correo.toPart(),
            nombre.toPart(),
            rolId.toString().toPart(),
            plazaId?.toString()?.toPart(),
            password.toPart(),
            fotoPart
        )
    }

    suspend fun editarUsuario(
        id: Int,
        nombre: String,
        rolId: Int,
        plazaId: Int?,
        password: String?,
        fotoFile: File?
    ): OperacionUsuarioResponse {
        val fotoPart = fotoFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto_perfil", it.name, requestFile)
        }

        return api.editarUsuario(
            getToken(),
            id.toString().toPart(),
            nombre.toPart(),
            rolId.toString().toPart(),
            plazaId?.toString()?.toPart(),
            password?.toPart(),
            fotoPart
        )
    }

    fun uriToFile(contentResolver: ContentResolver, uri: Uri, cacheDir: File): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    suspend fun eliminarUsuario(id: Int): OperacionUsuarioResponse {
        return api.eliminarUsuario(getToken(), id)
    }

    suspend fun cambiarPassword(nueva: String): OperacionUsuarioResponse {
        return api.cambiarPassword(getToken(), mapOf("password" to nueva))
    }

    suspend fun actualizarPerfil(nombre: String, fotoFile: File?): OperacionUsuarioResponse {
        val fotoPart = fotoFile?.let {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto_perfil", it.name, requestFile)
        }

        return api.actualizarPerfil(
            getToken(),
            nombre.toPart(),
            fotoPart
        )
    }
}
