package com.galindo.rickandmorty.ui.favoritos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.ui.UiState
import com.galindo.rickandmorty.ui.components.Cargando
import com.galindo.rickandmorty.ui.components.ErrorView
import com.galindo.rickandmorty.ui.lista.CharacterItemCard
import com.galindo.rickandmorty.ui.navigation.AppBottomBar
import com.galindo.rickandmorty.ui.theme.RickandmortyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    esModoOscuro: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onItemClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit = {},
    onNavigateFavoritos: () -> Unit = {},
    onNavigateAdmin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.estado.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()

    RickandmortyTheme(darkTheme = esModoOscuro) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("Favoritos", color = MaterialTheme.colorScheme.onBackground) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (esModoOscuro) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = "Cambiar Tema",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                AppBottomBar(
                    currentRoute = "favoritos",
                    isAdmin = esAdmin,
                    onNavigateHome = onNavigateHome,
                    onNavigateFavoritos = onNavigateFavoritos,
                    onNavigateAdmin = onNavigateAdmin,
                    onLogout = onLogout
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (val state = uiState) {
                    is UiState.Cargando -> {
                        Cargando()
                    }
                    is UiState.Error -> {
                        ErrorView(mensaje = state.mensaje)
                    }
                    is UiState.Exito -> {
                        val lista = state.datos
                        if (lista.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes personajes favoritos", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    CharacterItemCard(
        item = item,
        isAdmin = false,
        onClick = onClick,
        onFavoriteClick = onFavoriteClick
    )
}
