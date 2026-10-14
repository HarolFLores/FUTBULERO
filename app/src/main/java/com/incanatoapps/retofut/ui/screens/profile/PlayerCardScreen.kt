package com.incanatoapps.retofut.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.components.FifaCard
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCardScreen(
    navController: NavHostController,
    playerId: Int,
    viewModel: PlayerCardViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val state = viewModel.cardState

    LaunchedEffect(playerId) {
        val token = sessionManager.fetchAuthToken()
        if (token != null) {
            viewModel.loadPlayerCard(token, playerId)
        }
    }

    MainScaffold(
        navController = navController,
        title = "Ficha Técnica"
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is PlayerCardState.Loading -> CircularProgressIndicator(color = BrandGreen)
                is PlayerCardState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = Color.Red)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { 
                        val token = sessionManager.fetchAuthToken()
                        if (token != null) viewModel.loadPlayerCard(token, playerId)
                    }) {
                        Text("Reintentar")
                    }
                }
                is PlayerCardState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        FifaCard(card = state.card)
                        
                        Spacer(Modifier.height(32.dp))
                        
                        Text(
                            "Estadísticas de Campo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        
                        Text(
                            "Datos basados en el rendimiento real en partidos oficiales de FootBolero.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
