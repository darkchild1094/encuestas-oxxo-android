package com.kernel94.pulsoti.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kernel94.pulsoti.data.remote.dto.PromedioPreguntaDto
import com.kernel94.pulsoti.ui.components.FilaRanking
import com.kernel94.pulsoti.ui.components.KpiChip
import com.kernel94.pulsoti.ui.components.LoadingOverlay
import com.kernel94.pulsoti.ui.components.SeccionCompacta
import com.kernel94.pulsoti.ui.components.colorParaPromedio
import java.text.SimpleDateFormat
import java.util.*

private enum class CampoFecha { DESDE, HASTA }

private fun fechaDesdeMillis(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date(millis))

// Dashboard de ATI: una sola pantalla compacta, sin pestañas. Cada
// seccion es una fila delgada por resultado (FilaRanking), no una
// tarjeta grande -- cabe mucho mas en la misma pantalla.
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
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        KpiChip(
                            modifier = Modifier.weight(1.2f),
                            icono = Icons.Filled.TrendingUp,
                            color = colorParaPromedio(state.promedioGeneral),
                            valor = String.format(Locale.getDefault(), "%.1f", state.promedioGeneral),
                            etiqueta = "Promedio",
                        )
                        KpiChip(
                            modifier = Modifier.weight(1f),
                            icono = Icons.Filled.Store,
                            color = Color(0xFF2E86AB),
                            valor = state.tiendasPlaza.size.toString(),
                            etiqueta = "Tiendas",
                        )
                        KpiChip(
                            modifier = Modifier.weight(1f),
                            icono = Icons.Filled.Groups,
                            color = Color(0xFF5A5F63),
                            valor = state.encuestasTotales.toString(),
                            etiqueta = "Encuestas",
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AssistChip(
                            onClick = { campoFecha = CampoFecha.DESDE },
                            label = { Text("Desde: ${state.desde ?: "Inicio"}", style = MaterialTheme.typography.labelSmall) },
                        )
                        AssistChip(
                            onClick = { campoFecha = CampoFecha.HASTA },
                            label = { Text("Hasta: ${state.hasta ?: "Hoy"}", style = MaterialTheme.typography.labelSmall) }
                        )
                        if (state.desde != null || state.hasta != null) {
                            TextButton(onClick = viewModel::limpiarFechas, contentPadding = PaddingValues(4.dp)) {
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
                    seccionRanking("ATIs de tu plaza", Icons.Filled.Groups, Color(0xFF2E86AB), state.atisPlaza)
                    seccionRanking("Tiendas de tu plaza", Icons.Filled.Store, Color(0xFFD71921), state.tiendasPlaza)
                    seccionRanking("ATIs de tu región", Icons.Filled.LocationCity, Color(0xFF5A5F63), state.atisRegion)
                    seccionRanking("Desempeño PFS", Icons.Filled.SupportAgent, Color(0xFFB8860B), state.pfsDesempeno)

                    item {
                        Text(
                            "Encuesta de oficina",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    seccionRanking("Oficina · por área", Icons.Filled.Business, Color(0xFF3AAE7A), state.oficinaArea)
                    seccionRanking("Oficina · por ATI", Icons.Filled.Groups, Color(0xFF2E86AB), state.oficinaAti)
                    seccionRanking("Oficina · por plaza", Icons.Filled.LocationCity, Color(0xFF5A5F63), state.oficinaPlaza)

                    item { Spacer(Modifier.height(4.dp)) }
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

private fun LazyListScope.seccionRanking(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    datos: List<PromedioPreguntaDto>,
) {
    item { SeccionCompacta(titulo = titulo, icono = icono, color = color, total = datos.size) }
    if (datos.isEmpty()) {
        item {
            Text(
                "Sin datos para este filtro.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        items(datos, key = { "$titulo-${it.pregunta_id}" }) { item ->
            FilaRanking(texto = item.pregunta_texto, promedio = item.promedio, totalEncuestas = item.total_encuestas)
        }
    }
}
