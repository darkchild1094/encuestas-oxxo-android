package mx.com.getic.encuestasoxxo

import android.app.Application

class EncuestasOxxoApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
