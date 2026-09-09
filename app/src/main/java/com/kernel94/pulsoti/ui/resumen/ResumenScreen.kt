package com.kernel94.pulsoti.ui.resumen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kernel94.pulsoti.data.remote.dto.ConteosResumenDto
import com.kernel94.pulsoti.data.remote.dto.EncuestaRecienteDto
import com.kernel94.pulsoti.data.remote.dto.ResumenSistemaDto
import com.kernel94.pulsoti.data.remote.dto.UsuariosPorRolDto
import com.kernel94.pulsoti.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    viewModel: ResumenViewModel,
    onAbrirMenu: () -> Unit,
) {
    val state = viewModel.state
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) { viewModel.cargar() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen del sistema") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) { Icon(Icons.Filled.Menu, contentDescription = "Menu") }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refrescar")
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
            modifier = Modifier.fillMaxSize().padding(padding).nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            val datos = state.datos
            if (datos == null) {
                if (state.error != null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.cargar() }) { Text("Reintentar") }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item { ConteosGrid(datos.conteos, datos.tokens_activos) }
                    item { EncuestasResumenCard(datos.conteos) }

                    item {
                        SeccionHeader("Usuarios por rol", Icons.Filled.People, Color(0xFF2E86AB))
                    }
                    items(datos.usuarios_por_rol, key = { it.rol }) { fila ->
                        UsuariosPorRolRow(fila, maxTotal = datos.usuarios_por_rol.maxOfOrNull { it.total } ?: 1)
                    }

                    item {
                        SeccionHeader("Actividad reciente", Icons.Filled.History, Color(0xFF5A5F63))
                    }
                    if (datos.ultimas_encuestas.isEmpty()) {
                        item {
                            Text(
                                "Todavía no hay encuestas registradas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(datos.ultimas_encuestas, key = { it.fecha + it.lugar }) { fila -> ActividadRow(fila) }
                    }

                    item {
                        VersionAppCard(datos)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }

            PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun ConteosGrid(conteos: ConteosResumenDto, tokensActivos: Int?) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KpiTile(Modifier.weight(1f), Icons.Filled.People, Color(0xFF2E86AB), conteos.usuarios.toString(), "Usuarios")
            KpiTile(Modifier.weight(1f), Icons.Filled.Store, Color(0xFFD71921), conteos.tiendas.toString(), "Tiendas")
            KpiTile(Modifier.weight(1f), Icons.Filled.LocationCity, Color(0xFFFFC72C), conteos.plazas.toString(), "Plazas")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            KpiTile(Modifier.weight(1f), Icons.Filled.Business, Color(0xFF6BAA75), conteos.areas.toString(), "Áreas")
            KpiTile(Modifier.weight(1f), Icons.Filled.Key, Color(0xFF9C27B0), tokensActivos?.toString() ?: "n/d", "Sesiones activas")
            KpiTile(Modifier.weight(1f), Icons.Filled.LockReset, Color(0xFFFF7043), conteos.pendientes_password.toString(), "Pass. pendiente")
        }
    }
}

@Composable
private fun KpiTile(modifier: Modifier, icono: ImageVector, color: Color, valor: String, etiqueta: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color)
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun EncuestasResumenCard(conteos: ConteosResumenDto) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text("Encuestas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DatoLinea("Total", conteos.encuestas.toString())
                DatoLinea("Tienda", conteos.encuestas_tienda.toString())
                DatoLinea("Oficina", conteos.encuestas_oficina.toString())
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DatoLinea("Últimos 7 días", conteos.encuestas_7d.toString())
                DatoLinea("Últimos 30 días", conteos.encuestas_30d.toString())
            }
        }
    }
}

@Composable
private fun DatoLinea(etiqueta: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SeccionHeader(titulo: String, icono: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UsuariosPorRolRow(fila: UsuariosPorRolDto, maxTotal: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fila.rol, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text("${fila.total}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { if (maxTotal == 0) 0f else fila.total.toFloat() / maxTotal },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            )
        }
    }
}

@Composable
private fun ActividadRow(fila: EncuestaRecienteDto) {
    val esOficina = fila.tipo == "oficina"
    val color = if (esOficina) Color(0xFF6BAA75) else Color(0xFFD71921)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            if (esOficina) Icons.Filled.Business else Icons.Filled.Store,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(fila.lugar, style = MaterialTheme.typography.bodyMedium)
            Text(fila.fecha, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        AssistChip(onClick = {}, enabled = false, label = { Text(if (esOficina) "Oficina" else "Tienda") })
    }
}

@Composable
private fun VersionAppCard(datos: ResumenSistemaDto) {
    val v = datos.version_app
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text("App publicada", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            if (v?.version_name != null) {
                Text("Versión ${v.version_name} (code ${v.version_code ?: "?"})", style = MaterialTheme.typography.bodyMedium)
                if (v.obligatoria == true) {
                    AssistChip(onClick = {}, enabled = false, label = { Text("Actualización obligatoria") })
                }
                if (!v.novedades.isNullOrBlank()) {
                    Text(v.novedades, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Text("Sin información de versión publicada.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
