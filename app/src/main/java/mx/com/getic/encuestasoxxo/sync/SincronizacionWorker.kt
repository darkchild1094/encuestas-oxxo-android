package mx.com.getic.encuestasoxxo.sync

import android.content.Context
import androidx.work.*
import mx.com.getic.encuestasoxxo.EncuestasOxxoApp
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

        fun agendar(context: Context) {
            val restriccion = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val trabajo = OneTimeWorkRequestBuilder<SincronizacionWorker>()
                .setConstraints(restriccion)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NOMBRE,
                ExistingWorkPolicy.REPLACE,
                trabajo,
            )
        }
    }
}
