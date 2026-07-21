package mx.com.getic.encuestasoxxo.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.R
import mx.com.getic.encuestasoxxo.data.UsuarioRecordado

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (rol: String, debeCambiar: Boolean) -> Unit,
) {
    val estado = viewModel.estado
    val cuentasRecordadas by viewModel.cuentasRecordadas.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    // Se muestra la lista de cuentas recordadas solo si hay alguna Y
    // todavia no se eligio ninguna (ni se esta escribiendo un correo
    // nuevo a mano).
    val mostrarListaCuentas = cuentasRecordadas.isNotEmpty() &&
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
                            "Cuentas de este dispositivo",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            cuentasRecordadas.forEach { cuenta ->
                                FilaCuentaRecordada(
                                    cuenta = cuenta,
                                    onClick = { viewModel.seleccionarCuenta(cuenta) },
                                    onOlvidar = { viewModel.olvidarCuenta(cuenta) },
                                )
                            }
                        }
                        TextButton(
                            onClick = { viewModel.usarOtraCuenta() },
                            modifier = Modifier.align(Alignment.End),
                        ) {
                            Text("Usar otra cuenta")
                        }
                        HorizontalDivider()
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
                            AvatarInicial(nombreOCorreo = estado.cuentaSeleccionada.nombre.ifBlank { estado.cuentaSeleccionada.correo })
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
private fun FilaCuentaRecordada(
    cuenta: UsuarioRecordado,
    onClick: () -> Unit,
    onOlvidar: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        AvatarInicial(nombreOCorreo = cuenta.nombre.ifBlank { cuenta.correo })
        Column(Modifier.weight(1f)) {
            Text(
                cuenta.nombre.ifBlank { cuenta.correo },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                cuenta.correo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        IconButton(onClick = onOlvidar) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Olvidar esta cuenta",
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun AvatarInicial(nombreOCorreo: String) {
    val inicial = nombreOCorreo.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (inicial == "?") {
            Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        } else {
            Text(
                inicial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
