package com.kernel94.pulsoti.ui.resumen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kernel94.pulsoti.data.remote.dto.ConteosResumenDto
import com.kernel94.pulsoti.data.remote.dto.EncuestaRecienteDto
import com.kernel94.pulsoti.data.remote.dto.ResumenSistemaDto
import com.kernel94.pulsoti.data.remote.dto.UsuariosPorRolDto
import com.kernel94.pulsoti.ui.components.KpiChip
import com.kernel94.pulsoti.ui.components.LoadingOverlay
import com.kernel94.pulsoti.ui.components.SeccionCompacta

// "Resumen del sistema" para WEBMASTER: version compacta (una fila
// delgada por dato), pensada para caber sin scroll interminable.
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
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item { ConteosGrid(datos.conteos, datos.tokens_activos) }
                    item { EncuestasResumenLinea(datos.conteos) }

                    item { SeccionCompacta("Usuarios por rol", Icons.Filled.People, Color(0xFF2E86AB), 0) }
                    items(datos.usuarios_por_rol, key = { it.rol }) { fila ->
                        UsuariosPorRolRow(fila, maxTotal = datos.usuarios_por_rol.maxOfOrNull { it.total } ?: 1)
                    }

                    item { SeccionCompacta("Actividad reciente", Icons.Filled.History, Color(0xFF5A5F63), 0) }
                    if (datos.ultimas_encuestas.isEmpty()) {
                        item {
                            Text(
                                "Todavía no hay encuestas registradas.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(datos.ultimas_encuestas, key = { it.fecha + it.lugar }) { fila -> ActividadRow(fila) }
                    }

                    item { VersionAppLinea(datos) }
                    item { Spacer(Modifier.height(4.dp)) }
                }
            }

            PullToRefreshContainer(state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun ConteosGrid(conteos: ConteosResumenDto, tokensActivos: Int?) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            KpiChip(Icons.Filled.People, Color(0xFF2E86AB), conteos.usuarios.toString(), "Usuarios", Modifier.weight(1f))
            KpiChip(Icons.Filled.Store, Color(0xFFD71921), conteos.tiendas.toString(), "Tiendas", Modifier.weight(1f))
            KpiChip(Icons.Filled.LocationCity, Color(0xFFB8860B), conteos.plazas.toString(), "Plazas", Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            KpiChip(Icons.Filled.Business, Color(0xFF3AAE7A), conteos.areas.toString(), "Áreas", Modifier.weight(1f))
            KpiChip(Icons.Filled.Key, Color(0xFF7B5EA7), tokensActivos?.toString() ?: "n/d", "Sesiones", Modifier.weight(1f))
            KpiChip(Icons.Filled.LockReset, Color(0xFFC66A2E), conteos.pendientes_password.toString(), "Pass. pend.", Modifier.weight(1f))
        }
    }
}

@Composable
private fun EncuestasResumenLinea(conteos: ConteosResumenDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DatoLinea("Total", conteos.encuestas.toString())
        DatoLinea("Tienda", conteos.encuestas_tienda.toString())
        DatoLinea("Oficina", conteos.encuestas_oficina.toString())
        DatoLinea("7 días", conteos.encuestas_7d.toString())
        DatoLinea("30 días", conteos.encuestas_30d.toString())
    }
}

@Composable
private fun DatoLinea(etiqueta: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun UsuariosPorRolRow(fila: UsuariosPorRolDto, maxTotal: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(fila.rol, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(96.dp))
        LinearProgressIndicator(
            progress = { if (maxTotal == 0) 0f else fila.total.toFloat() / maxTotal },
            modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
        )
        Text("${fila.total}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActividadRow(fila: EncuestaRecienteDto) {
    val esOficina = fila.tipo == "oficina"
    val color = if (esOficina) Color(0xFF3AAE7A) else Color(0xFFD71921)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            if (esOficina) Icons.Filled.Business else Icons.Filled.Store,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp),
        )
        Text(
            fila.lugar,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            fila.fecha.substringBefore(" "),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun VersionAppLinea(datos: ResumenSistemaDto) {
    val v = datos.version_app
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Filled.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
        if (v?.version_name != null) {
            Text(
                "App publicada: v${v.version_name} (code ${v.version_code ?: "?"})" +
                    if (v.obligatoria == true) " · obligatoria" else "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text("Sin información de versión publicada.", style = MaterialTheme.typography.labelSmall)
        }
    }
}
