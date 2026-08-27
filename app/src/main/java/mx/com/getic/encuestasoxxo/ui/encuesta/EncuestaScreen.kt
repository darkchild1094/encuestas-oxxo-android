package mx.com.getic.encuestasoxxo.ui.encuesta

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.com.getic.encuestasoxxo.R
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
    // OJO: sin /public/ -- alwaysdata sirve la carpeta public/ del repo
    // directamente como raiz de /nps/ (ver RewriteBase /nps/ dentro de
    // public/.htaccess), asi que agregar /public/ aqui duplica la ruta
    // y la foto no carga. Mismo patron que ya usa UsuariosScreen.kt.
    val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
    return "$base/$rutaFoto"
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
                tiendaNombre = estado.tiendaSeleccionada?.nombre.orEmpty(),
                folio = estado.folio,
                onReiniciar = { viewModel.reiniciarParaNuevaEncuesta() },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = estado.folio,
                        onValueChange = viewModel::onFolioChange,
                        label = { Text("Número de folio") },
                        placeholder = { Text("INC") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                    )
                }

                if (estado.folio.isNotBlank()) {
                if (estado.tiendaSeleccionada != null) {
                    item {
                        val plaza = estado.plazas.firstOrNull { it.id == estado.tiendaSeleccionada.plaza_id }
                        val pNombre = plaza?.nombre ?: sesion.plazaNombre ?: ""
                        HeaderTienda(
                            tienda = estado.tiendaSeleccionada,
                            plazaNombre = pNombre,
                            onCambiarTienda = { viewModel.onTiendaSeleccionada(-1) }
                        )
                    }

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

                    item {
                        Text(
                            text = "Ayúdame a responder las siguientes preguntas por favor:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
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
                        if (esPreguntaDePfs(pregunta.texto)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val fotoUrl = remember(sesion.fotoPerfil, apiBaseUrl) { urlFoto(sesion.fotoPerfil, apiBaseUrl) }
                                AvatarGafete(fotoUrl, width = 100.dp)
                                Text(
                                    text = pregunta.texto,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            Text(pregunta.texto, style = MaterialTheme.typography.titleMedium)
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
private fun HeaderTienda(
    tienda: TiendaDto,
    plazaNombre: String,
    onCambiarTienda: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_oxxo),
            contentDescription = "OXXO",
            modifier = Modifier.height(44.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tienda.nombre.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (plazaNombre.isNotBlank()) {
                Text(
                    text = "PLAZA $plazaNombre".uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        TextButton(
            onClick = onCambiarTienda,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text("CAMBIAR", style = MaterialTheme.typography.labelSmall)
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
                AvatarCircular(urlFoto(tienda.ati_foto, apiBaseUrl), size = 72.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val labelAsesor = when {
                        tienda.ati_genero == "H" -> "Asesor"
                        tienda.ati_genero == "M" -> "Asesora"
                        // Heurística simple: si el nombre termina en 'a', es mujer.
                        tienda.ati_nombre?.trim()?.endsWith("a", ignoreCase = true) == true -> "Asesora"
                        else -> "Asesor"
                    }
                    Text(
                        text = "¡Hola! Soy ${tienda.ati_nombre ?: "tu ATI"}, tu $labelAsesor de TI y estoy para apoyarte cuando necesites ayuda con los servicios y equipos de la tienda.",
                        style = MaterialTheme.typography.bodyMedium,
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
private fun AvatarGafete(fotoUrl: String?, width: Dp) {
    Surface(
        modifier = Modifier
            .width(width)
            .aspectRatio(0.63f), // Proporción vertical típica de un gafete
        shape = RoundedCornerShape(2.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        if (fotoUrl != null) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = "Gafete",
                contentScale = ContentScale.Fit, // Asegura que se vea completa
                modifier = Modifier.fillMaxSize().padding(2.dp),
                onError = { errorState ->
                    Timber.e(errorState.result.throwable, "COIL_DEBUG: Falló la carga desde $fotoUrl")
                }
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(width / 3),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AvatarCircular(fotoUrl: String?, size: Dp) {
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
                }.take(20)

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
    tiendaNombre: String,
    folio: String,
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
            Image(
                painter = painterResource(R.drawable.logo_oxxo),
                contentDescription = "Logo OXXO",
                modifier = Modifier.size(width = 180.dp, height = 96.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = "¡Gracias por su evaluación!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = tiendaNombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Text(
                text = "Folio: $folio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
