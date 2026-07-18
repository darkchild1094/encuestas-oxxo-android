package mx.com.getic.encuestasoxxo.ui.navigation

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.AppContainer
import mx.com.getic.encuestasoxxo.BuildConfig
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.ui.encuesta.EncuestaScreen
import mx.com.getic.encuestasoxxo.ui.encuesta.EncuestaViewModel
import mx.com.getic.encuestasoxxo.ui.historial.HistorialScreen
import mx.com.getic.encuestasoxxo.ui.historial.HistorialViewModel
import mx.com.getic.encuestasoxxo.ui.login.LoginScreen
import mx.com.getic.encuestasoxxo.ui.login.LoginViewModel

object Rutas {
    const val LOGIN = "login"
    const val ENCUESTA = "encuesta"
    const val HISTORIAL = "historial"
    const val USUARIOS = "usuarios"
    const val PREGUNTAS = "preguntas"
    const val RESPUESTAS = "respuestas"
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
        sesionState!!.esEncuestable -> Rutas.ENCUESTA
        else -> Rutas.HISTORIAL
    }

    NavHost(navController = navController, startDestination = inicio) {
        composable(Rutas.LOGIN) {
            val viewModel = viewModel { LoginViewModel(container.authRepository) }
            LoginScreen(
                viewModel = viewModel,
                onLoginExitoso = { rol ->
                    val destino = if (rol == "ATI") Rutas.HISTORIAL else Rutas.ENCUESTA
                    navController.navigate(destino) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                },
            )
        }

        composable(Rutas.ENCUESTA) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container) { abrirMenu ->
                    val viewModel = viewModel {
                        EncuestaViewModel(container.encuestaRepository, sesion)
                    }
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
                ConDrawer(navController, sesion, container) { abrirMenu ->
                    val viewModel = viewModel {
                        mx.com.getic.encuestasoxxo.ui.historial.HistorialViewModel(container.encuestaRepository)
                    }
                    mx.com.getic.encuestasoxxo.ui.historial.HistorialScreen(
                        viewModel = viewModel,
                        onAbrirMenu = abrirMenu,
                    )
                }
            }
        }

        composable(Rutas.USUARIOS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container) { _ ->
                    androidx.compose.material3.Text(
                        "Gestionar usuarios -- siguiente avance (necesita API de CRUD)",
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        }

        composable(Rutas.PREGUNTAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container) { _ ->
                    androidx.compose.material3.Text(
                        "Gestionar preguntas -- siguiente avance (necesita API de CRUD)",
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }
        }

        composable(Rutas.RESPUESTAS) {
            val sesion = sesionState
            if (sesion != null) {
                ConDrawer(navController, sesion, container) { _ ->
                    androidx.compose.material3.Text(
                        "Respuestas de tiendas -- siguiente avance (necesita API de lectura)",
                        modifier = Modifier.padding(24.dp),
                    )
                }
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
    contenido: @Composable (abrirMenu: () -> Unit) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    sesion.nombreCompleto.ifBlank { sesion.correo },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    sesion.rol,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()

                if (sesion.esEncuestable) {
                    NavigationDrawerItem(
                        label = { Text("Responder encuesta") },
                        selected = false,
                        icon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.ENCUESTA) },
                    )
                }
                if (sesion.veResultadosTiendas) {
                    NavigationDrawerItem(
                        label = { Text("Respuestas de tiendas") },
                        selected = false,
                        icon = { Icon(Icons.Filled.BarChart, contentDescription = null) },
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(Rutas.RESPUESTAS) },
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
