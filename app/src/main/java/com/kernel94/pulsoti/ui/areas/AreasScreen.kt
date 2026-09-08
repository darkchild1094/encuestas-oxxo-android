package com.kernel94.pulsoti.ui.areas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kernel94.pulsoti.ui.components.EnlaceCompartible
import com.kernel94.pulsoti.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(
    viewModel: AreasViewModel,
    apiBaseUrl: String,
    onAbrirMenu: () -> Unit,
) {
    val estado = viewModel.estado
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    LaunchedEffect(estado.operacionExitosa) {
        if (estado.operacionExitosa) {
            Toast.makeText(context, "Área creada", Toast.LENGTH_SHORT).show()
            mostrarDialogo = false
            viewModel.resetOperacionExitosa()
        }
    }

    // El backend expone la API en ".../nps/api/"; el cuestionario web
    // publico vive en ".../nps/encuesta-oficina" -- mismo truco que ya
    // usan PerfilScreen y NavGraph para armar URLs de sitio a partir de
    // la URL de la API.
    val enlaceOficina = remember(apiBaseUrl) {
        apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/') + "/encuesta-oficina"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Áreas administrativas") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar área")
            }
        }
    ) { padding ->
        LoadingOverlay(mensaje = "Cargando áreas...", mostrar = estado.cargando)

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                EnlaceCompartible(
                    titulo = "Encuesta de oficina (formulario web)",
                    url = enlaceOficina,
                )
            }
            item {
                Text(
                    "Áreas registradas para la encuesta de oficina. Toca el botón + " +
                        "para dar de alta una nueva (RH, Mantenimiento, Asesores...). " +
                        "Editarlas, desactivarlas o eliminarlas se hace desde el panel web.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (!estado.cargando && estado.areas.isEmpty()) {
                item { Text("Todavía no hay áreas registradas.") }
            }
            items(estado.areas) { area ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        area.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        if (mostrarDialogo) {
            NuevaAreaDialog(
                guardando = estado.guardando,
                onDismiss = { mostrarDialogo = false },
                onConfirm = { nombre -> viewModel.agregarArea(nombre) },
            )
        }
    }
}

@Composable
private fun NuevaAreaDialog(
    guardando: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var nombre by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva área") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del área") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (guardando) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(enabled = !guardando, onClick = { onConfirm(nombre) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(enabled = !guardando, onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
