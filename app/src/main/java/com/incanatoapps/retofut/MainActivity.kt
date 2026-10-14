package com.incanatoapps.retofut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.incanatoapps.retofut.navigation.NavGraph
import com.incanatoapps.retofut.ui.theme.RetofutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetofutTheme {
                FutRetoApp()
            }
        }
    }
}

@Composable
fun FutRetoApp() {
    val navController = rememberNavController()
    
    // Usamos el Scaffold que viene definido en DashboardScreen o una estructura limpia aquí
    // Para este diseño Soft UI Premium, el DashboardScreen ya gestiona su propio Scaffold y BottomBar
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavGraph(navController = navController)
    }
}
