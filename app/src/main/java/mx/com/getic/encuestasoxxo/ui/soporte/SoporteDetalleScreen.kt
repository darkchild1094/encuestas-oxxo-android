package mx.com.getic.encuestasoxxo.ui.soporte

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.com.getic.encuestasoxxo.BuildConfig
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.MensajeSoporteDto
import mx.com.getic.encuestasoxxo.ui.components.LoadingOverlay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteDetalleScreen(
    viewModel: SoporteDetalleViewModel,
    sesion: Sesion,
    apiBaseUrl: String,
    onBack: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    var mostrarDialogoResolver by remember { mutableStateOf(false) }

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
                title = { Text("Ticket #${state.ticket?.id ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    if (sesion.rol == "WEBMASTER" && state.ticket?.estatus != "RESUELTO") {
                        Button(onClick = { mostrarDialogoResolver = true }, modifier = Modifier.padding(end = 8.dp)) {
                            Text("Resolver")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LoadingOverlay(mostrar = state.cargando)

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            state.ticket?.let { ticket ->
                // Encabezado del ticket
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(ticket.asunto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(ticket.descripcion, style = MaterialTheme.typography.bodyMedium)
                        if (!ticket.notas_cierre.isNullOrBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text("Notas de cierre:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            Text(ticket.notas_cierre, color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Chat
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.mensajes) { msg ->
                        ChatBubble(msg, sesion, apiBaseUrl)
                    }
                }

                // Input de mensaje
                if (ticket.estatus != "RESUELTO" || sesion.rol == "WEBMASTER") {
                    Surface(tonalElevation = 8.dp) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = state.nuevoMensaje,
                                    onValueChange = viewModel::onMensajeChange,
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Escribe un comentario...") },
                                    maxLines = 3
                                )
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { filePicker.launch("image/*") }) {
                                    Icon(Icons.Default.AttachFile, null, tint = if (state.archivoEvidencia != null) Color.Green else MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { viewModel.enviarComentario() }, enabled = !state.enviandoMensaje) {
                                    Icon(Icons.Default.Send, null)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mostrarDialogoResolver) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoResolver = false },
                title = { Text("Cerrar Ticket") },
                text = {
                    Column {
                        Text("Ingresa las notas de resolución:")
                        OutlinedTextField(
                            value = state.notasResolucion,
                            onValueChange = viewModel::onNotasResolucionChange,
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.resolverTicket(); mostrarDialogoResolver = false }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoResolver = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(msg: MensajeSoporteDto, sesion: Sesion, apiBaseUrl: String) {
    val soyYo = msg.usuario_id == sesion.usuarioId
    val alineacion = if (soyYo) Alignment.End else Alignment.Start
    val color = if (soyYo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alineacion) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                if (!soyYo) {
                    Text(msg.usuario_nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
                Text(msg.mensaje, style = MaterialTheme.typography.bodyMedium)
                
                if (!msg.evidencia_ruta.isNullOrBlank()) {
                    val url = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/') + "/" + msg.evidencia_ruta
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = url,
                        contentDescription = "Evidencia",
                        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Text(msg.fecha, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.End), color = MaterialTheme.colorScheme.outline)
            }
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
