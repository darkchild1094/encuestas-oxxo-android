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
import mx.com.getic.encuestasoxxo.BuildConfig
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
        if (sesion.rol != "ATI") return Result.success()

        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val ultimaFecha = app.container.notificacionesStore.obtenerUltimaEncuesta()
        if (ultimaFecha == null) {
            app.container.notificacionesStore.guardarUltimaEncuesta(formato.format(Date()))
            return Result.success()
        }

        return try {
            val respuesta = app.container.api.encuestasNuevas(
                token = "Bearer ${sesion.token}",
                desde = ultimaFecha,
            )
            if (respuesta.encuestas.isNotEmpty()) {
                mostrarNotificacion(respuesta.encuestas.size, respuesta.encuestas.first().tienda)
                app.container.notificacionesStore.guardarUltimaEncuesta(respuesta.ultima_fecha)
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun mostrarNotificacion(cantidad: Int, tienda: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CANAL, "Encuestas nuevas", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        val texto = if (cantidad == 1) {
            "Nueva encuesta en $tienda"
        } else {
            "$cantidad encuestas nuevas, incluida $tienda"
        }
        val notification = NotificationCompat.Builder(applicationContext, CANAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Resultados actualizados")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(ID_NOTIFICACION, notification)
    }

    companion object {
        private const val NOMBRE = "notificar_encuestas_ati"
        private const val CANAL = "encuestas_nuevas"
        private const val ID_NOTIFICACION = 7001

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
