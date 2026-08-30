package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SoporteRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    private suspend fun token(): String = "Bearer " + (sessionManager.sesionActualBloqueante()?.token ?: "")

    suspend fun misTickets(): List<TicketSoporteDto> = try {
        api.misTickets(token())
    } catch (e: Exception) { emptyList() }

    suspend fun adminTickets(): List<TicketSoporteDto> = try {
        api.adminTickets(token())
    } catch (e: Exception) { emptyList() }

    suspend fun detalleTicket(id: Int): DetalleTicketResponse? = try {
        api.detalleTicket(token(), id)
    } catch (e: Exception) { null }

    suspend fun crearTicket(asunto: String, descripcion: String, file: File? = null): Boolean = try {
        val evidencePart = file?.let {
            val reqFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("evidencia", it.name, reqFile)
        }
        api.crearTicket(
            token(),
            asunto.toRequestBody("text/plain".toMediaTypeOrNull()),
            descripcion.toRequestBody("text/plain".toMediaTypeOrNull()),
            evidencePart
        ).success
    } catch (e: Exception) { false }

    suspend fun comentarTicket(ticketId: Int, mensaje: String, file: File? = null): Boolean = try {
        val evidencePart = file?.let {
            val reqFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("evidencia", it.name, reqFile)
        }
        api.comentarTicket(
            token(),
            ticketId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            mensaje.toRequestBody("text/plain".toMediaTypeOrNull()),
            evidencePart
        ).success
    } catch (e: Exception) { false }

    suspend fun resolverTicket(ticketId: Int, notas: String): Boolean = try {
        api.resolverTicket(token(), ticketId, notas).success
    } catch (e: Exception) { false }
}
