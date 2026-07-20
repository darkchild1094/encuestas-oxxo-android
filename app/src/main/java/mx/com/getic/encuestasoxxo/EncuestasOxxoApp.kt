package mx.com.getic.encuestasoxxo

import android.app.Application
import timber.log.Timber

class EncuestasOxxoApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        container = AppContainer(this)
    }
}
