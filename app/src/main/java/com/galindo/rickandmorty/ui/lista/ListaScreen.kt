package com.galindo.rickandmorty.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.ui.UiState
import com.galindo.rickandmorty.ui.components.Cargando
import com.galindo.rickandmorty.ui.components.ErrorView

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personajes - Rick and Morty") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                    }
                    IconButton(onClick = onAdminClick) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                    }
                    IconButton(onClick = {
                        viewModel.cerrarSesion()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.refrescar() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay personajes disponibles. Presiona actualizar.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(lista, key = { it.id }) { item ->
                                ItemRow(
                                    item = item,
                                    onClick = { onItemClick(item.id) },
                                    onFavoriteClick = { viewModel.alternarFavorito(item.id) }
                                )
                            }
                        }
                    }
                }
            }

            if (isRefreshing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun ItemRow(
    item: Item,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (item.favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (item.favorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
