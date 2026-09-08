package com.kernel94.pulsoti

import android.content.Context
import com.kernel94.pulsoti.data.SessionManager
import com.kernel94.pulsoti.data.UsuariosRecordadosStore
import com.kernel94.pulsoti.data.NotificacionesStore
import com.kernel94.pulsoti.data.local.AppDatabase
import com.kernel94.pulsoti.data.remote.RetrofitClient
import com.kernel94.pulsoti.data.repository.AuthRepository
import com.kernel94.pulsoti.data.repository.EncuestaRepository
import com.kernel94.pulsoti.data.repository.DashboardRepository
import com.kernel94.pulsoti.data.repository.UsuarioRepository
import com.kernel94.pulsoti.data.repository.SoporteRepository
import com.kernel94.pulsoti.data.repository.CatalogoRepository
import com.kernel94.pulsoti.domain.GeneralSyncManager
import com.kernel94.pulsoti.utils.UpdateManager

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

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(api, sessionManager)
    }

    val soporteRepository: SoporteRepository by lazy {
        SoporteRepository(api, sessionManager)
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository(api, sessionManager, database.usuarioDao())
    }

    val catalogoRepository: CatalogoRepository by lazy {
        CatalogoRepository(api, database.catalogoDao())
    }

    val generalSyncManager: GeneralSyncManager by lazy {
        GeneralSyncManager(database, api)
    }

    val updateManager: UpdateManager by lazy {
        UpdateManager(context, api)
    }
}
