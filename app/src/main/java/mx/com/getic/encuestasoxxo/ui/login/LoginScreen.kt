package mx.com.getic.encuestasoxxo.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.com.getic.encuestasoxxo.R
import mx.com.getic.encuestasoxxo.data.UsuarioRecordado

private fun urlFoto(rutaFoto: String?, apiBaseUrl: String): String? {
    if (rutaFoto.isNullOrBlank()) return null
    if (rutaFoto.startsWith("http")) return rutaFoto
    val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
    return "$base/$rutaFoto"
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    apiBaseUrl: String,
    onLoginExitoso: (rol: String, debeCambiar: Boolean) -> Unit,
) {
    val estado = viewModel.estado
    val cuentasRecordadas by viewModel.cuentasRecordadas.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Se muestra la lista de cuentas recordadas solo si hay alguna Y
    // todavia no se eligio ninguna (ni se esta escribiendo un correo
    // nuevo a mano).
    val mostrarListaCuentas = cuentasRecordadas.isNotEmpty() &&
        estado.mostrarCuentasRecordadas &&
        estado.cuentaSeleccionada == null &&
        estado.correo.isBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.background,
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_pulso_ti),
                contentDescription = "Pulso TI",
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(bottom = 8.dp)
            )

            Text(
                "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(8.dp))

            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (mostrarListaCuentas) {
                        Text(
                            "Continuar como...",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(cuentasRecordadas) { cuenta ->
                                BurbujaUsuario(
                                    cuenta = cuenta,
                                    apiBaseUrl = apiBaseUrl,
                                    onClick = { viewModel.seleccionarCuenta(cuenta) },
                                    onOlvidar = { viewModel.olvidarCuenta(cuenta) }
                                )
                            }
                            item {
                                BurbujaNuevaCuenta(onClick = { viewModel.usarOtraCuenta() })
                            }
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    }

                    if (estado.cuentaSeleccionada != null) {
                        // Cuenta ya elegida: solo se confirma quien es y se
                        // pide la contraseña, no hay que volver a teclear
                        // el correo.
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            val fotoUrl = remember(estado.cuentaSeleccionada.fotoPerfil, apiBaseUrl) {
                                urlFoto(estado.cuentaSeleccionada.fotoPerfil, apiBaseUrl)
                            }
                            AvatarConFoto(
                                fotoUrl = fotoUrl,
                                nombre = estado.cuentaSeleccionada.nombre,
                                size = 48.dp
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    estado.cuentaSeleccionada.nombre.ifBlank { estado.cuentaSeleccionada.correo },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    estado.cuentaSeleccionada.correo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                            TextButton(onClick = { viewModel.usarOtraCuenta() }) {
                                Text("Cambiar")
                            }
                        }
                    } else if (!mostrarListaCuentas) {
                        OutlinedTextField(
                            value = estado.correo,
                            onValueChange = viewModel::onCorreoChange,
                            label = { Text("Correo electrónico") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )
                    }

                    if (!mostrarListaCuentas) {
                        OutlinedTextField(
                            value = estado.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        )

                        if (estado.error != null) {
                            Text(
                                text = estado.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.login(onLoginExitoso) },
                            enabled = !estado.cargando,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (estado.cargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("ENTRAR", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BurbujaUsuario(
    cuenta: UsuarioRecordado,
    apiBaseUrl: String,
    onClick: () -> Unit,
    onOlvidar: () -> Unit,
) {
    val fotoUrl = remember(cuenta.fotoPerfil, apiBaseUrl) { urlFoto(cuenta.fotoPerfil, apiBaseUrl) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClick),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp
            ) {
                AvatarConFoto(fotoUrl = fotoUrl, nombre = cuenta.nombre, size = 56.dp)
            }
            Surface(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = 4.dp, y = (-4).dp)
                    .clip(CircleShape)
                    .clickable(onClick = onOlvidar),
                color = MaterialTheme.colorScheme.errorContainer,
                tonalElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Quitar",
                    modifier = Modifier.padding(4.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = cuenta.nombre.split(" ").firstOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BurbujaNuevaCuenta(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Usar otra cuenta",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Otro",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun AvatarConFoto(
    fotoUrl: String?,
    nombre: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp
) {
    // Forzamos el tamaño tanto en el contenedor como en la imagen
    // para evitar que imagenes grandes rompan el layout.
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            AvatarInicial(nombreOCorreo = nombre, size = size)
        }
    }
}

@Composable
private fun AvatarInicial(nombreOCorreo: String, size: androidx.compose.ui.unit.Dp = 40.dp) {
    val inicial = nombreOCorreo.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (inicial == "?" || inicial == "") {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(size / 2),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Text(
                inicial,
                style = if (size < 48.dp) MaterialTheme.typography.titleSmall else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
