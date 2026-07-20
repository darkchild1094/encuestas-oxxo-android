package mx.com.getic.encuestasoxxo.ui.usuarios

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.com.getic.encuestasoxxo.data.remote.dto.*
import mx.com.getic.encuestasoxxo.data.repository.EncuestaRepository
import mx.com.getic.encuestasoxxo.data.repository.UsuarioRepository
import java.io.File

class UsuariosViewModel(
    private val repo: UsuarioRepository,
    private val encuestaRepo: EncuestaRepository
) : ViewModel() {
    var usuarios by mutableStateOf<List<UsuarioDto>>(emptyList())
    var roles by mutableStateOf<List<RolDto>>(emptyList())
    var plazas by mutableStateOf<List<PlazaDto>>(emptyList())
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init { cargar() }

    fun cargar() {
        cargando = true
        viewModelScope.launch {
            try {
                usuarios = repo.obtenerUsuarios()
                roles = repo.obtenerRoles()
                val negocios = encuestaRepo.negocios()
                if (negocios.isNotEmpty()) {
                    val regiones = encuestaRepo.regiones(negocios.first().id)
                    if (regiones.isNotEmpty()) {
                        plazas = encuestaRepo.plazas(regiones.first().id)
                    }
                }
            } catch (e: Exception) {
                error = "Error al cargar datos"
            } finally {
                cargando = false
            }
        }
    }

    fun crear(correo: String, nombre: String, rolId: Int, plazaId: Int?, password: String, foto: File?) {
        viewModelScope.launch {
            try {
                val res = repo.crearUsuario(correo, nombre, rolId, plazaId, password, foto)
                if (res.success) cargar() else error = res.error
            } catch (e: Exception) { error = "Error de red" }
        }
    }

    fun editar(id: Int, nombre: String, rolId: Int, plazaId: Int?, password: String?, foto: File?) {
        viewModelScope.launch {
            try {
                val res = repo.editarUsuario(id, nombre, rolId, plazaId, password, foto)
                if (res.success) cargar() else error = res.error
            } catch (e: Exception) { error = "Error de red" }
        }
    }

    fun uriToFile(context: android.content.Context, uri: Uri): File? {
        return repo.uriToFile(context.contentResolver, uri, context.cacheDir)
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            try {
                val res = repo.eliminarUsuario(id)
                if (res.success) cargar() else error = res.error
            } catch (e: Exception) { error = "Error de red" }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    viewModel: UsuariosViewModel,
    onAbrirMenu: () -> Unit,
    apiBaseUrl: String
) {
    var mostrarDialogo by remember { mutableStateOf<UsuarioDto?>(null) }
    var modoCrear by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) { Icon(Icons.Default.Menu, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { modoCrear = true }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        if (viewModel.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
                items(viewModel.usuarios) { u ->
                    ListItem(
                        headlineContent = { Text(u.nombre_completo ?: u.correo ?: "S/N") },
                        supportingContent = { Text("${u.rol} | ${u.plaza_nombre ?: "Sin plaza"}") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { mostrarDialogo = u }) { Icon(Icons.Default.Edit, null) }
                                IconButton(onClick = { viewModel.eliminar(u.id) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (modoCrear) {
            UsuarioDialog(
                onDismiss = { modoCrear = false },
                onConfirm = { correo, nombre, rol, plaza, pass, foto -> 
                    viewModel.crear(correo, nombre, rol, plaza, pass, foto)
                    modoCrear = false 
                },
                roles = viewModel.roles,
                plazas = viewModel.plazas,
                viewModel = viewModel,
                apiBaseUrl = apiBaseUrl
            )
        }

        if (mostrarDialogo != null) {
            UsuarioDialog(
                usuario = mostrarDialogo,
                onDismiss = { mostrarDialogo = null },
                onConfirm = { _, nombre, rol, plaza, pass, foto -> 
                    viewModel.editar(mostrarDialogo!!.id, nombre, rol, plaza, pass, foto)
                    mostrarDialogo = null 
                },
                roles = viewModel.roles,
                plazas = viewModel.plazas,
                viewModel = viewModel,
                apiBaseUrl = apiBaseUrl
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioDialog(
    usuario: UsuarioDto? = null,
    onDismiss: () -> Unit,
    onConfirm: (correo: String, nombre: String, rolId: Int, plazaId: Int?, pass: String, foto: File?) -> Unit,
    roles: List<RolDto>,
    plazas: List<PlazaDto>,
    viewModel: UsuariosViewModel,
    apiBaseUrl: String
) {
    var correo by remember { mutableStateOf(usuario?.correo ?: "") }
    var nombre by remember { mutableStateOf(usuario?.nombre_completo ?: "") }
    var rolId by remember { mutableStateOf(roles.find { it.nombre == usuario?.rol }?.id ?: roles.firstOrNull()?.id ?: 0) }
    var plazaId by remember { mutableStateOf(usuario?.plaza_id) }
    var password by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoFile by remember { mutableStateOf<File?>(null) }
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fotoUri = uri
        uri?.let { fotoFile = viewModel.uriToFile(context, it) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (usuario == null) "Nuevo Usuario" else "Editar Usuario") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(80.dp).clip(CircleShape).clickable { launcher.launch("image/*") },
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            if (fotoUri != null) {
                                AsyncImage(model = fotoUri, contentDescription = null, contentScale = ContentScale.Crop)
                            } else if (usuario?.foto_perfil != null) {
                                // Reutilizar logica de URL si ya tiene foto
                                val base = apiBaseUrl.trimEnd('/').removeSuffix("/api").trimEnd('/')
                                AsyncImage(model = "$base/${usuario.foto_perfil}", contentDescription = null, contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.padding(20.dp))
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true // Ahora editable
                    )
                }
                item {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text("Rol", style = MaterialTheme.typography.labelSmall)
                    roles.forEach { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = rolId == r.id, onClick = { rolId = r.id })
                            Text(r.nombre)
                        }
                    }
                }
                item {
                    Text("Plaza", style = MaterialTheme.typography.labelSmall)
                    val plazaSeleccionada = plazas.find { it.id == plazaId }
                    var expandidoPlaza by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandidoPlaza,
                        onExpandedChange = { expandidoPlaza = it }
                    ) {
                        OutlinedTextField(
                            value = plazaSeleccionada?.nombre ?: "Sin plaza",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoPlaza) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandidoPlaza,
                            onDismissRequest = { expandidoPlaza = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin plaza") },
                                onClick = { plazaId = null; expandidoPlaza = false }
                            )
                            plazas.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.nombre) },
                                    onClick = { plazaId = p.id; expandidoPlaza = false }
                                )
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (usuario == null) "Password" else "Nueva Password (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(correo, nombre, rolId, plazaId, password, fotoFile)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
