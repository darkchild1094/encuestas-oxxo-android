package mx.com.getic.encuestasoxxo

import android.content.Context
import mx.com.getic.encuestasoxxo.data.SessionManager
import mx.com.getic.encuestasoxxo.data.UsuariosRecordadosStore
import mx.com.getic.encuestasoxxo.data.NotificacionesStore
import mx.com.getic.encuestasoxxo.data.local.AppDatabase
import mx.com.getic.encuestasoxxo.data.remote.RetrofitClient
import mx.com.getic.encuestasoxxo.data.repository.AuthRepository
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository
import mx.com.getic.encuestasoxxo.data.repository.EstadisticasRepository
import mx.com.getic.encuestasoxxo.data.repository.UsuarioRepository
import mx.com.getic.encuestasoxxo.data.repository.CatalogoRepository
import mx.com.getic.encuestasoxxo.domain.EncuestaSyncManager
import mx.com.getic.encuestasoxxo.domain.GeneralSyncManager
import mx.com.getic.encuestasoxxo.utils.UpdateManager

// Service Locator simple: un solo lugar donde se arman las
// dependencias, sin librerias de DI. Para el tamano de esta app
// (3-4 pantallas) es mas facil de leer que meter Hilt.
class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.obtener(context)
    val sessionManager: SessionManager = SessionManager(context)
    val usuariosRecordadosStore: UsuariosRecordadosStore = UsuariosRecordadosStore(context)
    val notificacionesStore: NotificacionesStore = NotificacionesStore(context)
    val api = RetrofitClient.api

    val authRepository: AuthRepository by lazy {
        AuthRepository(api, sessionManager, usuariosRecordadosStore)
    }

    val encuestaRepository: EncuestaRepository by lazy {
        EncuestaRepository(api, database.cuestionarioDao(), database.encuestaDao(), database.tiendaDao(), database.atiDao(), sessionManager)
    }

    val estadisticasRepository: EstadisticasRepository by lazy {
        EstadisticasRepository(api, sessionManager)
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository(api, sessionManager, database.usuarioDao())
    }

    val catalogoRepository: CatalogoRepository by lazy {
        CatalogoRepository(api, database.catalogoDao())
    }

    val encuestaSyncManager: EncuestaSyncManager by lazy {
        EncuestaSyncManager(database, api)
    }

    val generalSyncManager: GeneralSyncManager by lazy {
        GeneralSyncManager(database, api)
    }

    val updateManager: UpdateManager by lazy {
        UpdateManager(context, api)
    }
}
