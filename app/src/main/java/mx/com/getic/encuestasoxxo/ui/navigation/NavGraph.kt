package mx.com.getic.encuestasoxxo.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import mx.com.getic.encuestasoxxo.AppContainer
import mx.com.getic.encuestasoxxo.BuildConfig
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.ui.AppViewModelFactory
import mx.com.getic.encuestasoxxo.ui.encuesta.EncuestaScreen
import mx.com.getic.encuestasoxxo.ui.encuesta.EncuestaViewModel
import mx.com.getic.encuestasoxxo.ui.historial.HistorialScreen
import mx.com.getic.encuestasoxxo.ui.historial.HistorialViewModel
import mx.com.getic.encuestasoxxo.ui.login.ChangePasswordScreen
import mx.com.getic.encuestasoxxo.ui.login.ChangePasswordViewModel
import mx.com.getic.encuestasoxxo.ui.login.LoginScreen
import mx.com.getic.encuestasoxxo.ui.login.LoginViewModel
import mx.com.getic.encuestasoxxo.ui.preguntas.PreguntasScreen
import mx.com.getic.encuestasoxxo.ui.preguntas.PreguntasViewModel
import mx.com.getic.encuestasoxxo.ui.usuarios.UsuariosScreen
import mx.com.getic.encuestasoxxo.ui.usuarios.UsuariosViewModel
import mx.com.getic.encuestasoxxo.ui.perfil.PerfilScreen
import mx.com.getic.encuestasoxxo.ui.perfil.PerfilViewModel

object Rutas {
    const val LOGIN = "login"
    const val CHANGE_PASSWORD = "change_password"
    const val ENCUESTA = "encuesta"
    const val HISTORIAL = "historial"
    const val USUARIOS = "usuarios"
    const val PREGUNTAS = "preguntas"
    const val RESPUESTAS = "respuestas"
    const val PERFIL = "perfil"
}

@Composable
fun NavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val sesionState by container.sessionManager.sesionActual.collectAsState(initial = null)
    var revisado by remember { mutableStateOf(false) }

    // Espera UNA lectura de DataStore antes de decidir la pantalla de
    // arranque -- evita un parpadeo al Login si ya habia sesion guardada.
    LaunchedEffect(sesionState) {
        if (!revisado) revisado = true
    }

    if (!revisado) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val inicio = when {
        sesionState == null -> Rutas.LOGIN
        sesionState!!.debeCambiarPassword -> Rutas.CHANGE_PASSWORD
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
                onLoginExitoso = { rol, debeCambiar ->
                    if (debeCambiar) {
                        navController.navigate(Rutas.CHANGE_PASSWORD) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    } else {
                        val destino = when (rol) {
                            "ATI" -> Rutas.HISTORIAL
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
                        onAbrirMenu = abrirMenu,
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

                // Historial para ATI y otros, excepto WEBMASTER y PFS
                if (sesion.rol != "WEBMASTER" && sesion.rol != "PFS") {
                    NavigationDrawerItem(
                        label = { Text("Historial de NPS") },
                        selected = false,
                        icon = { Icon(Icons.Filled.History, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.HISTORIAL) },
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
                if (sesion.gestionaPreguntas) {
                    NavigationDrawerItem(
                        label = { Text("Preguntas") },
                        selected = false,
                        icon = { Icon(Icons.Filled.QuestionAnswer, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.PREGUNTAS) },
                    )
                }
                if (sesion.gestionaUsuarios) {
                    NavigationDrawerItem(
                        label = { Text("Usuarios") },
                        selected = false,
                        icon = { Icon(Icons.Filled.People, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.USUARIOS) },
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesion") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Logout, contentDescription = null) },
                    onClick = {
                        scope.launch {
                            container.authRepository.logout()
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
