package com.galindo.rickandmorty

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.galindo.rickandmorty.ui.navigation.AppNav
import com.galindo.rickandmorty.ui.theme.RickandmortyTheme

class MainActivity : ComponentActivity() {
    lateinit var container: AppContainer
        private set

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = AppContainer(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge()
        setContent {
            var esModoOscuro by remember { mutableStateOf(true) }
            RickandmortyTheme(darkTheme = esModoOscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNav(
                        container = container,
                        esModoOscuro = esModoOscuro,
                        onToggleTheme = { esModoOscuro = !esModoOscuro }
                    )
                }
            }
        }
    }
}
