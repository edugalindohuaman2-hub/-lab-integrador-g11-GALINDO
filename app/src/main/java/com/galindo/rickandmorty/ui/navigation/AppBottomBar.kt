package com.galindo.rickandmorty.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomBar(
    currentRoute: String,
    isAdmin: Boolean,
    onNavigateHome: () -> Unit,
    onNavigateFavoritos: () -> Unit,
    onNavigateAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF171212),
        contentColor = Color.White
    ) {
        // 1. Lista (Inicio)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Lista") },
            label = { Text("Lista") },
            selected = currentRoute == "lista",
            onClick = onNavigateHome,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8),
                indicatorColor = Color(0xFF5C4033)
            )
        )
        // 2. Favoritos
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = currentRoute == "favoritos",
            onClick = onNavigateFavoritos,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8),
                indicatorColor = Color(0xFF5C4033)
            )
        )
        // 3. Admin (Solo si isAdmin == true)
        if (isAdmin) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Security, contentDescription = "Admin") },
                label = { Text("Admin") },
                selected = currentRoute == "admin",
                onClick = onNavigateAdmin,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8),
                    indicatorColor = Color(0xFF5C4033)
                )
            )
        }
        // 4. Salir
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir") },
            label = { Text("Salir") },
            selected = false,
            onClick = onLogout,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color.White,
                unselectedIconColor = Color(0xFFEF4444),
                unselectedTextColor = Color(0xFFEF4444),
                indicatorColor = Color.Transparent
            )
        )
    }
}
