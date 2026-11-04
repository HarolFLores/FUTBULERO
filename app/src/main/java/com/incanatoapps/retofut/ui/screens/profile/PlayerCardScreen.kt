package com.incanatoapps.retofut.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.CardPerformance
import com.incanatoapps.retofut.data.network.model.CardStats
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse
import com.incanatoapps.retofut.ui.components.FifaCard
import com.incanatoapps.retofut.ui.components.FifaCardShareHelper
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerCardScreen(
    navController: NavController,
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

    // Estados de gamificación y valoración post-partido
    var showRatingDialog by remember { mutableStateOf(false) }
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var currentOutcome by remember { mutableStateOf<LevelUpOutcome?>(null) }

    var selectedStars by remember { mutableIntStateOf(5) }
    var goalsInput by remember { mutableIntStateOf(2) }
    var assistsInput by remember { mutableIntStateOf(1) }
    var defenseInput by remember { mutableIntStateOf(3) }

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

                // Botón Compartir en Header con FileProvider + WhatsApp
                IconButton(
                    onClick = {
                        FifaCardShareHelper.shareFifaCard(
                            context = context,
                            card = cardToDisplay,
                            customMessage = "¡Mira mi Carta Oficial en FUTBULERO! Rating ${cardToDisplay.rating} OVR • ${cardToDisplay.nombre} • ${cardToDisplay.posicion} ⚽🔥"
                        )
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                        .testTag("share_card_header_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir Carta", tint = TextPrimary)
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
            // --- CARTA FIFA 3D CON BOTÓN DE COMPARTIR DIRECTO ---
            item {
                Spacer(Modifier.height(8.dp))
                FifaCard(
                    card = cardToDisplay,
                    onShareClick = {
                        FifaCardShareHelper.shareFifaCard(
                            context = context,
                            card = cardToDisplay,
                            customMessage = "¡Carta FIFA FUTBULERO de ${cardToDisplay.nombre}! Media ${cardToDisplay.rating} OVR ⚽⭐"
                        )
                    }
                )
                Spacer(Modifier.height(14.dp))
            }

            // --- BOTÓN PRINCIPAL COMPARTIR POR WHATSAPP (ACCESO RÁPIDO) ---
            item {
                Button(
                    onClick = {
                        FifaCardShareHelper.shareFifaCard(
                            context = context,
                            card = cardToDisplay,
                            customMessage = "¡Mira mi Carta Oficial de FUTBULERO! Rating ${cardToDisplay.rating} OVR • ${cardToDisplay.nombre} • ${cardToDisplay.posicion} ⚽🔥"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366), // Color WhatsApp oficial
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(48.dp)
                        .testTag("whatsapp_share_cta_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Compartir Carta por WhatsApp", fontWeight = FontWeight.Black, fontSize = 13.5.sp)
                }
                Spacer(Modifier.height(14.dp))
            }

            // --- PANEL DE GAMIFICACIÓN, NIVEL Y EXPERIENCIA (XP) ---
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = DarkForestGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(color = ForestGreen, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            "NIVEL ${viewModel.playerLevel}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "${viewModel.playerXp} XP Acumulada",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Próximo nivel en ${(viewModel.playerLevel + 1) * 500 - viewModel.playerXp} XP",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.5.sp
                                )
                            }

                            // Botón para simular / aplicar valoración post-partido
                            Button(
                                onClick = { showRatingDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("post_match_rating_btn")
                            ) {
                                Icon(Icons.Outlined.MilitaryTech, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Valorar Partido", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Barra de progreso XP hacia el siguiente nivel
                        val xpProgress = ((viewModel.playerXp % 500) / 500f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LimeNeon,
                            trackColor = Color(0xFF14532D)
                        )
                    }
                }
            }

            // --- DETALLES DE RENDIMIENTO / ATRIBUTOS FIFA ---
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "DESGLOSE DE ATRIBUTOS FIFA",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = DarkForestGreen,
                                letterSpacing = 0.5.sp
                            )
                            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    "OVR ${cardToDisplay.rating}",
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
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
                        .padding(horizontal = 20.dp, vertical = 6.dp)
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
                                Text("${cardToDisplay.performance.asistencias} 👟", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
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
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp)
                ) {
                    Text("Regresar al Perfil", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }

    // --- MODAL DE VALORACIÓN POST-PARTIDO ---
    if (showRatingDialog) {
        val estimatedXp = (selectedStars * 200) + (goalsInput * 120) + (assistsInput * 80) + (defenseInput * 60)
        Dialog(onDismissRequest = { showRatingDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Valoración Post-Partido",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = DarkForestGreen
                    )
                    Text(
                        "Califica tu rendimiento en la pichanga para ganar XP, mejorar tus atributos y subir la media FIFA.",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // 1. Selector de 1 a 5 Estrellas
                    Text(
                        "CALIFICACIÓN GENERAL (1 A 5 ESTRELLAS)",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { selectedStars = i }) {
                                Icon(
                                    imageVector = if (i <= selectedStars) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "$i estrellas",
                                    tint = if (i <= selectedStars) Color(0xFFF59E0B) else Color(0xFFD1D5DB),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = when (selectedStars) {
                                1 -> "1 Estrella: Rendimiento Discreto (+200 XP)"
                                2 -> "2 Estrellas: Cumplidor (+400 XP)"
                                3 -> "3 Estrellas: Buen Partido (+600 XP • Sube Media)"
                                4 -> "4 Estrellas: ¡Figura de la Cancha! (+800 XP)"
                                else -> "⭐ 5 Estrellas: ¡MVP IMPARABLE! (+1000 XP • Bonus SHO)"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // 2. Stepper Goles
                    RatingStepperRow(
                        label = "Goles anotados",
                        icon = "⚽",
                        value = goalsInput,
                        impact = "+SHO (Disparo)",
                        onValueChange = { goalsInput = it }
                    )

                    Spacer(Modifier.height(8.dp))

                    // 3. Stepper Asistencias
                    RatingStepperRow(
                        label = "Asistencias clave",
                        icon = "👟",
                        value = assistsInput,
                        impact = "+PAS (Pase)",
                        onValueChange = { assistsInput = it }
                    )

                    Spacer(Modifier.height(8.dp))

                    // 4. Stepper Defensa
                    RatingStepperRow(
                        label = "Recuperaciones defensivas",
                        icon = "🛡️",
                        value = defenseInput,
                        impact = "+DEF & +PHY",
                        onValueChange = { defenseInput = it }
                    )

                    Spacer(Modifier.height(14.dp))

                    // Preview de XP a ganar
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("XP Estimada:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                            Text("+$estimatedXp XP 🚀", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Botones Cancelar / Confirmar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRatingDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val rating = PostMatchRating(
                                    stars = selectedStars,
                                    goals = goalsInput,
                                    assists = assistsInput,
                                    defenseRecoveries = defenseInput
                                )
                                val outcome = viewModel.applyPostMatchRating(rating, cardToDisplay)
                                currentOutcome = outcome
                                showRatingDialog = false
                                showCelebrationDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Subir Nivel", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // --- MODAL DE CELEBRACIÓN DE SUBIDA DE NIVEL Y OVR ---
    if (showCelebrationDialog && currentOutcome != null) {
        val outcome = currentOutcome!!
        Dialog(onDismissRequest = { showCelebrationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(LimeNeon, DarkForestGreen)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 32.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "¡VALORACIÓN REGISTRADA!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB45309),
                        letterSpacing = 1.sp
                    )

                    Text(
                        if (outcome.levelUpAchieved) "¡SUBISTE DE NIVEL Y MEDIA FIFA!" else "¡EXPERIENCIA GANADA!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    // Comparador de Media OVR
                    Surface(
                        color = Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, LimeNeon),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("OVR ANTERIOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Text("${outcome.previousOvr}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }

                            Icon(Icons.Outlined.TrendingUp, null, tint = ForestGreen, modifier = Modifier.size(28.dp))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("NUEVO OVR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${outcome.newOvr}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                    if (outcome.newOvr > outcome.previousOvr) {
                                        Text(" (+${outcome.newOvr - outcome.previousOvr})", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Desglose de mejoras en estadísticas
                    Text(
                        "MEJORAS DE ATRIBUTOS GANADAS:",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        outcome.statBoosts.forEach { (stat, boost) ->
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(horizontal = 3.dp)
                            ) {
                                Text(
                                    "$stat +$boost",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "+${outcome.earnedXp} XP • Nivel de Jugador: ${outcome.newLevel}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )

                    Spacer(Modifier.height(18.dp))

                    // Botón Compartir Nueva Carta en WhatsApp
                    Button(
                        onClick = {
                            FifaCardShareHelper.shareFifaCard(
                                context = context,
                                card = cardToDisplay,
                                customMessage = "¡Acabo de subir de nivel en FUTBULERO! Nueva Media ${cardToDisplay.rating} OVR tras el partido ⚽🔥"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Compartir Nueva Media en WhatsApp", fontWeight = FontWeight.Black, fontSize = 12.5.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showCelebrationDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingStepperRow(
    label: String,
    icon: String,
    value: Int,
    impact: String,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF9FAFB))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(impact, fontSize = 9.5.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
                    .clickable { if (value > 0) onValueChange(value - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }

            Text(
                "$value",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = DarkForestGreen,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(DarkForestGreen)
                    .clickable { onValueChange(value + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
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
