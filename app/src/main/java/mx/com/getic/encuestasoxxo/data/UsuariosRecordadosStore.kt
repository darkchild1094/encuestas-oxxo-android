package mx.com.getic.encuestasoxxo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.usuariosRecordadosDataStore by preferencesDataStore(name = "usuarios_recordados")

data class UsuarioRecordado(val correo: String, val nombre: String)

/**
 * Recuerda, por dispositivo, los correos que ya han iniciado sesion en la
 * app -- para que al cerrar sesion (o al abrir la app de nuevo) el usuario
 * solo tenga que elegir su cuenta de una lista y teclear la contraseña, en
 * vez de escribir el correo cada vez.
 *
 * Vive en un DataStore SEPARADO del de la sesion activa (SessionManager).
 * Esto es a proposito: SessionManager.cerrarSesion() limpia todo su
 * DataStore al cerrar sesion, pero este historial de cuentas debe
 * sobrevivir a ese cierre.
 */
class UsuariosRecordadosStore(private val context: Context) {

    private object Claves {
        val LISTA = stringPreferencesKey("lista_usuarios")
    }

    private val separadorUsuario = "\n"
    private val separadorCampo = "\u0001"
    private val maxRecordados = 6

    suspend fun recordar(correo: String, nombre: String) {
        if (correo.isBlank()) return
        context.usuariosRecordadosDataStore.edit { prefs ->
            val actuales = parsear(prefs[Claves.LISTA])
            val filtrados = actuales.filterNot { it.correo.equals(correo, ignoreCase = true) }
            val nuevaLista = listOf(UsuarioRecordado(correo, nombre)) + filtrados
            prefs[Claves.LISTA] = serializar(nuevaLista.take(maxRecordados))
        }
    }

    suspend fun olvidar(correo: String) {
        context.usuariosRecordadosDataStore.edit { prefs ->
            val actuales = parsear(prefs[Claves.LISTA])
            prefs[Claves.LISTA] = serializar(actuales.filterNot { it.correo.equals(correo, ignoreCase = true) })
        }
    }

    val usuariosRecordados: Flow<List<UsuarioRecordado>> =
        context.usuariosRecordadosDataStore.data.map { prefs -> parsear(prefs[Claves.LISTA]) }

    private fun parsear(raw: String?): List<UsuarioRecordado> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(separadorUsuario).mapNotNull { linea ->
            val partes = linea.split(separadorCampo)
            val correo = partes.getOrNull(0)
            if (correo.isNullOrBlank()) null
            else UsuarioRecordado(correo = correo, nombre = partes.getOrElse(1) { "" })
        }
    }

    private fun serializar(lista: List<UsuarioRecordado>): String =
        lista.joinToString(separadorUsuario) { "${it.correo}$separadorCampo${it.nombre}" }
}
