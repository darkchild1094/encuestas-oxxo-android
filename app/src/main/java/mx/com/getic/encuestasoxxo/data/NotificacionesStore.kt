package mx.com.getic.encuestasoxxo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.notificacionesDataStore by preferencesDataStore(name = "notificaciones")

class NotificacionesStore(private val context: Context) {
    private val ultimaEncuesta = stringPreferencesKey("ultima_encuesta_notificada")

    suspend fun obtenerUltimaEncuesta(): String? =
        context.notificacionesDataStore.data.first()[ultimaEncuesta]

    suspend fun guardarUltimaEncuesta(fecha: String) {
        context.notificacionesDataStore.edit { it[ultimaEncuesta] = fecha }
    }
}
