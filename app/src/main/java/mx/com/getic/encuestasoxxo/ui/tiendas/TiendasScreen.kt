package mx.com.getic.encuestasoxxo.ui.tiendas

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.AtiDto
import mx.com.getic.encuestasoxxo.data.remote.dto.TiendaDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendasScreen(
    viewModel: TiendasViewModel,
    sesion: Sesion,
    onAbrirMenu: () -> Unit
) {
    var tiendaDetalle by remember { mutableStateOf<TiendaDto?>(null) }
    val context = LocalContext.current
    
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refrescar()
        }
    }
    
    LaunchedEffect(viewModel.cargando) {
        if (!viewModel.cargando) {
            // pullToRefreshState.endRefresh()
        }
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Tiendas") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) { Icon(Icons.Default.Menu, null) }
                }
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Selector de Plaza (solo para Webmaster)
                if (sesion.rol == "WEBMASTER") {
                    var expandidoPlaza by remember { mutableStateOf(false) }
                    val plazaSeleccionada = viewModel.plazas.find { it.id == viewModel.plazaSeleccionadaId }

                    ExposedDropdownMenuBox(
                        expanded = expandidoPlaza,
                        onExpandedChange = { expandidoPlaza = it }
                    ) {
                        OutlinedTextField(
                            value = plazaSeleccionada?.nombre ?: "Seleccionar Plaza",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Plaza") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoPlaza) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandidoPlaza,
                            onDismissRequest = { expandidoPlaza = false }
                        ) {
                            viewModel.plazas.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.nombre) },
                                    onClick = {
                                        viewModel.cargarTiendas(p.id)
                                        expandidoPlaza = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Buscador
                OutlinedTextField(
                    value = viewModel.query,
                    onValueChange = { viewModel.query = it },
                    label = { Text("Buscar por CR o nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = if (viewModel.query.isNotEmpty()) {
                        { IconButton(onClick = { viewModel.query = "" }) { Icon(Icons.Default.Clear, null) } }
                    } else null
                )

                Spacer(Modifier.height(16.dp))

                if (viewModel.cargando && !pullToRefreshState.isRefreshing) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (viewModel.error != null) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    val tiendas = viewModel.tiendasFiltradas
                    if (tiendas.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron tiendas")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(tiendas) { tienda ->
                                ListItem(
                                    headlineContent = { Text(tienda.nombre, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text("CR: ${tienda.codigo}") },
                                    leadingContent = { Icon(Icons.Default.Store, null) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                        .let { if (tiendaDetalle == null) it.clickable { tiendaDetalle = tienda } else it }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
            
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        if (tiendaDetalle != null) {
            TiendaEdicionDialog(
                tienda = tiendaDetalle!!,
                atisPlaza = viewModel.atisPlaza,
                guardando = viewModel.guardando,
                onGuardar = { editada ->
                    viewModel.actualizarTienda(editada) {
                        tiendaDetalle = null
                        Toast.makeText(context, "Tienda actualizada", Toast.LENGTH_SHORT).show()
                    }
                },
                onDismiss = { tiendaDetalle = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaEdicionDialog(
    tienda: TiendaDto,
    atisPlaza: List<AtiDto>,
    guardando: Boolean,
    onGuardar: (TiendaDto) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(tienda.nombre) }
    var codigo by remember { mutableStateOf(tienda.codigo) }
    var direccion by remember { mutableStateOf(tienda.direccion ?: "") }
    var latitud by remember { mutableStateOf(tienda.latitud?.toString() ?: "") }
    var longitud by remember { mutableStateOf(tienda.longitud?.toString() ?: "") }
    var atiId by remember { mutableStateOf(tienda.ati_usuario_id) }
    
    var expandidoAti by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tienda") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nombre de Tienda") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )
                OutlinedTextField(
                    value = codigo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Código (CR)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latitud,
                        onValueChange = { latitud = it },
                        label = { Text("Latitud") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitud,
                        onValueChange = { longitud = it },
                        label = { Text("Longitud") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Selector de ATI
                ExposedDropdownMenuBox(
                    expanded = expandidoAti,
                    onExpandedChange = { expandidoAti = it }
                ) {
                    val atiActual = atisPlaza.find { it.id == atiId }
                    OutlinedTextField(
                        value = atiActual?.nombre_completo ?: "Sin ATI asignado",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Asesor TI") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoAti) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandidoAti,
                        onDismissRequest = { expandidoAti = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguno") },
                            onClick = { atiId = null; expandidoAti = false }
                        )
                        atisPlaza.forEach { ati ->
                            DropdownMenuItem(
                                text = { Text(ati.nombre_completo) },
                                onClick = {
                                    atiId = ati.id
                                    expandidoAti = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(tienda.copy(
                        nombre = nombre,
                        codigo = codigo,
                        direccion = direccion,
                        latitud = latitud.toDoubleOrNull(),
                        longitud = longitud.toDoubleOrNull(),
                        ati_usuario_id = atiId
                    ))
                },
                enabled = !guardando && nombre.isNotBlank() && codigo.isNotBlank()
            ) {
                if (guardando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !guardando) { Text("Cancelar") }
        }
    )
}
