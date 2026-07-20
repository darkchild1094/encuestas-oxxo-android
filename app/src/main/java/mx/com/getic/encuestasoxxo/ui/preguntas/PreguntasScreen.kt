package mx.com.getic.encuestasoxxo.ui.preguntas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntasScreen(
    viewModel: PreguntasViewModel,
    onAbrirMenu: () -> Unit
) {
    val estado = viewModel.estado
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }
    var preguntaAEditar by remember { mutableStateOf<PreguntaEntity?>(null) }

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    LaunchedEffect(estado.operacionExitosa) {
        if (estado.operacionExitosa) {
            Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
            viewModel.resetOperacionExitosa()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Preguntas") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            if (estado.plazaId != null) {
                FloatingActionButton(onClick = {
                    preguntaAEditar = null
                    mostrarDialogo = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Pregunta")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!estado.plazaFija) {
                item {
                    SelectorUbicacion(
                        estado = estado,
                        onNegocio = viewModel::onNegocioSeleccionado,
                        onRegion = viewModel::onRegionSeleccionada,
                        onPlaza = viewModel::onPlazaSeleccionada
                    )
                }
            } else {
                item {
                    Text(
                        text = "Preguntas de la plaza asignada",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (estado.cargandoPreguntas) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (estado.plazaId != null) {
                if (estado.preguntas.isEmpty()) {
                    item {
                        Text("No hay preguntas en esta plaza.", modifier = Modifier.padding(16.dp))
                    }
                }
                items(estado.preguntas) { pregunta ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pregunta.texto, style = MaterialTheme.typography.bodyLarge)
                                Text("Orden: ${pregunta.orden}", style = MaterialTheme.typography.labelMedium)
                            }
                            IconButton(onClick = {
                                preguntaAEditar = pregunta
                                mostrarDialogo = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = {
                                viewModel.eliminarPregunta(pregunta.id)
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Selecciona una plaza para ver sus preguntas.", modifier = Modifier.padding(16.dp))
                }
            }
        }

        if (mostrarDialogo) {
            PreguntaDialog(
                pregunta = preguntaAEditar,
                onDismiss = { mostrarDialogo = false },
                onConfirm = { texto, orden ->
                    if (preguntaAEditar == null) {
                        viewModel.agregarPregunta(texto, orden)
                    } else {
                        viewModel.editarPregunta(preguntaAEditar!!.id, texto, orden)
                    }
                    mostrarDialogo = false
                }
            )
        }
    }
}

@Composable
fun PreguntaDialog(
    pregunta: PreguntaEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var texto by remember { mutableStateOf(pregunta?.texto ?: "") }
    var orden by remember { mutableStateOf(pregunta?.orden?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (pregunta == null) "Nueva Pregunta" else "Editar Pregunta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Texto de la pregunta") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = orden,
                    onValueChange = { orden = it },
                    label = { Text("Orden") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(texto, orden.toIntOrNull() ?: 0) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun SelectorUbicacion(
    estado: PreguntasUiState,
    onNegocio: (Int) -> Unit,
    onRegion: (Int) -> Unit,
    onPlaza: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DesplegableSimple("Negocio", estado.negocios.map { it.id to it.nombre }, estado.negocioId, onNegocio)
        DesplegableSimple("Region", estado.regiones.map { it.id to it.nombre }, estado.regionId, onRegion)
        DesplegableSimple("Plaza", estado.plazas.map { it.id to it.nombre }, estado.plazaId, onPlaza)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DesplegableSimple(
    etiqueta: String,
    opciones: List<Pair<Int, String>>,
    seleccionId: Int?,
    onSeleccion: (Int) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }
    val textoSeleccion = opciones.firstOrNull { it.first == seleccionId }?.second ?: ""

    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = textoSeleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = { onSeleccion(id); expandido = false },
                )
            }
        }
    }
}
