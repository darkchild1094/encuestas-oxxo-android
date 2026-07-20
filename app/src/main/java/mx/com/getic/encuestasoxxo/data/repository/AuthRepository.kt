package mx.com.getic.encuestasoxxo.data.repository

import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.remote.ApiService
import mx.com.getic.encuestasoxxo.data.remote.dto.LoginRequest

sealed class ResultadoLogin {
    data class Ok(val rol: String, val debeCambiarPassword: Boolean) : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
}

class AuthRepository(
    private val api: ApiService,
    private val sesion: SessionManager,
) {
    suspend fun login(correo: String, password: String): ResultadoLogin {
        return try {
            val respuesta = api.login(LoginRequest(correo, password))
            sesion.guardarSesion(respuesta.token, respuesta.usuario)
            ResultadoLogin.Ok(
                rol = respuesta.usuario.rol,
                debeCambiarPassword = respuesta.usuario.debe_cambiar_password ?: false
            )
        } catch (e: retrofit2.HttpException) {
            val mensaje = if (e.code() == 401) {
                "Correo o password incorrectos."
            } else {
                "No se pudo conectar con el servidor (${e.code()})."
            }
            ResultadoLogin.Error(mensaje)
        } catch (e: java.io.IOException) {
            // Sin conexion -- el login SI necesita internet la primera
            // vez (para sacar el token), a diferencia de contestar
            // encuestas que ya funciona offline despues de logueado.
            ResultadoLogin.Error("Sin conexion. El primer login necesita internet.")
        } catch (e: Exception) {
            // Captura errores de parsing (Gson) u otros inesperados para evitar crash
            ResultadoLogin.Error("Error inesperado: ${e.message}")
        }
    }

    suspend fun logout() = sesion.cerrarSesion()
}
