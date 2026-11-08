package com.galindo.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.galindo.rickandmorty.ui.navigation.AppNav
import com.galindo.rickandmorty.ui.theme.RickandmortyTheme

class MainActivity : ComponentActivity() {
    lateinit var container: AppContainer
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = AppContainer(applicationContext)
        enableEdgeToEdge()
        setContent {
            RickandmortyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNav(container = container)
                }
            }
        }
    }
}
