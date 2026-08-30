package mx.com.getic.encuestasoxxo.ui.sync

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.R

@Composable
fun SyncScreen(
    viewModel: SyncViewModel,
    onTerminado: () -> Unit
) {
    if (viewModel.terminado) {
        LaunchedEffect(Unit) {
            onTerminado()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_pulso_ti),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            
            if (!viewModel.terminado && viewModel.updateAvailable == null) {
                CircularProgressIndicator()
            }
            
            Text(
                text = viewModel.estado,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            if (viewModel.estado.contains("Error")) {
                Button(onClick = { viewModel.iniciarSincronizacion() }) {
                    Text("Reintentar")
                }
            }
        }
    }

    // Diálogo de actualización
    val contexto = LocalContext.current
    viewModel.updateAvailable?.let { info ->
        AlertDialog(
            onDismissRequest = { if (!info.obligatoria) viewModel.ignorarActualizacion() },
            title = { Text("Actualización disponible") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Hay una nueva versión disponible (${info.versionName}). " +
                            if (info.obligatoria) "Es necesario actualizar para continuar." else "¿Deseas actualizar ahora?"
                    )
                    if (info.novedades.isNotBlank()) {
                        HorizontalDivider()
                        Text("Novedades de esta versión:", fontWeight = FontWeight.Medium)
                        // Notas en columna scrollable por si son largas y no caben en el dialogo
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
                                contexto.startActivity(intent)
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
                    TextButton(onClick = { viewModel.ignorarActualizacion() }) {
                        Text("Más tarde")
                    }
                }
            } else null
        )
    }
}
