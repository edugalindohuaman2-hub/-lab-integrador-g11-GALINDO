package com.galindo.rickandmorty.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.galindo.rickandmorty.ui.navigation.AppBottomBar
import com.galindo.rickandmorty.ui.theme.RickandmortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    esModoOscuro: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateFavoritos: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val stats by viewModel.stats.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()
    val personajes by viewModel.personajesAdmin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var editingId by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Alive") }
    var imagenUrl by remember { mutableStateOf("") }
    var showFormDialog by remember { mutableStateOf(false) }
    val estadosDisponibles = listOf("Alive", "Dead", "Unknown")

    var showDeleteDialog by remember { mutableStateOf<AdminCharacter?>(null) }

    RickandmortyTheme(darkTheme = esModoOscuro) {
        val bgColor = MaterialTheme.colorScheme.background
        val textColor = MaterialTheme.colorScheme.onBackground
        val surfaceColor = MaterialTheme.colorScheme.surface
        val primaryColor = MaterialTheme.colorScheme.primary

        Scaffold(
            containerColor = bgColor,
            topBar = {
                TopAppBar(
                    title = { Text("Gestión Firebase", color = textColor, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = textColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (esModoOscuro) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = "Cambiar Tema",
                                tint = primaryColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingId = null
                        nombre = ""
                        especie = ""
                        genero = ""
                        estado = "Alive"
                        imagenUrl = ""
                        showFormDialog = true
                    },
                    containerColor = primaryColor
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Personaje Firebase", tint = MaterialTheme.colorScheme.onPrimary)
                }
            },
            bottomBar = {
                AppBottomBar(
                    currentRoute = "admin",
                    isAdmin = esAdmin,
                    onNavigateHome = onNavigateHome,
                    onNavigateFavoritos = onNavigateFavoritos,
                    onNavigateAdmin = onNavigateAdmin,
                    onLogout = onLogout
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dashboard Metrics Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Panel de Control Cloud Firestore",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Personajes en Firebase", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${personajes.size}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                            }
                            Column {
                                Text(text = "Total en Caché", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${stats.first}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.cargarPersonajesIniciales {
                                    Toast.makeText(context, "¡Base de datos poblada con éxito!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Text("CARGAR DATOS INICIALES EN FIREBASE", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.cargarPersonajesDesdeApi {
                                    Toast.makeText(context, "¡Personajes cargados desde la API de Rick and Morty!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            Text("CARGAR DESDE API RICK & MORTY", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Registros de Firebase",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )

                if (personajes.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hay personajes registrados en Firebase.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(personajes, key = { it.id }) { p ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = surfaceColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (p.imagenUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = p.imagenUrl,
                                            contentDescription = p.nombre,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = p.nombre,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = textColor
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${p.especie} · ${p.genero} · ${p.estado}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = {
                                        editingId = p.id
                                        nombre = p.nombre
                                        especie = p.especie
                                        genero = p.genero
                                        estado = p.estado.ifBlank { "Alive" }
                                        imagenUrl = p.imagenUrl
                                        showFormDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = primaryColor)
                                    }
                                    IconButton(onClick = {
                                        showDeleteDialog = p
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo CRUD Form
        if (showFormDialog) {
            AlertDialog(
                onDismissRequest = { showFormDialog = false },
                title = { Text(if (editingId == null) "Crear Personaje" else "Editar Personaje", color = textColor, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = especie,
                            onValueChange = { especie = it },
                            label = { Text("Especie") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = genero,
                            onValueChange = { genero = it },
                            label = { Text("Género") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Estado:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            estadosDisponibles.forEach { opcion ->
                                FilterChip(
                                    selected = estado == opcion,
                                    onClick = { estado = opcion },
                                    label = { Text(opcion) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primaryColor,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = surfaceColor,
                                        labelColor = textColor
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = imagenUrl,
                            onValueChange = { imagenUrl = it },
                            label = { Text("URL de Imagen HD") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nombre.isNotBlank() && especie.isNotBlank()) {
                                viewModel.guardarPersonaje(
                                    id = editingId,
                                    nombre = nombre,
                                    especie = especie,
                                    genero = genero,
                                    estado = estado,
                                    imagenUrl = imagenUrl
                                ) {
                                    showFormDialog = false
                                }
                            } else {
                                Toast.makeText(context, "Por favor completa al menos Nombre y Especie", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Guardar", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFormDialog = false }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                containerColor = surfaceColor
            )
        }

        // Diálogo Eliminación
        if (showDeleteDialog != null) {
            val target = showDeleteDialog!!
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Eliminar Personaje", color = textColor, fontWeight = FontWeight.Bold) },
                text = { Text("¿Deseas eliminar a ${target.nombre} de Cloud Firestore?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarPersonaje(target.id)
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.onError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                containerColor = surfaceColor
            )
        }
    }
}
