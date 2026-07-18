package mx.com.getic.encuestasoxxo.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import mx.com.getic.encuestasoxxo.data.remote.dto.UsuarioDto

private val Context.dataStore by preferencesDataStore(name = "sesion")

// Guarda el token + los datos del usuario logueado para que la app
// no pida login cada vez que se abre. Los flags de permisos
// (gestiona_preguntas, es_encuestable, etc.) se guardan tal cual
// vinieron del login -- son la fuente de verdad para decidir que
// pantalla mostrar primero y que opciones habilitar.
class SessionManager(private val context: Context) {

    private object Claves {
        val TOKEN = stringPreferencesKey("token")
        val USUARIO_ID = intPreferencesKey("usuario_id")
        val CORREO = stringPreferencesKey("correo")
        val NOMBRE = stringPreferencesKey("nombre_completo")
        val FOTO = stringPreferencesKey("foto_perfil")
        val ROL = stringPreferencesKey("rol")
        val GESTIONA_PREGUNTAS = booleanPreferencesKey("gestiona_preguntas")
        val GESTIONA_USUARIOS = booleanPreferencesKey("gestiona_usuarios")
        val ES_ENCUESTABLE = booleanPreferencesKey("es_encuestable")
        val VE_RESULTADOS = booleanPreferencesKey("ve_resultados_tiendas")
        val PLAZA_ID = intPreferencesKey("plaza_id")
        val PLAZA_NOMBRE = stringPreferencesKey("plaza_nombre")
    }

    suspend fun guardarSesion(token: String, usuario: UsuarioDto) {
        context.dataStore.edit { prefs ->
            prefs[Claves.TOKEN] = token
            prefs[Claves.USUARIO_ID] = usuario.id
            prefs[Claves.CORREO] = usuario.correo
            prefs[Claves.NOMBRE] = usuario.nombre_completo ?: ""
            prefs[Claves.FOTO] = usuario.foto_perfil ?: ""
            prefs[Claves.ROL] = usuario.rol
            prefs[Claves.GESTIONA_PREGUNTAS] = usuario.gestiona_preguntas
            prefs[Claves.GESTIONA_USUARIOS] = usuario.gestiona_usuarios
            prefs[Claves.ES_ENCUESTABLE] = usuario.es_encuestable
            prefs[Claves.VE_RESULTADOS] = usuario.ve_resultados_tiendas
            if (usuario.plaza_id != null) {
                prefs[Claves.PLAZA_ID] = usuario.plaza_id
                prefs[Claves.PLAZA_NOMBRE] = usuario.plaza_nombre ?: ""
            } else {
                prefs.remove(Claves.PLAZA_ID)
                prefs.remove(Claves.PLAZA_NOMBRE)
            }
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { it.clear() }
    }

    val sesionActual: Flow<Sesion?> = context.dataStore.data.map { prefs ->
        val token = prefs[Claves.TOKEN] ?: return@map null
        Sesion(
            token = token,
            usuarioId = prefs[Claves.USUARIO_ID] ?: 0,
            correo = prefs[Claves.CORREO] ?: "",
            nombreCompleto = prefs[Claves.NOMBRE] ?: "",
            fotoPerfil = prefs[Claves.FOTO]?.ifBlank { null },
            rol = prefs[Claves.ROL] ?: "",
            gestionaPreguntas = prefs[Claves.GESTIONA_PREGUNTAS] ?: false,
            gestionaUsuarios = prefs[Claves.GESTIONA_USUARIOS] ?: false,
            esEncuestable = prefs[Claves.ES_ENCUESTABLE] ?: false,
            veResultadosTiendas = prefs[Claves.VE_RESULTADOS] ?: false,
            plazaId = prefs[Claves.PLAZA_ID],
            plazaNombre = prefs[Claves.PLAZA_NOMBRE],
        )
    }

    suspend fun sesionActualBloqueante(): Sesion? = sesionActual.first()
}

data class Sesion(
    val token: String,
    val usuarioId: Int,
    val correo: String,
    val nombreCompleto: String,
    val fotoPerfil: String?,
    val rol: String,
    val gestionaPreguntas: Boolean,
    val gestionaUsuarios: Boolean,
    val esEncuestable: Boolean,
    val veResultadosTiendas: Boolean,
    val plazaId: Int? = null,
    val plazaNombre: String? = null,
) {
    // Misma regla que ya usa el panel web para decidir la pantalla
    // de inicio: si puede ser encuestado, arranca en Encuesta; si no
    // (el ATI), arranca en Historial.
    val pantallaInicio: String get() = if (esEncuestable) "encuesta" else "historial"
}
