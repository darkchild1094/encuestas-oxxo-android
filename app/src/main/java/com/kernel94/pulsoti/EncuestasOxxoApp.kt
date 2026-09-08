package com.kernel94.pulsoti

import android.app.Application
import com.kernel94.pulsoti.sync.SincronizacionWorker
import timber.log.Timber

class EncuestasOxxoApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        container = AppContainer(this)

        // Red de seguridad para las encuestas que quedaron sin subir:
        // barre las pendientes cada pocas horas aunque nadie abra la
        // pantalla de Encuesta. Es idempotente y unico, se puede llamar
        // en cada arranque sin duplicar trabajo.
        SincronizacionWorker.agendarPeriodico(this)
    }
}
