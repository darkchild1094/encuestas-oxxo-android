package mx.com.getic.encuestasoxxo.ui.perfil

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    sesion: Sesion,
    apiBaseUrl: String,
    onBack: () -> Unit
) {
    val estado = viewModel.estado
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onFotoSelected(uri)
    }

    LaunchedEffect(estado.exito) {
        if (estado.exito) {
            Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(estado.error) {
        estado.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(estado.sinActualizaciones) {
        if (estado.sinActualizaciones) {
            Toast.makeText(context, "Ya tienes la última versión instalada", Toast.LENGTH_SHORT).show()
            viewModel.limpiarSinActualizaciones()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        LoadingOverlay(mostrar = estado.cargando)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Foto de perfil
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(120.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 2.dp
                ) {
                    val fotoAMostrar = if (estado.fotoUri != null) {
                        estado.fotoUri
                    } else if (sesion.fotoPerfil != null) {
                        val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
                        "$base/${sesion.fotoPerfil}"
                    } else {
                        null
                    }

                    if (fotoAMostrar != null) {
                        AsyncImage(
                            model = fotoAMostrar,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                SmallFloatingActionButton(
                    onClick = { launcher.launch("image/*") },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cambiar foto", modifier = Modifier.size(18.dp))
                }
            }

            Text(
                text = sesion.correo,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "Cambiar contraseña (deja en blanco si no quieres cambiarla)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            var passVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = estado.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                    }
                }
            )

            OutlinedTextField(
                value = estado.confirmPassword,
                onValueChange = viewModel::onConfirmChange,
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardar(context) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !estado.cargando
            ) {
                if (estado.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar cambios")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Versión instalada: ${viewModel.versionActual}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedButton(
                onClick = { viewModel.buscarActualizaciones() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !estado.buscandoActualizacion
            ) {
                if (estado.buscandoActualizacion) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buscando...")
                } else {
                    Icon(Icons.Filled.SystemUpdate, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Buscar actualizaciones")
                }
            }
        }
    }

    // Diálogo de actualización disponible (búsqueda manual)
    val contextoUpdate = LocalContext.current
    estado.actualizacionDisponible?.let { info ->
        AlertDialog(
            onDismissRequest = { if (!info.obligatoria) viewModel.cerrarDialogoActualizacion() },
            title = { Text("Actualización disponible") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Hay una nueva versión disponible (${info.versionName}). " +
                            if (info.obligatoria) "Es necesario actualizar para continuar usando la app." else "¿Deseas actualizarla ahora?"
                    )
                    if (info.novedades.isNotBlank()) {
                        HorizontalDivider()
                        Text("Novedades de esta versión:", fontWeight = FontWeight.Medium)
                        Column(
                            modifier = Modifier
                                .heightIn(max = 220.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(info.novedades, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (info.url.isNotBlank()) {
                        HorizontalDivider()
                        Text(
                            "¿No se descarga sola? Toca aquí para bajarla manualmente desde el navegador:",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = "Descargar APK manualmente",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(info.url))
                                contextoUpdate.startActivity(intent)
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.descargarActualizacion() }) {
                    Text("Actualizar")
                }
            },
            dismissButton = if (!info.obligatoria) {
                {
                    TextButton(onClick = { viewModel.cerrarDialogoActualizacion() }) {
                        Text("Más tarde")
                    }
                }
            } else null
        )
    }
}
