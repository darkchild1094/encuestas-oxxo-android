package mx.com.getic.encuestasoxxo.sync

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
import mx.com.getic.encuestasoxxo.EncuestasOxxoApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificacionesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as EncuestasOxxoApp
        val sesion = app.container.sessionManager.sesionActualBloqueante()
            ?: return Result.success()

        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val ultimaFecha = app.container.notificacionesStore.obtenerUltimaEncuesta()
            ?: formato.format(Date(System.currentTimeMillis() - 3600000)) // 1 hora atras por default

        return try {
            val respuesta = app.container.api.obtenerNotificaciones(
                token = "Bearer ${sesion.token}",
                desde = ultimaFecha,
            )
            
            respuesta.notificaciones.forEachIndexed { index, notif ->
                mostrarNotificacion(
                    id = ID_BASE + index,
                    titulo = notif.titulo,
                    mensaje = notif.mensaje
                )
            }
            
            if (respuesta.notificaciones.isNotEmpty() || ultimaFecha.isBlank()) {
                app.container.notificacionesStore.guardarUltimaEncuesta(respuesta.server_time)
            }
            
            // Tambien checar version silenciosamente
            app.container.updateManager.checarYDescargar { versionName, _, _ ->
                mostrarNotificacion(ID_UPDATE, "Nueva Versión Disponible", "La versión $versionName está lista para instalar.")
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

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
        private const val ID_BASE = 8000
        private const val ID_UPDATE = 9999

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
