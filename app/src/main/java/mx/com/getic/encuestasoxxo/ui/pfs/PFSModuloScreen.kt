package mx.com.getic.encuestasoxxo.ui.pfs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaPFSDto

@Composable
fun PFSModuloScreen(
    viewModel: PFSModuloViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDetalles by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        viewModel.cargarEncuestasPendientes()
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HeaderPFS(
            totalEncuestas = uiState.totalEncuestas,
            tiendaId = uiState.tiendaId,
            onRefresh = { viewModel.cargarEncuestasPendientes() }
        )
        
        when {
            uiState.cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorBox(
                    mensaje = uiState.error ?: "Error desconocido",
                    onRetry = { viewModel.cargarEncuestasPendientes() }
                )
            }
            uiState.encuestas.isEmpty() -> {
                EmptyStateBox()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.encuestas) { encuesta ->
                        EncuestaCard(
                            encuesta = encuesta,
                            onVerDetalles = { mostrarDetalles = encuesta.id },
                            onReintentar = { viewModel.reintentar(encuesta.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (mostrarDetalles != null) {
        val encuesta = uiState.encuestas.find { it.id == mostrarDetalles }
        if (encuesta != null) {
            DetallesErrorDialog(
                encuesta = encuesta,
                onDismiss = { mostrarDetalles = null }
            )
        }
    }
}

@Composable
private fun HeaderPFS(
    totalEncuestas: Int,
    tiendaId: Int?,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1976D2))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Estado de Encuestas",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                if (tiendaId != null) {
                    Text(
                        text = "Tienda #$tiendaId",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }
            
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Actualizar",
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("Total", totalEncuestas.toString(), Color.White)
        }
    }
}

@Composable
private fun StatItem(label: String, valor: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = color.copy(alpha = 0.8f), fontSize = 12.sp)
        Text(
            text = valor,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EncuestaCard(
    encuesta: EncuestaPFSDto,
    onVerDetalles: () -> Unit,
    onReintentar: () -> Unit
) {
    val estadoColor = when (encuesta.estado) {
        "exito" -> Color(0xFF4CAF50)
        "error" -> Color(0xFFF44336)
        "enviando" -> Color(0xFFFFC107)
        else -> Color(0xFF9E9E9E)
    }
    
    val estadoTexto = when (encuesta.estado) {
        "exito" -> "✓ Enviada"
        "error" -> "✗ Error"
        "enviando" -> "↻ Enviando"
        "pendiente" -> "○ Pendiente"
        else -> encuesta.estado ?: "Desconocido"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = encuesta.estado == "error") { onVerDetalles() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (encuesta.folio != null) {
                        Text(
                            text = "Folio: ${encuesta.folio}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = encuesta.id.take(8),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Badge(
                    modifier = Modifier.padding(start = 8.dp),
                    containerColor = estadoColor,
                    contentColor = Color.White
                ) {
                    Text(
                        text = estadoTexto,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Respuestas: ${encuesta.total_respuestas}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Text(
                text = "Fecha: ${encuesta.fecha_creacion_local}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            if (encuesta.comentario != null && encuesta.comentario.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comentario: ${encuesta.comentario}",
                    fontSize = 13.sp,
                    color = Color(0xFF555555),
                    maxLines = 2
                )
            }
            
            if (encuesta.intento_numero != null && encuesta.intento_numero > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Intentos: ${encuesta.intento_numero}",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
            
            if (encuesta.estado == "error") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onVerDetalles,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Detalles")
                    }
                    
                    Button(
                        onClick = onReintentar,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetallesErrorDialog(
    encuesta: EncuestaPFSDto,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Detalles del Error")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow("Folio:", encuesta.folio ?: "N/A")
                InfoRow("ID:", encuesta.id.take(12) + "...")
                InfoRow("Estado:", encuesta.estado ?: "Desconocido")
                InfoRow("Intentos:", encuesta.intento_numero?.toString() ?: "N/A")
                
                if (encuesta.mensaje_error != null) {
                    Divider()
                    Text(
                        text = "Mensaje de Error:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = encuesta.mensaje_error,
                        fontSize = 12.sp,
                        color = Color(0xFFF44336),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFFDE7),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(8.dp)
                    )
                }
                
                if (encuesta.fecha_intento != null) {
                    Divider()
                    InfoRow("Último intento:", encuesta.fecha_intento)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = value, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun ErrorBox(mensaje: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                tint = Color(0xFFF44336),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = mensaje,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun EmptyStateBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Todas las encuestas enviadas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "No hay encuestas pendientes",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
