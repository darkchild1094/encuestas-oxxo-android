package mx.com.getic.encuestasoxxo.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.com.getic.encuestasoxxo.data.remote.dto.PromedioPreguntaDto
import mx.com.getic.encuestasoxxo.ui.components.LoadingOverlay
import java.text.SimpleDateFormat
import java.util.*

private enum class CampoFecha { DESDE, HASTA }

private fun fechaDesdeMillis(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAbrirMenu: () -> Unit
) {
    val state = viewModel.state
    var campoFecha by remember { mutableStateOf<CampoFecha?>(null) }
    
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.cargar()
        }
    }
    
    LaunchedEffect(state.cargando) {
        if (!state.cargando) {
            // Sincronización de estado terminada
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Administrativo") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LoadingOverlay(mostrar = state.cargando && !pullToRefreshState.isRefreshing)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Selectores de Categoría
                ScrollableTabRow(
                    selectedTabIndex = state.tipoSeleccionado.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    TipoDashboard.entries.forEach { tipo ->
                        Tab(
                            selected = state.tipoSeleccionado == tipo,
                            onClick = { viewModel.cargar(tipo) },
                            text = {
                                Text(
                                    text = when (tipo) {
                                        TipoDashboard.ATI_PLAZA -> "ATIs Plaza"
                                        TipoDashboard.TIENDAS_PLAZA -> "Tiendas Plaza"
                                        TipoDashboard.ATI_REGION -> "ATIs Región"
                                        TipoDashboard.PFS_PERFORMANCE -> "Desempeño PFS"
                                    },
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }

                // Filtros de Fecha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AssistChip(
                        onClick = { campoFecha = CampoFecha.DESDE },
                        label = { Text("Desde: ${state.desde ?: "Inicio"}") },
                        leadingIcon = { Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { campoFecha = CampoFecha.HASTA },
                        label = { Text("Hasta: ${state.hasta ?: "Hoy"}") }
                    )
                    if (state.desde != null || state.hasta != null) {
                        TextButton(onClick = viewModel::limpiarFechas) {
                            Text("Limpiar", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (state.error != null && state.datos.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.cargar() }) {
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
                                    TipoDashboard.ATI_PLAZA -> "Ranking de Asesores TI en Plaza"
                                    TipoDashboard.TIENDAS_PLAZA -> "Desempeño por Tienda en Plaza"
                                    TipoDashboard.ATI_REGION -> "Comparativa de Asesores en la Región"
                                    TipoDashboard.PFS_PERFORMANCE -> "Calificación PFS (Pregunta principal)"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        items(state.datos) { item ->
                            DashboardCard(item)
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
fun DashboardCard(item: PromedioPreguntaDto) {
    val colorBase = when {
        item.promedio >= 9.0 -> Color(0xFF4CAF50)
        item.promedio >= 7.0 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gráfica Circular de Progreso
            CircularScore(score = item.promedio, color = colorBase)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.pregunta_texto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.total_encuestas} evaluaciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = { (item.promedio / 10f).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = colorBase,
                    trackColor = colorBase.copy(alpha = 0.1f)
                )
            }
            
            Text(
                text = String.format(Locale.getDefault(), "%.1f", item.promedio),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = colorBase
            )
        }
    }
}

@Composable
fun CircularScore(score: Double, color: Color) {
    val sweepAngle by animateFloatAsState(
        targetValue = (score / 10f * 360f).toFloat(),
        label = "sweep"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(60.dp)
    ) {
        Canvas(modifier = Modifier.size(54.dp)) {
            // Fondo
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
            // Progreso
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(score * 10).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}
