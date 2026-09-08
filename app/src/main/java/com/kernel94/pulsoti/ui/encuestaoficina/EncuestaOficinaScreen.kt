package com.kernel94.pulsoti.ui.encuestaoficina

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kernel94.pulsoti.data.Sesion
import com.kernel94.pulsoti.ui.components.LoadingOverlay
import com.kernel94.pulsoti.ui.encuesta.NpsFaceSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncuestaOficinaScreen(
    viewModel: EncuestaOficinaViewModel,
    sesion: Sesion,
    onAbrirMenu: () -> Unit,
) {
    val estado = viewModel.estado
    val context = LocalContext.current

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.enviadoOk) "Encuesta enviada" else "Encuesta de oficina") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { padding ->
        LoadingOverlay(
            mensaje = if (estado.enviando) "Enviando encuesta..." else "Cargando...",
            mostrar = estado.cargando || estado.enviando,
        )

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (estado.enviadoOk) {
                PantallaAgradecimientoOficina(
                    areaNombre = estado.areaSeleccionada?.nombre.orEmpty(),
                    onCerrar = { viewModel.reiniciarParaNuevaEncuesta() },
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Text(
                            text = "Hola ${sesion.nombreCompleto}, selecciona el área que estás encuestando:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    item {
                        SelectorArea(
                            areas = estado.areas,
                            seleccionId = estado.areaId,
                            onSeleccionar = viewModel::onAreaSeleccionada,
                        )
                    }

                    if (estado.areaId != null) {
                        item {
                            Text(
                                text = "Asesor TI: ${sesion.nombreCompleto}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        items(estado.preguntas, key = { it.id }) { pregunta ->
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)
                                NpsFaceSelector(
                                    seleccion = estado.calificaciones[pregunta.id],
                                    onSeleccionar = { viewModel.onCalificar(pregunta.id, it) },
                                )
                            }
                        }

                        if (estado.preguntas.isNotEmpty()) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Comentarios (opcional)", style = MaterialTheme.typography.titleMedium)
                                    OutlinedTextField(
                                        value = estado.comentario,
                                        onValueChange = viewModel::onComentarioChange,
                                        placeholder = { Text("Escriba sus comentarios aquí...") },
                                        minLines = 3,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                    )
                                }
                            }
                            item {
                                Button(
                                    onClick = { viewModel.enviar() },
                                    enabled = !estado.enviando,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    if (estado.enviando) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Enviar")
                                    }
                                }
                            }
                        } else if (!estado.cargando) {
                            item {
                                Text(
                                    "Esta encuesta todavía no tiene preguntas configuradas.",
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorArea(
    areas: List<com.kernel94.pulsoti.data.remote.dto.AdministracionDto>,
    seleccionId: Int?,
    onSeleccionar: (Int) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }
    val textoSeleccion = areas.firstOrNull { it.id == seleccionId }?.nombre ?: ""

    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = textoSeleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text("Área") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            if (areas.isEmpty()) {
                DropdownMenuItem(text = { Text("No hay áreas registradas") }, onClick = {}, enabled = false)
            }
            areas.forEach { area ->
                DropdownMenuItem(
                    text = { Text(area.nombre) },
                    onClick = { onSeleccionar(area.id); expandido = false },
                )
            }
        }
    }
}

@Composable
private fun PantallaAgradecimientoOficina(
    areaNombre: String,
    onCerrar: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "¡Gracias por tu evaluación!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            if (areaNombre.isNotBlank()) {
                Text(text = areaNombre, style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = "Tu respuesta sobre la encuesta de oficina quedó registrada.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onCerrar, modifier = Modifier.fillMaxWidth()) {
                Text("Contestar otra")
            }
        }
    }
}
