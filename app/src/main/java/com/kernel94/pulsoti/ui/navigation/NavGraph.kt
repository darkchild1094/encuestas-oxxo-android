package com.kernel94.pulsoti.ui.navigation

import android.Manifest
import android.app.Activity
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.kernel94.pulsoti.AppContainer
import com.kernel94.pulsoti.BuildConfig
import com.kernel94.pulsoti.data.Sesion
import com.kernel94.pulsoti.ui.AppViewModelFactory
import com.kernel94.pulsoti.ui.encuesta.EncuestaScreen
import com.kernel94.pulsoti.ui.encuesta.EncuestaViewModel
import com.kernel94.pulsoti.ui.historial.HistorialScreen
import com.kernel94.pulsoti.ui.historial.HistorialViewModel
import com.kernel94.pulsoti.ui.login.ChangePasswordScreen
import com.kernel94.pulsoti.ui.login.ChangePasswordViewModel
import com.kernel94.pulsoti.ui.login.LoginScreen
import com.kernel94.pulsoti.ui.login.LoginViewModel
import com.kernel94.pulsoti.ui.preguntas.PreguntasScreen
import com.kernel94.pulsoti.ui.preguntas.PreguntasViewModel
import com.kernel94.pulsoti.ui.areas.AreasScreen
import com.kernel94.pulsoti.ui.areas.AreasViewModel
import com.kernel94.pulsoti.ui.encuestaoficina.EncuestaOficinaScreen
import com.kernel94.pulsoti.ui.encuestaoficina.EncuestaOficinaViewModel
import com.kernel94.pulsoti.ui.tiendas.TiendasScreen
import com.kernel94.pulsoti.ui.tiendas.TiendasViewModel
import com.kernel94.pulsoti.ui.dashboard.DashboardScreen
import com.kernel94.pulsoti.ui.dashboard.DashboardViewModel
import com.kernel94.pulsoti.ui.usuarios.UsuariosScreen
import com.kernel94.pulsoti.ui.usuarios.UsuariosViewModel
import com.kernel94.pulsoti.sync.NotificacionesWorker
import com.kernel94.pulsoti.ui.perfil.PerfilScreen
import com.kernel94.pulsoti.ui.perfil.PerfilViewModel
import com.kernel94.pulsoti.ui.pfs.PFSModuloScreen
import com.kernel94.pulsoti.ui.pfs.PFSModuloViewModel
import com.kernel94.pulsoti.ui.sync.SyncScreen
import com.kernel94.pulsoti.ui.sync.SyncViewModel
import com.kernel94.pulsoti.ui.soporte.SoporteScreen
import com.kernel94.pulsoti.ui.soporte.SoporteViewModel
import com.kernel94.pulsoti.ui.soporte.SoporteDetalleScreen
import com.kernel94.pulsoti.ui.soporte.SoporteDetalleViewModel

object Rutas {
    const val LOGIN = "login"
    const val CHANGE_PASSWORD = "change_password"
    const val ENCUESTA = "encuesta"
    const val HISTORIAL = "historial"
    const val USUARIOS = "usuarios"
    const val PREGUNTAS = "preguntas"
    const val AREAS = "areas"
    const val ENCUESTA_OFICINA = "encuesta_oficina"
    const val TIENDAS = "tiendas"
    const val DASHBOARD = "dashboard"
    const val RESPUESTAS = "respuestas"
    const val PERFIL = "perfil"
    const val PFS = "pfs"
    const val SYNC = "sync"
    const val SOPORTE = "soporte"
    const val SOPORTE_DETALLE = "soporte_detalle/{id}"
}

@Composable
fun NavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val sesionState by container.sessionManager.sesionActual.collectAsState(initial = null)
    val context = LocalContext.current
    var revisado by remember { mutableStateOf(false) }

    // Espera UNA lectura de DataStore antes de decidir la pantalla de
    // arranque -- evita un parpadeo al Login si ya habia sesion guardada.
    LaunchedEffect(sesionState) {
        if (!revisado) revisado = true
        if (sesionState != null) {
            // Solicitar permiso de notificaciones para todos en Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                (context as? Activity)?.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
            // Agendar notificaciones para todos los roles
            NotificacionesWorker.agendar(context)
        } else {
            NotificacionesWorker.cancelar(context)
        }
    }

    if (!revisado) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // Validacion de sesion EN SEGUNDO PLANO, sin bloquear el arranque
    // (el timeout de red es de 30s -- bloquear aqui congelaria la app
    // ese tiempo si el servidor esta lento/caido). Si el token ya no
    // sirve, validarSesionSiHayInternet() cierra la sesion local por su
    // cuenta; como sesionState es un Flow, NavGraph se entera solo y
    // recompone hacia Login. Corre una sola vez por sesion activa
    // (queda "atado" al token: si cambia -por logout/login- se re-evalua).
    LaunchedEffect(sesionState?.token) {
        sesionState?.let { container.authRepository.validarSesionSiHayInternet(it.token) }
    }

    val inicio = when {
        sesionState == null -> Rutas.LOGIN
        !sesionState!!.syncRealizado -> Rutas.SYNC
        sesionState!!.debeCambiarPassword -> Rutas.CHANGE_PASSWORD
        // ATI ahora tambien es_encuestable (puede contestar la encuesta
        // de tienda, como PFS), pero su pantalla de inicio sigue siendo
        // el Dashboard -- mismo criterio que ya usan SyncScreen.onTerminado
        // y LoginScreen.onLoginExitoso, aqui les faltaba este caso.
        sesionState!!.rol == "ATI" -> Rutas.DASHBOARD
        sesionState!!.esEncuestable -> Rutas.ENCUESTA
        sesionState!!.rol == "WEBMASTER" -> Rutas.USUARIOS
        else -> Rutas.HISTORIAL
    }

    NavHost(navController = navController, startDestination = inicio) {
        composable(Rutas.LOGIN) {
            val factory = AppViewModelFactory(container)
            val viewModel = viewModel { factory.create(LoginViewModel::class.java) }
            LoginScreen(
                viewModel = viewModel,
                apiBaseUrl = BuildConfig.API_BASE_URL,
                onLoginExitoso = { rol, debeCambiar ->
                    if (debeCambiar) {
                        navController.navigate(Rutas.CHANGE_PASSWORD) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    } else {
                        val destino = when (rol) {
                            "ATI" -> Rutas.DASHBOARD
                            "WEBMASTER" -> Rutas.USUARIOS
                            else -> Rutas.ENCUESTA
                        }
                        navController.navigate(destino) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    }
                },
            )
        }

        composable(Rutas.CHANGE_PASSWORD) {
            val factory = AppViewModelFactory(container)
            val viewModel = viewModel { factory.create(ChangePasswordViewModel::class.java) }
            ChangePasswordScreen(viewModel = viewModel, onExito = {
                navController.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } }
            })
        }

        composable(Rutas.SYNC) {
            val sesion = sesionState
            if (sesion != null) {
                val factory = AppViewModelFactory(container, sesion)
                val viewModel = viewModel { factory.create(SyncViewModel::class.java) }
                SyncScreen(viewModel = viewModel, onTerminado = {
                    val destino = when {
                        sesion.debeCambiarPassword -> Rutas.CHANGE_PASSWORD
                        sesion.rol == "ATI" -> Rutas.DASHBOARD
                        sesion.esEncuestable -> Rutas.ENCUESTA
                        sesion.rol == "WEBMASTER" -> Rutas.USUARIOS
                        else -> Rutas.HISTORIAL
                    }
                    navController.navigate(destino) { popUpTo(Rutas.SYNC) { inclusive = true } }
                })
            }
        }

        composable(Rutas.ENCUESTA) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(EncuestaViewModel::class.java) }
                    EncuestaScreen(
                        viewModel = viewModel,
                        sesion = sesion,
                        apiBaseUrl = BuildConfig.API_BASE_URL,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.HISTORIAL) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container)
                    val viewModel = viewModel { factory.create(HistorialViewModel::class.java) }
                    HistorialScreen(
                        viewModel = viewModel,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.USUARIOS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container)
                    val viewModel = viewModel { factory.create(UsuariosViewModel::class.java) }
                    UsuariosScreen(
                        viewModel = viewModel,
                        onAbrirMenu = abrirMenu,
                        apiBaseUrl = BuildConfig.API_BASE_URL
                    )
                }
            }
        }

        composable(Rutas.PREGUNTAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(PreguntasViewModel::class.java) }
                    PreguntasScreen(
                        viewModel = viewModel,
                        apiBaseUrl = BuildConfig.API_BASE_URL,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.AREAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(AreasViewModel::class.java) }
                    AreasScreen(
                        viewModel = viewModel,
                        apiBaseUrl = BuildConfig.API_BASE_URL,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.ENCUESTA_OFICINA) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(EncuestaOficinaViewModel::class.java) }
                    EncuestaOficinaScreen(
                        viewModel = viewModel,
                        sesion = sesion,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.TIENDAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(TiendasViewModel::class.java) }
                    TiendasScreen(
                        viewModel = viewModel,
                        sesion = sesion,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.DASHBOARD) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(DashboardViewModel::class.java) }
                    DashboardScreen(
                        viewModel = viewModel,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.PFS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(PFSModuloViewModel::class.java) }
                    PFSModuloScreen(
                        viewModel = viewModel,
                        onAbrirMenu = abrirMenu
                    )
                }
            }
        }

        composable(Rutas.RESPUESTAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { _ ->
                    androidx.compose.material3.Text(
                        "Respuestas de tiendas -- siguiente avance (necesita API de lectura)",
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        }

        composable(Rutas.PERFIL) {
            val sesion = sesionState
            if (sesion != null) {
                val factory = AppViewModelFactory(container, sesion)
                val viewModel = viewModel { factory.create(PerfilViewModel::class.java) }
                PerfilScreen(
                    viewModel = viewModel,
                    sesion = sesion,
                    apiBaseUrl = BuildConfig.API_BASE_URL,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Rutas.SOPORTE) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container, BuildConfig.API_BASE_URL) { abrirMenu ->
                    val factory = AppViewModelFactory(container, sesion)
                    val viewModel = viewModel { factory.create(SoporteViewModel::class.java) }
                    SoporteScreen(
                        viewModel = viewModel,
                        sesion = sesion,
                        onAbrirMenu = abrirMenu,
                        onVerDetalle = { id -> navController.navigate("soporte_detalle/$id") }
                    )
                }
            }
        }

        composable(Rutas.SOPORTE_DETALLE) { backStackEntry ->
            val sesion = sesionState
            val ticketId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (sesion != null && ticketId != null) {
                val factory = AppViewModelFactory(container, sesion, extraId = ticketId)
                val viewModel = viewModel { factory.create(SoporteDetalleViewModel::class.java) }
                SoporteDetalleScreen(
                    viewModel = viewModel,
                    sesion = sesion,
                    apiBaseUrl = BuildConfig.API_BASE_URL,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Envuelve cualquier pantalla de "adentro" con el drawer lateral,
// cuyas opciones se prenden o apagan segun los permisos de la sesion
// -- mismo criterio que ya usa el panel web.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConDrawer(
    navController: NavHostController,
    sesion: Sesion,
    container: AppContainer,
    apiBaseUrl: String,
    contenido: @Composable (abrirMenu: () -> Unit) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val fotoUrl = remember(sesion.fotoPerfil, apiBaseUrl) {
        sesion.fotoPerfil?.let { perfil ->
            if (perfil.startsWith("http")) {
                perfil
            } else {
                val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
                "$base/$perfil"
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { scope.launch { drawerState.close() }; navController.navigate(Rutas.PERFIL) },
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        if (fotoUrl != null) {
                            AsyncImage(
                                model = fotoUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column {
                        Text(
                            text = sesion.nombreCompleto.ifBlank { "Usuario" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sesion.rol,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.PERFIL) },
                )

                NavigationDrawerItem(
                    label = { Text(if (sesion.rol == "WEBMASTER") "Atención a Soporte" else "Reportar Problema") },
                    selected = false,
                    icon = { Icon(Icons.Filled.BugReport, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.SOPORTE) },
                )

                NavigationDrawerItem(
                    label = { Text("Estado de Envíos") },
                    selected = false,
                    icon = { Icon(Icons.Filled.CloudSync, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.PFS) },
                )

                if (sesion.rol == "ATI") {
                    NavigationDrawerItem(
                        label = { Text("Dashboard") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.DASHBOARD) },
                    )
                    if (sesion.veResultadosTiendas) {
                        NavigationDrawerItem(
                            label = { Text("Respuestas de tiendas") },
                            selected = false,
                            icon = { Icon(Icons.Filled.History, contentDescription = null) },
                            onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.HISTORIAL) },
                        )
                    }
                    NavigationDrawerItem(
                        label = { Text("Tiendas") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Store, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.TIENDAS) },
                    )
                    NavigationDrawerItem(
                        label = { Text("Áreas (encuesta de oficina)") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Business, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.AREAS) },
                    )
                }

                if (sesion.esEncuestable) {
                    NavigationDrawerItem(
                        label = { Text("Responder encuesta") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.ENCUESTA) },
                    )
                }
                if (sesion.contestaOficina) {
                    NavigationDrawerItem(
                        label = { Text("Responder encuesta de oficina") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Assignment, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.ENCUESTA_OFICINA) },
                    )
                }
                if (sesion.gestionaPreguntas) {
                    NavigationDrawerItem(
                        label = { Text("Preguntas") },
                        selected = false,
                        icon = { Icon(Icons.Filled.QuestionAnswer, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.PREGUNTAS) },
                    )
                }

                if (sesion.gestionaUsuarios || sesion.usuarioId == 128) {
                    NavigationDrawerItem(
                        label = { Text("Usuarios") },
                        selected = false,
                        icon = { Icon(Icons.Filled.People, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.USUARIOS) },
                    )
                }

                Spacer(Modifier.weight(1f))
                
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )

                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesion") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Logout, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            container.authRepository.logout()
                            NotificacionesWorker.cancelar(context)
                            drawerState.close()
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                )
                Spacer(Modifier.height(12.dp))
            }
        },
    ) {
        contenido { scope.launch { drawerState.open() } }
    }
}
