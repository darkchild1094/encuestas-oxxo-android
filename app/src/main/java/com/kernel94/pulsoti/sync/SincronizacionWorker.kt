package com.kernel94.pulsoti.sync

import android.content.Context
import androidx.work.*
import com.kernel94.pulsoti.EncuestasOxxoApp
import java.util.concurrent.TimeUnit

// Reintenta subir encuestas pendientes cuando hay señal. Se agenda
// cada vez que se guarda una encuesta localmente (por si el intento
// inmediato en el repositorio fallo por falta de conexion).
class SincronizacionWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val app = applicationContext as EncuestasOxxoApp
        val exito = app.container.encuestaRepository.intentarSincronizarPendientes()
        return if (exito) Result.success() else Result.retry()
    }

    companion object {
        private const val NOMBRE = "sincronizar_encuestas"
        private const val NOMBRE_PERIODICO = "sincronizar_encuestas_periodico"

        private val soloConRed = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Intento inmediato tras guardar una encuesta. REPLACE a proposito:
        // un guardado nuevo reinicia el backoff para que los reintentos
        // salgan pronto, y como la subida es idempotente en el servidor
        // (INSERT IGNORE por UUID) y manda TODAS las pendientes en lote,
        // cancelar una subida a medias no pierde nada -- el worker nuevo
        // la reintenta.
        fun agendar(context: Context) {
            val trabajo = OneTimeWorkRequestBuilder<SincronizacionWorker>()
                .setConstraints(soloConRed)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NOMBRE,
                ExistingWorkPolicy.REPLACE,
                trabajo,
            )
        }

        // Red de seguridad: aunque el intento inmediato agote su backoff,
        // o su cadena se haya cancelado, esto vuelve a barrer las
        // pendientes cada pocas horas. Se agenda una vez al arranque de
        // la app (EncuestasOxxoApp) y sobrevive reinicios del telefono.
        fun agendarPeriodico(context: Context) {
            val trabajo = PeriodicWorkRequestBuilder<SincronizacionWorker>(6, TimeUnit.HOURS)
                .setConstraints(soloConRed)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NOMBRE_PERIODICO,
                ExistingPeriodicWorkPolicy.KEEP,
                trabajo,
            )
        }
    }
}
