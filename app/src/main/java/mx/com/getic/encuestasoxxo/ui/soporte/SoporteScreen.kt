package mx.com.getic.encuestasoxxo.ui.soporte

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.TicketSoporteDto
import mx.com.getic.encuestasoxxo.ui.components.LoadingOverlay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(
    viewModel: SoporteViewModel,
    sesion: Sesion,
    onAbrirMenu: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    var mostrarDialogoCrear by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = copyUriToFile(context, it)
            viewModel.onArchivoChange(file)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (sesion.rol == "WEBMASTER") "Tickets de Soporte" else "Reportar Problema") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) { Icon(Icons.Default.Menu, null) }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargar() }) { Icon(Icons.Default.Refresh, null) }
                }
            )
        },
        floatingActionButton = {
            if (sesion.rol != "WEBMASTER") {
                FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                    Icon(Icons.Default.Add, "Nuevo Reporte")
                }
            }
        }
    ) { padding ->
        LoadingOverlay(mostrar = state.cargando)

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.tickets.isEmpty() && !state.cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay reportes registrados.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.tickets) { ticket ->
                        TicketItem(ticket, onClick = { onVerDetalle(ticket.id) })
                    }
                }
            }
        }

        if (mostrarDialogoCrear) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCrear = false },
                title = { Text("Nuevo Reporte") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.asunto,
                            onValueChange = viewModel::onAsuntoChange,
                            label = { Text("Asunto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = state.descripcion,
                            onValueChange = viewModel::onDescripcionChange,
                            label = { Text("Descripción detallada") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { filePicker.launch("image/*") }) {
                                Icon(Icons.Default.AttachFile, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Adjuntar Evidencia")
                            }
                            if (state.archivoEvidencia != null) {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        if (state.error != null) {
                            Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            viewModel.crearTicket { 
                                mostrarDialogoCrear = false 
                            } 
                        },
                        enabled = !state.guardando
                    ) {
                        if (state.guardando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        else Text("Enviar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCrear = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun TicketItem(ticket: TicketSoporteDto, onClick: () -> Unit) {
    val colorEstatus = when(ticket.estatus) {
        "RESUELTO" -> Color(0xFF4CAF50)
        "ABIERTO" -> Color(0xFF2196F3)
        "EN_PROCESO" -> Color(0xFFFF9800)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Folio #${ticket.id}", style = MaterialTheme.typography.labelSmall)
                Text(ticket.estatus, color = colorEstatus, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            Text(ticket.asunto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(ticket.descripcion, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(ticket.fecha_creacion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun copyUriToFile(context: android.content.Context, uri: Uri): File {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor?.moveToFirst()
    val name = cursor?.getString(nameIndex ?: 0) ?: "evidencia.jpg"
    cursor?.close()

    val file = File(context.cacheDir, name)
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}
