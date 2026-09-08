package com.kernel94.pulsoti.sync

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kernel94.pulsoti.EncuestasOxxoApp
import com.kernel94.pulsoti.data.remote.NotificacionDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificacionesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as EncuestasOxxoApp
        val sesion = app.container.sessionManager.sesionActualBloqueante()
            ?: return Result.success()

        val store = app.container.notificacionesStore
        val cursorGuardado = store.obtenerUltimaEncuesta()
        // Primera corrida tras instalar/iniciar sesion: no hay que volcar
        // toda la ventana por defecto (1h) de golpe. Solo se deja el cursor
        // puesto y se marca lo existente como visto.
        val primeraCorrida = cursorGuardado == null

        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val desde = cursorGuardado
            ?: formato.format(Date(System.currentTimeMillis() - 3600000))

        return try {
            val respuesta = app.container.api.obtenerNotificaciones(
                token = "Bearer ${sesion.token}",
                desde = desde,
            )

            // El cursor avanza SIEMPRE tras una llamada exitosa: evita
            // re-traer (y re-notificar) la misma ventana en cada ciclo,
            // que es la causa principal del spam.
            if (respuesta.server_time.isNotBlank()) {
                store.guardarUltimaEncuesta(respuesta.server_time)
            }

            val vistas = store.obtenerClavesVistas()
            val nuevas = respuesta.notificaciones
                .map { it to claveDe(it) }
                .filter { (_, clave) -> clave !in vistas }

            when {
                primeraCorrida -> Unit // solo sembrar cursor + claves vistas

                nuevas.size > MAX_INDIVIDUALES -> mostrarNotificacion(
                    id = ID_RESUMEN,
                    titulo = "${nuevas.size} notificaciones nuevas",
                    mensaje = "Tienes ${nuevas.size} avisos de soporte sin leer. Abre la app para verlos.",
                )

                else -> nuevas.forEach { (notif, clave) ->
                    mostrarNotificacion(
                        // ID estable por notificacion: la misma se actualiza
                        // en su sitio en vez de apilarse.
                        id = ID_INDIVIDUAL_BASE + (clave.hashCode() and 0xFFFF),
                        titulo = notif.titulo,
                        mensaje = notif.mensaje,
                    )
                }
            }

            val clavesAMarcar =
                if (primeraCorrida) respuesta.notificaciones.map { claveDe(it) }
                else nuevas.map { it.second }
            store.agregarClavesVistas(clavesAMarcar)

            // Tambien checar version silenciosamente
            app.container.updateManager.checarYDescargar { versionName, _, _, _ ->
                mostrarNotificacion(ID_UPDATE, "Nueva Versión Disponible", "La versión $versionName está lista para instalar.")
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    /**
     * Clave estable para deduplicar entre corridas. Usa el id que mande el
     * backend en `data` si existe; si no, cae a tipo+titulo (el titulo trae
     * el folio, p. ej. "[#INC14163666] ...").
     */
    private fun claveDe(n: NotificacionDto): String =
        n.data?.get("id")?.takeIf { it.isNotBlank() }
            ?: n.data?.get("ticket_id")?.takeIf { it.isNotBlank() }
            ?: "${n.tipo}|${n.titulo}"

    private fun mostrarNotificacion(id: Int, titulo: String, mensaje: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canalObj = NotificationChannel(CANAL, "Notificaciones de Sistema", NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(canalObj)
        }

        val notification = NotificationCompat.Builder(applicationContext, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        try {
            NotificationManagerCompat.from(applicationContext).notify(id, notification)
        } catch (_: SecurityException) {}
    }

    companion object {
        private const val NOMBRE = "worker_notificaciones_global"
        private const val CANAL = "canal_notificaciones_pulso"
        // Si en una corrida hay mas nuevas que esto, se colapsan en una
        // sola notificacion-resumen en vez de disparar N.
        private const val MAX_INDIVIDUALES = 3
        private const val ID_UPDATE = 9999
        private const val ID_RESUMEN = 9998
        private const val ID_INDIVIDUAL_BASE = 10000

        fun agendar(context: Context) {
            val trabajo = PeriodicWorkRequestBuilder<NotificacionesWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NOMBRE,
                ExistingPeriodicWorkPolicy.KEEP,
                trabajo,
            )
        }

        fun cancelar(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(NOMBRE)
        }
    }
}
