package mx.com.getic.encuestasoxxo.ui.encuesta

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.com.getic.encuestasoxxo.data.Sesion
import mx.com.getic.encuestasoxxo.data.remote.dto.TiendaDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncuestaScreen(
    viewModel: EncuestaViewModel,
    sesion: Sesion,
    apiBaseUrl: String,
    onAbrirMenu: () -> Unit,
) {
    val estado = viewModel.estado
    val context = LocalContext.current

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    LaunchedEffect(estado.enviadoOk) {
        if (estado.enviadoOk) {
            Toast.makeText(context, "Encuesta guardada. Gracias.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encuesta de satisfaccion") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item { EncabezadoAtencion(sesion, apiBaseUrl) }

            item {
                if (estado.plazaFija) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Plaza: ${sesion.plazaNombre ?: ""}",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        BuscadorTienda(
                            tiendas = estado.tiendas,
                            seleccionId = estado.tiendaId,
                            onSeleccionar = viewModel::onTiendaSeleccionada,
                        )
                    }
                } else {
                    SelectorUnidad(
                        estado = estado,
                        onNegocio = viewModel::onNegocioSeleccionado,
                        onRegion = viewModel::onRegionSeleccionada,
                        onPlaza = viewModel::onPlazaSeleccionada,
                        onTienda = viewModel::onTiendaSeleccionada,
                    )
                }
            }

            if (estado.cargandoPreguntas) {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(estado.preguntas, key = { it.id }) { pregunta ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)
                    NpsFaceSelector(
                        seleccion = estado.calificaciones[pregunta.id],
                        onSeleccionar = { viewModel.onCalificar(pregunta.id, it) },
                    )
                }
            }

            if (estado.preguntas.isNotEmpty()) {
                item {
                    OutlinedTextField(
                        value = estado.comentario,
                        onValueChange = viewModel::onComentarioChange,
                        label = { Text("Comentarios (opcional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Button(
                        onClick = { viewModel.enviar(context) { } },
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
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun EncabezadoAtencion(sesion: Sesion, apiBaseUrl: String) {
    // Muestra al tecnico logueado (PFS o webmaster): la tienda esta
    // calificando el servicio que EL dio, por eso es su foto/nombre,
    // no el de quien esta contestando.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val fotoUrl = sesion.fotoPerfil?.let { apiBaseUrl.trimEnd('/').removeSuffix("/api") + "/" + it }
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(CircleShape),
            )
        } else {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }
        Column {
            Text("Te atendio", style = MaterialTheme.typography.labelMedium)
            Text(
                sesion.nombreCompleto.ifBlank { sesion.correo },
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuscadorTienda(
    tiendas: List<TiendaDto>,
    seleccionId: Int?,
    onSeleccionar: (Int) -> Unit,
) {
    var query by remember(seleccionId, tiendas) {
        val actual = tiendas.firstOrNull { it.id == seleccionId }
        mutableStateOf(actual?.let { "${it.codigo} - ${it.nombre}" } ?: "")
    }
    var expandido by remember { mutableStateOf(false) }

    val filtradas = remember(query, tiendas) {
        if (query.isBlank()) {
            tiendas
        } else {
            tiendas.filter {
                it.codigo.contains(query, ignoreCase = true) || it.nombre.contains(query, ignoreCase = true)
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expandido && filtradas.isNotEmpty(),
        onExpandedChange = { expandido = it },
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; expandido = true },
            label = { Text("Tienda * (busca por CR o nombre)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expandido && filtradas.isNotEmpty(),
            onDismissRequest = { expandido = false },
        ) {
            // Limite de 50 para no renderizar 200+ items de un jalon
            // mientras el usuario todavia no afina la busqueda.
            filtradas.take(50).forEach { t ->
                DropdownMenuItem(
                    text = { Text("${t.codigo} - ${t.nombre}") },
                    onClick = {
                        onSeleccionar(t.id)
                        query = "${t.codigo} - ${t.nombre}"
                        expandido = false
                    },
                )
            }
            if (filtradas.size > 50) {
                DropdownMenuItem(
                    text = { Text("... y ${filtradas.size - 50} mas, sigue escribiendo", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) },
                    onClick = {},
                    enabled = false,
                )
            }
        }
    }
}

@Composable
private fun SelectorUnidad(
    estado: EncuestaUiState,
    onNegocio: (Int) -> Unit,
    onRegion: (Int) -> Unit,
    onPlaza: (Int) -> Unit,
    onTienda: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DesplegableSimple("Negocio", estado.negocios.map { it.id to it.nombre }, estado.negocioId, onNegocio)
        DesplegableSimple("Region", estado.regiones.map { it.id to it.nombre }, estado.regionId, onRegion)
        DesplegableSimple("Plaza", estado.plazas.map { it.id to it.nombre }, estado.plazaId, onPlaza)
        BuscadorTienda(tiendas = estado.tiendas, seleccionId = estado.tiendaId, onSeleccionar = onTienda)
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
