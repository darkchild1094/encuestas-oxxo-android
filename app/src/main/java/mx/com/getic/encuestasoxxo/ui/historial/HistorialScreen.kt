package mx.com.getic.encuestasoxxo.ui.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var tiendaSeleccionada by remember { mutableStateOf<String?>(null) }
    val atiSeleccionadoId = estado.atiSeleccionadoId
    val encuestasDelAti = estado.encuestas.filter { it.atiId == atiSeleccionadoId }
    val encuestasPorTienda = encuestasDelAti.groupBy { "${it.tiendaCodigo}|${it.tienda}" }
    val encuestasTienda = tiendaSeleccionada?.let { encuestasPorTienda[it].orEmpty() }.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (tiendaSeleccionada == null) "Tus tiendas -- historial"
                        else encuestasTienda.firstOrNull()?.tienda ?: "Historial de tienda"
                    )
                },
                navigationIcon = {
                    if (tiendaSeleccionada == null) {
                        IconButton(onClick = onAbrirMenu) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = { tiendaSeleccionada = null }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver a tiendas")
                        }
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

        if (estado.encuestas.isEmpty() && estado.atis.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Todavia no hay respuestas en tus tiendas asignadas.")
            }
            return@Scaffold
        }

        val lista = if (tiendaSeleccionada == null) {
            encuestasPorTienda.values.sortedBy { it.first().tienda }
        } else {
            listOf(encuestasTienda)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (estado.atis.isNotEmpty()) {
                item {
                    ScrollableTabRow(
                        selectedTabIndex = estado.atis.indexOfFirst { it.id == atiSeleccionadoId }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                    ) {
                        estado.atis.forEach { ati ->
                            Tab(
                                selected = ati.id == atiSeleccionadoId,
                                onClick = {
                                    tiendaSeleccionada = null
                                    viewModel.seleccionarAti(ati.id)
                                },
                                text = { Text(ati.nombre_completo) },
                            )
                        }
                    }
                }
            }

            if (tiendaSeleccionada == null) {
                if (lista.isEmpty()) {
                    item { Text("Este ATI todavía no tiene respuestas registradas.") }
                }
                items(lista, key = { it.first().tiendaCodigo }) { encuestas ->
                    val primera = encuestas.first()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tiendaSeleccionada = "${primera.tiendaCodigo}|${primera.tienda}" }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${primera.tiendaCodigo} - ${primera.tienda}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${encuestas.size} encuesta${if (encuestas.size == 1) "" else "s"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                )
                            }
                            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Ver encuestas", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            } else {
                items(encuestasTienda.sortedByDescending { it.fecha }, key = { it.encuestaId }) { enc ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(enc.fecha, style = MaterialTheme.typography.titleMedium)
                            Text("${enc.tiendaCodigo} - ${enc.tienda}", style = MaterialTheme.typography.bodyMedium)

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
}