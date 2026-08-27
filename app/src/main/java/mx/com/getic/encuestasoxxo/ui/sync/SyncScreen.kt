package mx.com.getic.encuestasoxxo.ui.sync

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    viewModel.updateAvailable?.let { (versionName, _, obligatoria) ->
        AlertDialog(
            onDismissRequest = { if (!obligatoria) viewModel.ignorarActualizacion() },
            title = { Text("Actualización Disponible") },
            text = { 
                Text("Hay una nueva versión disponible ($versionName). ${if (obligatoria) "Es necesario actualizar para continuar." else "¿Deseas actualizar ahora?"}")
            },
            confirmButton = {
                Button(onClick = { viewModel.descargarActualizacion() }) {
                    Text("Actualizar")
                }
            },
            dismissButton = if (!obligatoria) {
                {
                    TextButton(onClick = { viewModel.ignorarActualizacion() }) {
                        Text("Más tarde")
                    }
                }
            } else null
        )
    }
}
