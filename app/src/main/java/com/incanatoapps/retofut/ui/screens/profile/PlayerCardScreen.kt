package com.incanatoapps.retofut.ui.screens.profile

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.CardPerformance
import com.incanatoapps.retofut.data.network.model.CardStats
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse
import com.incanatoapps.retofut.ui.components.FifaCard
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
    val token = sessionManager.fetchAuthToken()
    val userName = sessionManager.fetchUserName() ?: "Piero Flores"

    BackHandler { navController.popBackStack() }

    LaunchedEffect(playerId) {
        if (token != null && token != "expo_guest_token") {
            viewModel.loadPlayerCard(token, playerId)
        }
    }

    val fallbackCard = remember(playerId, userName) {
        val name = when (playerId) {
            1 -> userName
            2 -> "Mateo Farfán"
            3 -> "Christian Cueva Jr."
            4 -> "Carlos Zambrano R."
            else -> "Jugador #$playerId"
        }
        val pos = when (playerId) {
            1 -> "DC"
            2 -> "MC"
            3 -> "EXT"
            4 -> "DFC"
            else -> "DEL"
        }
        val rating = when (playerId) {
            1 -> 87
            2 -> 84
            3 -> 82
            4 -> 85
            else -> 80
        }
        PlayerCardResponse(
            id = playerId,
            nombre = name,
            foto = null,
            posicion = pos,
            rating = rating,
            stats = CardStats(
                PAC = if (playerId == 1) 89 else 82,
                SHO = if (playerId == 1) 91 else 78,
                PAS = if (playerId == 1) 84 else 86,
                DRI = if (playerId == 1) 88 else 85,
                DEF = if (playerId == 1) 45 else 72,
                PHY = if (playerId == 1) 78 else 80
            ),
            performance = CardPerformance(
                partidosJugados = 24,
                goles = if (playerId == 1) 29 else 8,
                asistencias = if (playerId == 1) 14 else 18
            )
        )
    }

    val state = viewModel.cardState
    val cardToDisplay = if (state is PlayerCardState.Success) state.card else fallbackCard

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
                }

                Text(
                    "FICHA TÉCNICA OFICIAL",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )

                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "¡Mira la Ficha Técnica de ${cardToDisplay.nombre} en ChocoFulbo! Rating ${cardToDisplay.rating} OVR • Posición ${cardToDisplay.posicion}."
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartir Ficha Técnica"))
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = TextPrimary)
                }
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CARTA FIFA 3D ---
            item {
                Spacer(Modifier.height(12.dp))
                FifaCard(card = cardToDisplay)
                Spacer(Modifier.height(20.dp))
            }

            // --- DETALLES DE RENDIMIENTO ---
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "DESGLOSE DE ATRIBUTOS FIFA",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = DarkForestGreen,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(14.dp))

                        AttributeProgressBar("Ritmo & Aceleración (PAC)", cardToDisplay.stats.PAC, LimeNeon)
                        Spacer(Modifier.height(10.dp))
                        AttributeProgressBar("Definición & Disparo (SHO)", cardToDisplay.stats.SHO, DarkForestGreen)
                        Spacer(Modifier.height(10.dp))
                        AttributeProgressBar("Visión & Pases (PAS)", cardToDisplay.stats.PAS, LimeNeon)
                        Spacer(Modifier.height(10.dp))
                        AttributeProgressBar("Regate & Habilidad (DRI)", cardToDisplay.stats.DRI, DarkForestGreen)
                        Spacer(Modifier.height(10.dp))
                        AttributeProgressBar("Defensa & Recuperación (DEF)", cardToDisplay.stats.DEF, Color(0xFF94A3B8))
                        Spacer(Modifier.height(10.dp))
                        AttributeProgressBar("Físico & Resistencia (PHY)", cardToDisplay.stats.PHY, LimeNeon)
                    }
                }
            }

            // --- ESTADÍSTICAS OFICIALES EN CANCHA ---
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "RENDIMIENTO EN PARTIDOS OFICIALES",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PARTIDOS", fontSize = 10.sp, color = TextSecondary)
                                Text("${cardToDisplay.performance.partidosJugados}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GOLES", fontSize = 10.sp, color = TextSecondary)
                                Text("${cardToDisplay.performance.goles} ⚽", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ASISTENCIAS", fontSize = 10.sp, color = TextSecondary)
                                Text("${cardToDisplay.performance.asistencias}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            // --- BOTÓN VOLVER ---
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = LimeNeon),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                ) {
                    Text("Regresar al Perfil", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun AttributeProgressBar(label: String, value: Int, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("$value", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}
