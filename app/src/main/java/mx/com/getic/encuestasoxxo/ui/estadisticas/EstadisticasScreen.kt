package mx.com.getic.encuestasoxxo.ui.estadisticas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

private enum class CampoFecha { DESDE, HASTA }

private fun fechaDesdeMillis(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel,
    onAbrirMenu: () -> Unit
) {
    val state = viewModel.state
    var campoFecha by remember { mutableStateOf<CampoFecha?>(null) }
    
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.cargar(state.tipoSeleccionado)
        }
    }
    
    LaunchedEffect(state.cargando) {
        if (!state.cargando) {
            // pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas de Desempeño") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Selectores de tipo
                TabRow(selectedTabIndex = state.tipoSeleccionado.ordinal) {
                    TipoEstadistica.entries.forEach { tipo ->
                        Tab(
                            selected = state.tipoSeleccionado == tipo,
                            onClick = { viewModel.cargar(tipo) },
                            text = {
                                Text(
                                    text = when (tipo) {
                                        TipoEstadistica.PFS -> "Por PFS"
                                        TipoEstadistica.REGION_ATI -> "ATI Región"
                                        TipoEstadistica.REGION_PLAZA -> "Plaza Región"
                                    },
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { campoFecha = CampoFecha.DESDE },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Desde: ${state.desde ?: "cualquier fecha"}")
                    }
                    OutlinedButton(
                        onClick = { campoFecha = CampoFecha.HASTA },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Hasta: ${state.hasta ?: "cualquier fecha"}")
                    }
                    if (state.desde != null || state.hasta != null) {
                        TextButton(onClick = viewModel::limpiarFechas) {
                            Text("Limpiar")
                        }
                    }
                }

                if (state.cargando && !pullToRefreshState.isRefreshing) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null && state.datos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.cargar(state.tipoSeleccionado) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = when (state.tipoSeleccionado) {
                                    TipoEstadistica.PFS -> "Promedio de preguntas por PFS en tu plaza"
                                    TipoEstadistica.REGION_ATI -> "Comparativa entre ATIs de la región"
                                    TipoEstadistica.REGION_PLAZA -> "Comparativa entre Plazas de la región"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(state.datos) { item ->
                            GraficaBarraPregunta(item)
                        }
                    }
                }
            }
            
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        if (campoFecha != null) {
            val fechaInicial = when (campoFecha) {
                CampoFecha.DESDE -> state.desde
                CampoFecha.HASTA -> state.hasta
                null -> null
            }?.let { fecha ->
                SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(fecha)?.time
            }
            val selector = rememberDatePickerState(initialSelectedDateMillis = fechaInicial)
            DatePickerDialog(
                onDismissRequest = { campoFecha = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selector.selectedDateMillis?.let { millis ->
                                val fecha = fechaDesdeMillis(millis)
                                if (campoFecha == CampoFecha.DESDE) viewModel.establecerDesde(fecha)
                                else viewModel.establecerHasta(fecha)
                            }
                            campoFecha = null
                        }
                    ) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { campoFecha = null }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = selector)
            }
        }
    }
}

@Composable
fun GraficaBarraPregunta(item: PromedioPreguntaDto) {
    val porcentaje = (item.promedio / 10.0).coerceIn(0.0, 1.0).toFloat()
    val animatedProgress by animateFloatAsState(targetValue = porcentaje, label = "progreso")

    val colorBarra = when {
        item.promedio >= 9.0 -> Color(0xFF4CAF50) // Verde
        item.promedio >= 7.0 -> Color(0xFFFFC107) // Amarillo
        else -> Color(0xFFF44336) // Rojo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.pregunta_texto,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", item.promedio),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorBarra
                )
            }

            // Barra de progreso personalizada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorBarra)
                )
            }
            
            Text(
                text = "${item.total_encuestas} encuestas realizadas",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
