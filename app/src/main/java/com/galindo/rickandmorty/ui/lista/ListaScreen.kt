package com.galindo.rickandmorty.ui.lista

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galindo.rickandmorty.ui.UiState
import com.galindo.rickandmorty.ui.components.Cargando
import com.galindo.rickandmorty.ui.components.ErrorView
import com.galindo.rickandmorty.ui.navigation.AppBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    esModoOscuro: Boolean,
    onToggleTheme: () -> Unit,
    onItemClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val aviso by viewModel.aviso.collectAsState()
    val query by viewModel.query.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    val unreadCount by viewModel.unreadCount.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Rick and Morty", 
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ) 
                },
                actions = {
                    // Selector de Tema Global (Sol / Luna)
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (esModoOscuro) Icons.Default.WbSunny else Icons.Default.NightsStay,
                            contentDescription = "Cambiar Tema",
                            tint = primaryColor
                        )
                    }

                    // Campanita de Notificaciones del Usuario (Visible solo si NO es admin)
                    if (!esAdmin) {
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = primaryColor) {
                                            Text("$unreadCount", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = textColor
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "lista",
                isAdmin = esAdmin,
                onNavigateHome = {},
                onNavigateFavoritos = onFavoritesClick,
                onNavigateAdmin = onAdminClick,
                onLogout = onLogout
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(bgColor)
        ) {
            if (aviso != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Text(
                        text = aviso!!,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = { Text("Buscar personajes por nombre o especie...", color = onSurfaceVariantColor) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = primaryColor
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = onSurfaceVariantColor
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = onSurfaceVariantColor.copy(alpha = 0.5f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is UiState.Cargando -> {
                        if (!isRefreshing) {
                            Cargando()
                        }
                    }
                    is UiState.Error -> {
                        ErrorView(mensaje = state.mensaje, onReintentar = { viewModel.refrescar() })
                    }
                    is UiState.Exito -> {
                        val lista = state.datos
                        if (lista.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = "Sin resultados",
                                    modifier = Modifier.size(64.dp),
                                    tint = onSurfaceVariantColor
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No se encontraron personajes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = onSurfaceVariantColor
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(lista, key = { it.id }) { item ->
                                    CharacterItemCard(
                                        item = item,
                                        isAdmin = esAdmin,
                                        onClick = { onItemClick(item.id) },
                                        onFavoriteClick = { viewModel.alternarFavorito(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                        color = primaryColor
                    )
                }
            }
        }
    }

    // Diálogo de Historial de Notificaciones con Navegación Directa al Detalle
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Historial de Notificaciones (FCM)", color = onSurfaceColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (notifications.isEmpty()) {
                        Text("No hay notificaciones recientes.", color = onSurfaceVariantColor)
                    } else {
                        notifications.forEach { note ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (note.leida) surfaceColor else primaryColor.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.marcarComoLeida(note.id)
                                        showNotificationsDialog = false
                                        val targetId = note.idPersonaje ?: 1
                                        onItemClick(targetId)
                                    }
                            ) {
                                Text(
                                    text = "• ${note.mensaje}",
                                    modifier = Modifier.padding(12.dp),
                                    color = onSurfaceColor,
                                    fontSize = 13.sp,
                                    fontWeight = if (note.leida) FontWeight.Normal else FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.marcarNotificacionesComoLeidas()
                        showNotificationsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Marcar como leídas", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Cerrar", color = onSurfaceVariantColor)
                }
            },
            containerColor = surfaceColor
        )
    }
}
