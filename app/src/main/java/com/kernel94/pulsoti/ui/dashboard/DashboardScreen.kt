package com.kernel94.pulsoti.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kernel94.pulsoti.data.remote.dto.PromedioPreguntaDto
import com.kernel94.pulsoti.ui.components.LoadingOverlay
import java.text.SimpleDateFormat
import java.util.*

private enum class CampoFecha { DESDE, HASTA }

private fun fechaDesdeMillis(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

private fun colorParaPromedio(promedio: Double): Color = when {
    promedio >= 9.0 -> Color(0xFF4CAF50)
    promedio >= 7.0 -> Color(0xFFFFC107)
    else -> Color(0xFFF44336)
}

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
        LaunchedEffect(true) { viewModel.cargar() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    KpiHeader(
                        promedioGeneral = state.promedioGeneral,
                        encuestasTotales = state.encuestasTotales,
                        tiendasEvaluadas = state.tiendasPlaza.size,
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AssistChip(
                            onClick = { campoFecha = CampoFecha.DESDE },
                            label = { Text("Desde: ${state.desde ?: "Inicio"}") },
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
                }

                if (state.error != null && !state.huboDatos) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { viewModel.cargar() }) { Text("Reintentar") }
                            }
                        }
                    }
                } else {
                    seccionRanking(
                        titulo = "ATIs de tu plaza",
                        subtitulo = "Ranking de asesores TI en tu plaza",
                        icono = Icons.Filled.Groups,
                        color = Color(0xFF2E86AB),
                        datos = state.atisPlaza,
                    )
                    seccionRanking(
                        titulo = "Tiendas de tu plaza",
                        subtitulo = "Desempeño por tienda",
                        icono = Icons.Filled.Store,
                        color = Color(0xFFD71921),
                        datos = state.tiendasPlaza,
                    )
                    seccionRanking(
                        titulo = "ATIs de tu región",
                        subtitulo = "Comparativa entre plazas de la región",
                        icono = Icons.Filled.LocationCity,
                        color = Color(0xFF5A5F63),
                        datos = state.atisRegion,
                    )
                    seccionRanking(
                        titulo = "Desempeño PFS",
                        subtitulo = "Calificación de la pregunta principal por PFS",
                        icono = Icons.Filled.SupportAgent,
                        color = Color(0xFFFFC72C),
                        datos = state.pfsDesempeno,
                    )
                    seccionRanking(
                        titulo = "Encuesta de oficina",
                        subtitulo = "Promedio por área administrativa",
                        icono = Icons.Filled.Business,
                        color = Color(0xFF6BAA75),
                        datos = state.oficina,
                    )

                    item { Spacer(Modifier.height(8.dp)) }
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
                    TextButton(onClick = {
                        selector.selectedDateMillis?.let { millis ->
                            val fecha = fechaDesdeMillis(millis)
                            if (campoFecha == CampoFecha.DESDE) viewModel.establecerDesde(fecha)
                            else viewModel.establecerHasta(fecha)
                        }
                        campoFecha = null
                    }) { Text("Aceptar") }
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

// --- KPIs (encabezado) ---------------------------------------------

@Composable
private fun KpiHeader(promedioGeneral: Double, encuestasTotales: Int, tiendasEvaluadas: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KpiCard(
            modifier = Modifier.weight(1.2f),
            icono = Icons.Filled.TrendingUp,
            color = colorParaPromedio(promedioGeneral),
            valor = String.format(Locale.getDefault(), "%.1f", promedioGeneral),
            etiqueta = "Promedio general",
        )
        KpiCard(
            modifier = Modifier.weight(1f),
            icono = Icons.Filled.Store,
            color = Color(0xFF2E86AB),
            valor = tiendasEvaluadas.toString(),
            etiqueta = "Tiendas",
        )
        KpiCard(
            modifier = Modifier.weight(1f),
            icono = Icons.Filled.Groups,
            color = Color(0xFF5A5F63),
            valor = encuestasTotales.toString(),
            etiqueta = "Encuestas",
        )
    }
}

@Composable
private fun KpiCard(
    icono: ImageVector,
    color: Color,
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// --- Secciones de ranking --------------------------------------------

private fun LazyListScope.seccionRanking(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    color: Color,
    datos: List<PromedioPreguntaDto>,
) {
    item {
        SeccionHeader(titulo = titulo, subtitulo = subtitulo, icono = icono, color = color, total = datos.size)
    }
    if (datos.isEmpty()) {
        item {
            Text(
                "Sin datos para este filtro.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    } else {
        items(datos, key = { "$titulo-${it.pregunta_id}" }) { item -> DashboardCard(item) }
    }
}

@Composable
private fun SeccionHeader(titulo: String, subtitulo: String, icono: ImageVector, color: Color, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (total > 0) {
            Text("$total", style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardCard(item: PromedioPreguntaDto) {
    val colorBase = colorParaPromedio(item.promedio)

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
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
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
