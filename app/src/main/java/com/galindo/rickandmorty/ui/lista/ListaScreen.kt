package com.galindo.rickandmorty.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.ui.UiState
import com.galindo.rickandmorty.ui.components.Cargando
import com.galindo.rickandmorty.ui.components.ErrorView
import com.galindo.rickandmorty.ui.navigation.AppBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
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

    Scaffold(
        containerColor = Color(0xFF181212),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Rick and Morty Catálogo", 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF181212)
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
                .background(Color(0xFF181212))
        ) {
            // Offline aviso banner
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

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChanged(it) },
                    placeholder = { Text("Buscar personajes por nombre o especie...", color = Color(0xFF94A3B8)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF00E676)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
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
                                    tint = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No se encontraron personajes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF94A3B8)
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
                        color = Color(0xFF00E676)
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterItemCard(
    item: Item,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val statusText = item.status.ifBlank { "Unknown" }
    val statusColor = when {
        statusText.contains("Alive", ignoreCase = true) -> Color(0xFF81C784)
        statusText.contains("Dead", ignoreCase = true) -> Color(0xFFE57373)
        else -> Color(0xFF9E9E9E)
    }

    val especie = item.species.ifBlank { "Unknown" }
    val genero = item.gender.ifBlank { "Unknown" }
    val especieGenero = "$especie · $genero"

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF2D2424)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = Modifier
                    .width(75.dp)
                    .height(105.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = especieGenero,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp
                    ),
                    color = Color(0xFFB0A4A4)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor)
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp
                        ),
                        color = statusColor
                    )
                }
            }

            // Vista de usuario: exclusivo botón de favorito (sin FAB ni opciones admin)
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (item.favorito) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (item.favorito) Color(0xFFEF4444) else Color(0xFFB0A4A4)
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    item: Item,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    CharacterItemCard(item = item, onClick = onClick, onFavoriteClick = onFavoriteClick)
}

@Composable
fun ItemRow(
    item: Item,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    CharacterItemCard(item = item, onClick = onClick, onFavoriteClick = onFavoriteClick)
}
