package com.galindo.rickandmorty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.galindo.rickandmorty.AppContainer
import com.galindo.rickandmorty.ui.admin.AdminScreen
import com.galindo.rickandmorty.ui.admin.AdminViewModel
import com.galindo.rickandmorty.ui.detalle.DetalleScreen
import com.galindo.rickandmorty.ui.detalle.DetalleViewModel
import com.galindo.rickandmorty.ui.favoritos.FavoritosScreen
import com.galindo.rickandmorty.ui.favoritos.FavoritosViewModel
import com.galindo.rickandmorty.ui.lista.ListaScreen
import com.galindo.rickandmorty.ui.lista.ListaViewModel
import com.galindo.rickandmorty.ui.login.LoginScreen
import com.galindo.rickandmorty.ui.login.LoginViewModel

inline fun <reified VM : ViewModel> viewModelFactory(crossinline creator: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return creator() as T
        }
    }
}

@Composable
fun AppNav(container: AppContainer) {
    val navController = rememberNavController()

    val startDestination = if (container.authRepository.isLoggedIn) "lista" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            val viewModel: LoginViewModel = viewModel(
                factory = viewModelFactory { LoginViewModel(container.authRepository) }
            )
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("lista") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("lista") {
            val viewModel: ListaViewModel = viewModel(
                factory = viewModelFactory { ListaViewModel(container.itemRepository, container.authRepository) }
            )
            ListaScreen(
                viewModel = viewModel,
                onItemClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onFavoritesClick = {
                    navController.navigate("favoritos")
                },
                onAdminClick = {
                    navController.navigate("admin")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("lista") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val viewModel: DetalleViewModel = viewModel(
                factory = viewModelFactory { DetalleViewModel(id, container.itemRepository) }
            )
            DetalleScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("favoritos") {
            val viewModel: FavoritosViewModel = viewModel(
                factory = viewModelFactory { FavoritosViewModel(container.itemRepository) }
            )
            FavoritosScreen(
                viewModel = viewModel,
                onItemClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("admin") {
            val viewModel: AdminViewModel = viewModel(
                factory = viewModelFactory { AdminViewModel(container.itemRepository, container.notificador) }
            )
            AdminScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
