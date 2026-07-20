package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
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
    private val sessionManager: SessionManager
) {
    private suspend fun getToken() = "Bearer ${sessionManager.sesionActualBloqueante()?.token}"

    suspend fun obtenerUsuarios(): List<UsuarioDto> {
        return api.obtenerUsuarios(getToken())
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
