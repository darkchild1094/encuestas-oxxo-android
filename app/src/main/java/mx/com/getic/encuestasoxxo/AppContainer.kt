package mx.com.getic.encuestasoxxo

import android.content.Context
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.local.AppDatabase
import mx.com.getic.encuestasoxxo.data.remote.RetrofitClient
import mx.com.getic.encuestasoxxo.data.repository.AuthRepository
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository

// Service Locator simple: un solo lugar donde se arman las
// dependencias, sin librerias de DI. Para el tamano de esta app
// (3-4 pantallas) es mas facil de leer que meter Hilt.
class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.obtener(context)
    val sessionManager: SessionManager = SessionManager(context)
    val api = RetrofitClient.api

    val authRepository: AuthRepository by lazy {
        AuthRepository(api, sessionManager)
    }

    val encuestaRepository: EncuestaRepository by lazy {
        EncuestaRepository(api, database.cuestionarioDao(), database.encuestaDao(), sessionManager)
    }
}
