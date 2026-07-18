package mx.com.getic.encuestasoxxo.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private fun colorPara(numero: Int): Color = when {
    numero <= 6 -> Color(0xFFDA3E64)
    numero <= 8 -> Color(0xFFF5B913)
    else -> Color(0xFF3AAE7A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(viewModel: HistorialViewModel, onAbrirMenu: () -> Unit) {
    val estado = viewModel.estado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus tiendas -- historial") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
            )
        },
    ) { padding ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (estado.error != null) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Text(estado.error)
            }
            return@Scaffold
        }

        if (estado.encuestas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Todavia no hay respuestas en tus tiendas asignadas.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(estado.encuestas, key = { it.encuestaId }) { enc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${enc.tiendaCodigo} - ${enc.tienda}", style = MaterialTheme.typography.titleMedium)
                        Text(enc.fecha, style = MaterialTheme.typography.labelMedium)

                        enc.calificaciones.forEach { (pregunta, cal) ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(pregunta, modifier = Modifier.weight(1f))
                                Text(
                                    "$cal/10",
                                    color = colorPara(cal),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }

                        if (!enc.comentario.isNullOrBlank()) {
                            HorizontalDivider()
                            Text("\"${enc.comentario}\"", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}