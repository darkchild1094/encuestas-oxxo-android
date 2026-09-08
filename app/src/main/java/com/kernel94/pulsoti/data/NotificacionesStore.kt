package com.kernel94.pulsoti.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.notificacionesDataStore by preferencesDataStore(name = "notificaciones")

class NotificacionesStore(private val context: Context) {
    private val ultimaEncuesta = stringPreferencesKey("ultima_encuesta_notificada")
    // Claves de notificaciones ya mostradas, en orden de llegada y separadas
    // por "\n" (se guarda como string ordenado, no como Set, para poder
    // recortar SIEMPRE las mas antiguas y no reventar DataStore).
    private val clavesVistas = stringPreferencesKey("claves_notificadas")

    suspend fun obtenerUltimaEncuesta(): String? =
        context.notificacionesDataStore.data.first()[ultimaEncuesta]

    suspend fun guardarUltimaEncuesta(fecha: String) {
        context.notificacionesDataStore.edit { it[ultimaEncuesta] = fecha }
    }

    suspend fun obtenerClavesVistas(): Set<String> =
        context.notificacionesDataStore.data.first()[clavesVistas]
            ?.split("\n")?.filter { it.isNotBlank() }?.toSet()
            ?: emptySet()

    /** Anexa claves nuevas al historial de ya-notificadas, acotado a [maximo]. */
    suspend fun agregarClavesVistas(nuevas: List<String>, maximo: Int = 200) {
        if (nuevas.isEmpty()) return
        context.notificacionesDataStore.edit { prefs ->
            val actuales = prefs[clavesVistas]
                ?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
            prefs[clavesVistas] = (actuales + nuevas).distinct()
                .takeLast(maximo)
                .joinToString("\n")
        }
    }
}
