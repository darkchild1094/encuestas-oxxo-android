package com.kernel94.pulsoti.ui.areas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
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
                    "Estas son las áreas registradas para la encuesta de oficina. " +
                        "Para dar de alta una nueva, pide al webmaster que la agregue " +
                        "desde el panel web (Administración → Áreas).",
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
    }
}
