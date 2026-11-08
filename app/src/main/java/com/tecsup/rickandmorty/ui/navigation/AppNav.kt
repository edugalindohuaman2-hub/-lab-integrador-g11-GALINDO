package com.tecsup.rickandmorty.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.rickandmorty.AppContainer
import com.tecsup.rickandmorty.domain.model.Rol
import com.tecsup.rickandmorty.ui.admin.AdminScreen
import com.tecsup.rickandmorty.ui.admin.AdminViewModel
import com.tecsup.rickandmorty.ui.detalle.DetalleScreen
import com.tecsup.rickandmorty.ui.detalle.DetalleViewModel
import com.tecsup.rickandmorty.ui.favoritos.FavoritosScreen
import com.tecsup.rickandmorty.ui.favoritos.FavoritosViewModel
import com.tecsup.rickandmorty.ui.lista.ListaScreen
import com.tecsup.rickandmorty.ui.lista.ListaViewModel
import com.tecsup.rickandmorty.ui.login.LoginScreen
import com.tecsup.rickandmorty.ui.login.LoginViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Lista : Screen("lista")
    object Detalle : Screen("detalle/{itemId}") {
        fun createRoute(itemId: Int) = "detalle/$itemId"
    }
    object Favoritos : Screen("favoritos")
    object Admin : Screen("admin")
}

@Composable
fun AppNav(container: AppContainer) {
    val navController = rememberNavController()
    var currentUserRole = Rol.USUARIO

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val viewModel = LoginViewModel(container.authRepository)
            LoginScreen(
                viewModel = viewModel,
                onLoginExitoso = { sesion ->
                    currentUserRole = sesion.rol
                    navController.navigate(Screen.Lista.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lista.route) {
            val viewModel = ListaViewModel(container.itemRepository, container.notificador)
            ListaScreen(
                viewModel = viewModel,
                onItemClick = { itemId ->
                    navController.navigate(Screen.Detalle.createRoute(itemId))
                },
                onFavoritosClick = {
                    navController.navigate(Screen.Favoritos.route)
                },
                onAdminClick = {
                    navController.navigate(Screen.Admin.route)
                },
                isAdmin = currentUserRole == Rol.ADMIN
            )
        }

        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            val viewModel = DetalleViewModel(itemId, container.itemRepository)
            DetalleScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Favoritos.route) {
            val viewModel = FavoritosViewModel(container.itemRepository)
            FavoritosScreen(
                viewModel = viewModel,
                onItemClick = { itemId ->
                    navController.navigate(Screen.Detalle.createRoute(itemId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Admin.route) {
            if (currentUserRole != Rol.ADMIN) {
                navController.popBackStack()
            } else {
                val viewModel = AdminViewModel(container.itemRepository, container.notificador)
                AdminScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
