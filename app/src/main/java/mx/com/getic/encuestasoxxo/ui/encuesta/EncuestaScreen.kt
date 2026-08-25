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
import mx.com.getic.encuestasoxxo.data.remote.dto.AtiDto
import mx.com.getic.encuestasoxxo.data.remote.dto.TiendaDto
import timber.log.Timber

// La pregunta del PFS no tiene un flag propio en el schema (ver
// nota en pregunta.texto): se identifica por texto. Si en algun
// momento se agrega una columna dedicada (ej. tipo_foto), cambiar
// este helper y ya -- toda la UI lo usa desde aqui.
private fun esPreguntaDePfs(texto: String): Boolean =
    texto.contains("PFS", ignoreCase = true) || texto.contains("Prestador de Field Service", ignoreCase = true)

private fun urlFoto(rutaFoto: String?, apiBaseUrl: String): String? {
    if (rutaFoto.isNullOrBlank()) return null
    if (rutaFoto.startsWith("http")) return rutaFoto
    val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
    return "$base/public/$rutaFoto"
}

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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
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

                if (estado.tiendaSeleccionada != null) {
                    item {
                        SaludoAti(
                            tienda = estado.tiendaSeleccionada,
                            atisDisponibles = estado.atisDisponibles,
                            cargandoAtis = estado.cargandoAtis,
                            asignandoAti = estado.asignandoAti,
                            apiBaseUrl = apiBaseUrl,
                            onSeleccionarAti = viewModel::onAtiSeleccionado,
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
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)

                        // Solo la pregunta del PFS lleva la foto del tecnico
                        // que esta contestando -- es quien atendio la
                        // incidencia de la que habla esa pregunta puntual.
                        if (esPreguntaDePfs(pregunta.texto)) {
                            FotoTecnico(sesion, apiBaseUrl)
                        }

                        NpsFaceSelector(
                            seleccion = estado.calificaciones[pregunta.id],
                            onSeleccionar = { viewModel.onCalificar(pregunta.id, it) },
                        )
                    }
                }

                if (estado.preguntas.isNotEmpty()) {
                    // Pregunta abierta, fija siempre al final (despues de
                    // la calificacion general de TI): reutiliza
                    // encuesta.comentario, que ya viaja end-to-end al
                    // backend -- no es una fila de `pregunta`.
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "¿Qué podríamos mejorar en el servicio de TI para facilitar tu operación diaria en tienda?",
                                style = MaterialTheme.typography.titleMedium,
                            )
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
private fun SaludoAti(
    tienda: TiendaDto,
    atisDisponibles: List<AtiDto>,
    cargandoAtis: Boolean,
    asignandoAti: Boolean,
    apiBaseUrl: String,
    onSeleccionarAti: (Int) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        if (tienda.ati_usuario_id != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AvatarCircular(urlFoto(tienda.ati_foto, apiBaseUrl), size = 64.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "¡Hola! Soy ${tienda.ati_nombre ?: "tu ATI"}, tu Asesora de TI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    )
                    Text(
                        "Estoy para apoyarte cuando necesites ayuda con los servicios y equipos de la tienda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    "Esta tienda todavía no tiene un ATI asignado. Selecciona quién te atiende:",
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (cargandoAtis) {
                    Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else if (atisDisponibles.isEmpty()) {
                    Text(
                        "No hay ATIs registrados para la plaza de esta tienda.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else {
                    atisDisponibles.forEach { ati ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable(enabled = !asignandoAti) { onSeleccionarAti(ati.id) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            AvatarCircular(urlFoto(ati.foto_perfil, apiBaseUrl), size = 44.dp)
                            Text(ati.nombre_completo, style = MaterialTheme.typography.bodyLarge)
                            if (asignandoAti) {
                                Spacer(Modifier.weight(1f))
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FotoTecnico(sesion: Sesion, apiBaseUrl: String) {
    val nombreAMostrar = sesion.nombreCompleto.ifBlank { sesion.correo }.ifBlank { "Usuario sin nombre" }
    val fotoUrl = remember(sesion.fotoPerfil, apiBaseUrl) { urlFoto(sesion.fotoPerfil, apiBaseUrl) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AvatarCircular(fotoUrl, size = 48.dp)
        Column {
            Text(nombreAMostrar, style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Text(sesion.rol, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun AvatarCircular(fotoUrl: String?, size: androidx.compose.ui.unit.Dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 1.dp,
    ) {
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { errorState ->
                    Timber.e(errorState.result.throwable, "COIL_DEBUG: Falló la carga desde $fotoUrl")
                }
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(size / 2),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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
