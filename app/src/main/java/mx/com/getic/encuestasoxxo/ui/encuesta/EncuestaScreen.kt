package mx.com.getic.encuestasoxxo.ui.encuesta

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import timber.log.Timber

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

    LaunchedEffect(sesion) {
        val baseStaticUrl = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
        // Segun el Logcat, apiBaseUrl ya tiene /nps/, asi que solo agregamos /public/
        val urlFinal = "$baseStaticUrl/public/${sesion.fotoPerfil}"
        Timber.d("COIL_DEBUG: URL DE FOTO GENERADA: $urlFinal")
        Timber.d("COIL_DEBUG: fotoPerfil en sesion: ${sesion.fotoPerfil}")
    }

    LaunchedEffect(estado.error) {
        estado.error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.enviadoOk) "Encuesta Finalizada" else "Encuesta de satisfacción") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
            )
        },
    ) { padding ->
        if (estado.enviadoOk) {
            PantallaAgradecimiento(
                onReiniciar = { viewModel.reiniciarParaNuevaEncuesta() },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item { EncabezadoAtencion(sesion, apiBaseUrl) }

                item {
                    val mostrarSelectores = !estado.plazaFija && sesion.rol != "ATI" && sesion.rol != "WEBMASTER"
                    
                    if (estado.plazaFija || !mostrarSelectores) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (sesion.plazaId != null) {
                                Text(
                                    "Plaza: ${sesion.plazaNombre ?: ""}",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            } else {
                                Text(
                                    "Error: No tienes una plaza asignada en tu perfil.",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

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
}

@Composable
private fun EncabezadoAtencion(sesion: Sesion, apiBaseUrl: String) {
    val nombreAMostrar = sesion.nombreCompleto.ifBlank { sesion.correo }.ifBlank { "Usuario sin nombre" }

    val fotoUrl = remember(sesion.fotoPerfil, apiBaseUrl) {
        sesion.fotoPerfil?.let { perfil ->
            if (perfil.startsWith("http")) {
                perfil
            } else {
                val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
                "$base/$perfil"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(90.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            if (fotoUrl != null) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = {
                        Timber.d("COIL_DEBUG: Cargando imagen desde $fotoUrl")
                    },
                    onSuccess = {
                        Timber.d("COIL_DEBUG: Imagen cargada con éxito desde $fotoUrl")
                    },
                    onError = { errorState ->
                        Timber.e(errorState.result.throwable, "COIL_DEBUG: Falló la carga. Razón: ${errorState.result.throwable.message}")
                    }
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Te atendió:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = nombreAMostrar,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = sesion.rol,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun BuscadorTienda(
    tiendas: List<TiendaDto>,
    seleccionId: Int?,
    onSeleccionar: (Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val tiendaSeleccionada = remember(seleccionId, tiendas) { tiendas.firstOrNull { it.id == seleccionId } }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (tiendaSeleccionada != null) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Tienda seleccionada:", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${tiendaSeleccionada.codigo} - ${tiendaSeleccionada.nombre}", 
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { onSeleccionar(-1); query = "" }) {
                        Text("CAMBIAR")
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar tienda por CR o nombre") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            if (query.length >= 2) {
                val filtradas = tiendas.filter {
                    it.codigo.contains(query, ignoreCase = true) || it.nombre.contains(query, ignoreCase = true)
                }.take(8)

                if (filtradas.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {
                            filtradas.forEach { t ->
                                ListItem(
                                    headlineContent = { Text("${t.codigo} - ${t.nombre}") },
                                    modifier = Modifier.clickable {
                                        onSeleccionar(t.id)
                                        query = ""
                                    }
                                )
                                if (t != filtradas.last()) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                } else {
                    Text(
                        "No se encontraron coincidencias.", 
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
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

@Composable
fun PantallaAgradecimiento(
    onReiniciar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "¡Gracias por su evaluación!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = "Su opinión es muy valiosa para nosotros. Por favor, entregue el dispositivo al prestador de servicios.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onReiniciar,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Cerrar")
            }
        }
    }
}
