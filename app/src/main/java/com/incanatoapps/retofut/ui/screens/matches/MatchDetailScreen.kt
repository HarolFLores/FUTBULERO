package com.incanatoapps.retofut.ui.screens.matches

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.MatchBet
import com.incanatoapps.retofut.data.network.model.MatchChatMessage
import com.incanatoapps.retofut.data.network.model.MatchPlayerRequest
import com.incanatoapps.retofut.data.network.model.UserRole
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.components.PitchPlayer
import com.incanatoapps.retofut.ui.components.SoccerPitch2D
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class MatchSquadPlayer(
    val id: Int,
    val name: String,
    val number: Int,
    val position: String,
    val isCaptain: Boolean = false,
    val rating: Int = 80,
    val isAnonymous: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(navController: NavHostController, matchId: Int) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest = remember { sessionManager.isGuest() }
    val userRole = remember { sessionManager.fetchUserRole() }
    val isCaptainDefault = remember { sessionManager.isCaptain() || userRole == UserRole.CAPITAN }

    BackHandler { navController.popBackStack() }

    // Control de Pestañas (TabRow)
    // Tab 0: Info y Solicitudes
    // Tab 1: Chat del Partido
    // Tab 2: Apuestas (Pozo y Espectadores)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Info y Solicitudes", "Chat del Partido", "Apuestas")

    // Modo de visualización: Capitán vs Jugador Libre (para probar ambas perspectivas)
    var isCaptainView by remember { mutableStateOf(isCaptainDefault) }

    // Diálogo de solicitud para Jugador Libre
    var showJoinDialog by remember { mutableStateOf(false) }
    var selectedTeamToApply by remember { mutableStateOf("Equipo A: Los Galácticos FC") }
    var selectedPositionToApply by remember { mutableStateOf("DEL (Delantero)") }
    var myPendingApplicationTeam by remember { mutableStateOf<String?>(null) }

    // Plantillas de los dos equipos enfrentados
    val teamAPlayers = remember {
        mutableStateListOf(
            MatchSquadPlayer(1, "Piero Flores", 10, "DEL", isCaptain = true, rating = 87),
            MatchSquadPlayer(2, "Mateo Farfán", 8, "MED", rating = 84),
            MatchSquadPlayer(3, "Carlos Zambrano", 4, "DEF", rating = 85),
            MatchSquadPlayer(4, "Renato Tapia", 6, "MED", rating = 83),
            MatchSquadPlayer(5, "Pedro Gallese", 1, "POR", rating = 86),
            MatchSquadPlayer(6, "Luis Advíncula", 17, "DEF", rating = 84),
            MatchSquadPlayer(7, "Beto 'Pelao' (Invitado)", 11, "DEL", rating = 81, isAnonymous = true)
        )
    }

    val teamBPlayers = remember {
        mutableStateListOf(
            MatchSquadPlayer(101, "Mario 'Tanke' Silva", 9, "DEL", isCaptain = true, rating = 84),
            MatchSquadPlayer(102, "Diego Linares", 1, "POR", rating = 83),
            MatchSquadPlayer(103, "César Morales", 5, "DEF", rating = 82),
            MatchSquadPlayer(104, "Jorge 'Zurdo' V.", 11, "MED", rating = 81),
            MatchSquadPlayer(105, "Fabricio Díaz", 7, "DEL", rating = 82),
            MatchSquadPlayer(106, "Juan Choque (Invitado)", 14, "DEF", rating = 79, isAnonymous = true)
        )
    }

    // Solicitudes de Jugadores Libres con estado PENDIENTE
    val pendingRequests = remember {
        mutableStateListOf(
            MatchPlayerRequest(
                id = 1,
                playerName = "Luis Fernando Rojas",
                position = "MED (MC)",
                rating = 83,
                requestedTeam = "Equipo A: Los Galácticos FC",
                status = "PENDIENTE",
                avatarInitials = "LR"
            ),
            MatchPlayerRequest(
                id = 2,
                playerName = "Cristian 'Kiti' Quispe",
                position = "DEL (DC)",
                rating = 81,
                requestedTeam = "Equipo B: Rayo Chiclayano",
                status = "PENDIENTE",
                avatarInitials = "CQ"
            ),
            MatchPlayerRequest(
                id = 3,
                playerName = "Andrés Meléndez V.",
                position = "DEF (DFC)",
                rating = 80,
                requestedTeam = "Equipo A: Los Galácticos FC",
                status = "PENDIENTE",
                avatarInitials = "AM"
            )
        )
    }

    // Mensajes del Chat grupal del Partido
    val chatMessages = remember {
        mutableStateListOf(
            MatchChatMessage(1L, "Piero Flores (Capitán)", "Capitán", "¡Buenas noches muchachos! Jugamos a las 20:00 hrs en punto. Los de Los Galácticos vamos con camiseta blanca.", "18:30"),
            MatchChatMessage(2L, "Mario Silva (Capitán)", "Capitán", "Rayo Chiclayano ya tiene 5 listos, nos faltan 2 cupos para completar los 7 del Fútbol 7.", "18:42"),
            MatchChatMessage(3L, "Juan Carlos Pérez", "Árbitro", "Buenas noches a ambos equipos. Réferi colegiado confirmado. Llegaré a las 19:45 para revisar nóminas y balones.", "18:50"),
            MatchChatMessage(4L, "Diego Linares", "Jugador", "Llevaré un juego de chalecos naranja por si los colores coinciden.", "19:05")
        )
    }
    var newChatMessage by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Sistema de Apuestas (Pozo entre Equipos & Espectadores)
    var matchPotAmount by remember { mutableDoubleStateOf(100.0) } // S/. 50 cada equipo
    var userBetSelection by remember { mutableStateOf("Equipo A (Los Galácticos)") }
    var userBetAmount by remember { mutableDoubleStateOf(20.0) }
    val matchBets = remember {
        mutableStateListOf(
            MatchBet(1L, "Harold V.", "Equipo A (Los Galácticos)", 20.0, 37.0, "Hace 5 min"),
            MatchBet(2L, "Pedro M.", "Equipo B (Rayo Chiclayano)", 15.0, 31.5, "Hace 12 min"),
            MatchBet(3L, "Carlos T.", "Empate", 10.0, 32.0, "Hace 20 min")
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        bottomBar = { ChocoFulboBottomBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Detalles de Pichanga",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "Fútbol 7 (7 vs 7 • 14 jugadores)",
                        fontSize = 11.sp,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Switch demostrativo: Alternar vista Capitán / Jugador Libre
                Surface(
                    onClick = {
                        isCaptainView = !isCaptainView
                        Toast.makeText(
                            context,
                            if (isCaptainView) "Vista activada: CAPITÁN (Puedes aceptar solicitudes)"
                            else "Vista activada: JUGADOR LIBRE (Puedes solicitar unirte)",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isCaptainView) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                    border = BorderStroke(1.dp, if (isCaptainView) Color(0xFFF59E0B) else Color(0xFF16A34A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isCaptainView) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Person,
                            null,
                            tint = if (isCaptainView) Color(0xFF92400E) else DarkForestGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isCaptainView) "Soy Capitán" else "Jugador Libre",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isCaptainView) Color(0xFF92400E) else DarkForestGreen
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))
            }

            // --- TAB ROW PRINCIPAL DE 3 PESTAÑAS ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = DarkForestGreen,
                divider = { HorizontalDivider(color = BorderGray, thickness = 1.dp) }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) DarkForestGreen else TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (index == 0 && pendingRequests.count { it.status == "PENDIENTE" } > 0) {
                                    Spacer(Modifier.width(4.dp))
                                    Surface(
                                        color = Color(0xFFEF4444),
                                        shape = CircleShape
                                    ) {
                                        Text(
                                            text = "${pendingRequests.count { it.status == "PENDIENTE" }}",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                if (index == 1) {
                                    Spacer(Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(LimeNeon)
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // --- CONTENIDO DE CADA TAB ---
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: INFO Y SOLICITUDES
                    InfoAndRequestsTab(
                        navController = navController,
                        matchId = matchId,
                        teamAPlayers = teamAPlayers,
                        teamBPlayers = teamBPlayers,
                        pendingRequests = pendingRequests,
                        isCaptainView = isCaptainView,
                        isGuest = isGuest,
                        myPendingApplicationTeam = myPendingApplicationTeam,
                        onRequestJoinClick = {
                            if (isGuest) {
                                Toast.makeText(context, "Inicia sesión para solicitar unirte a un equipo", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Login.route)
                            } else {
                                showJoinDialog = true
                            }
                        },
                        onAcceptPlayer = { req ->
                            val updatedReq = req.copy(status = "ACEPTADA")
                            val index = pendingRequests.indexOfFirst { it.id == req.id }
                            if (index != -1) pendingRequests[index] = updatedReq

                            // Agregar a la plantilla del equipo respectivo
                            if (req.requestedTeam.contains("Equipo A")) {
                                teamAPlayers.add(
                                    MatchSquadPlayer(
                                        id = 200 + req.id,
                                        name = req.playerName,
                                        number = (1..99).minus(teamAPlayers.map { it.number }.toSet()).firstOrNull() ?: 18,
                                        position = req.position.take(3),
                                        rating = req.rating
                                    )
                                )
                            } else {
                                teamBPlayers.add(
                                    MatchSquadPlayer(
                                        id = 300 + req.id,
                                        name = req.playerName,
                                        number = (1..99).minus(teamBPlayers.map { it.number }.toSet()).firstOrNull() ?: 19,
                                        position = req.position.take(3),
                                        rating = req.rating
                                    )
                                )
                            }
                            Toast.makeText(context, "¡Jugador ${req.playerName} ACEPTADO en el equipo!", Toast.LENGTH_SHORT).show()
                        },
                        onRejectPlayer = { req ->
                            val updatedReq = req.copy(status = "RECHAZADA")
                            val index = pendingRequests.indexOfFirst { it.id == req.id }
                            if (index != -1) pendingRequests[index] = updatedReq
                            Toast.makeText(context, "Solicitud de ${req.playerName} rechazada", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                1 -> {
                    // TAB 2: CHAT DEL PARTIDO
                    MatchChatTab(
                        messages = chatMessages,
                        messageInput = newChatMessage,
                        onMessageChange = { newChatMessage = it },
                        onSendMessage = {
                            val trimmed = newChatMessage.trim()
                            if (trimmed.isNotEmpty()) {
                                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val newMsg = MatchChatMessage(
                                    id = System.currentTimeMillis(),
                                    senderName = if (isCaptainView) "Tú (Capitán)" else "Tú (Jugador)",
                                    senderRole = if (isCaptainView) "Capitán" else "Jugador",
                                    message = trimmed,
                                    time = timeFormat.format(Date()),
                                    isMe = true
                                )
                                chatMessages.add(newMsg)
                                newChatMessage = ""
                            }
                        },
                        listState = chatListState
                    )
                }
                2 -> {
                    // TAB 3: APUESTAS (POZO Y ESPECTADORES)
                    MatchBetsTab(
                        potAmount = matchPotAmount,
                        userSelection = userBetSelection,
                        onUserSelectionChange = { userBetSelection = it },
                        userBetAmount = userBetAmount,
                        onUserBetAmountChange = { userBetAmount = it },
                        betsList = matchBets,
                        onPlaceBet = {
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val multiplier = when (userBetSelection) {
                                "Equipo A (Los Galácticos)" -> 1.85
                                "Equipo B (Rayo Chiclayano)" -> 2.10
                                else -> 3.20
                            }
                            val potential = userBetAmount * multiplier
                            val newBet = MatchBet(
                                id = System.currentTimeMillis(),
                                bettorName = "Tú (FUTBULERO)",
                                teamChosen = userBetSelection,
                                amount = userBetAmount,
                                potentialWin = potential,
                                time = "Hace un momento (${timeFormat.format(Date())})"
                            )
                            matchBets.add(0, newBet)
                            matchPotAmount += userBetAmount
                            Toast.makeText(
                                context,
                                "¡Apuesta de S/. ${userBetAmount.toInt()} registrada! Ganancia potencial: S/. ${String.format(Locale.US, "%.2f", potential)}",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        onNavigateToBetsHub = { navController.navigate(Screen.BetsHub.route) },
                        onNavigateToPostMatch = { navController.navigate(Screen.PostMatchReview.createRoute(matchId)) }
                    )
                }
            }
        }
    }

    // Modal para que el JUGADOR_LIBRE elija a cuál de los dos equipos unirse
    if (showJoinDialog) {
        Dialog(onDismissRequest = { showJoinDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PersonAddAlt1, null, tint = DarkForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Unirse a un Equipo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = DarkForestGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Como Jugador Libre, selecciona el equipo al que deseas integrarte. Tu solicitud quedará en estado PENDIENTE hasta que el Capitán la apruebe.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "SELECCIONA EQUIPO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))

                    val teams = listOf(
                        Pair("Equipo A: Los Galácticos FC", "${teamAPlayers.size}/7 jugadores • Cap. Piero M."),
                        Pair("Equipo B: Rayo Chiclayano", "${teamBPlayers.size}/7 jugadores • Cap. Mario S.")
                    )

                    teams.forEach { (team, info) ->
                        val isSelected = selectedTeamToApply == team
                        Surface(
                            onClick = { selectedTeamToApply = team },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isSelected) ForestGreen else BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedTeamToApply = team },
                                    colors = RadioButtonDefaults.colors(selectedColor = DarkForestGreen)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(team, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimary)
                                    Text(info, fontSize = 11.sp, color = if (isSelected) ForestGreen else TextSecondary)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "TU POSICIÓN PREFERIDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("DEL", "MED", "DEF", "POR").forEach { pos ->
                            val isPosSelected = selectedPositionToApply.startsWith(pos)
                            Surface(
                                onClick = { selectedPositionToApply = pos },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPosSelected) DarkForestGreen else Color(0xFFF3F4F6),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        pos,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPosSelected) LimeNeon else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showJoinDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val newReq = MatchPlayerRequest(
                                    id = (pendingRequests.maxOfOrNull { it.id } ?: 0) + 1,
                                    playerName = "Tú (Jugador Libre)",
                                    position = selectedPositionToApply,
                                    rating = 82,
                                    requestedTeam = selectedTeamToApply,
                                    status = "PENDIENTE",
                                    avatarInitials = "TÚ"
                                )
                                pendingRequests.add(newReq)
                                myPendingApplicationTeam = selectedTeamToApply
                                showJoinDialog = false
                                Toast.makeText(
                                    context,
                                    "¡Solicitud enviada al Capitán de $selectedTeamToApply! Estado: PENDIENTE ⏳",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Enviar Solicitud", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// TAB 1: INFO Y SOLICITUDES
// =====================================================================
@Composable
private fun InfoAndRequestsTab(
    navController: NavHostController,
    matchId: Int,
    teamAPlayers: List<MatchSquadPlayer>,
    teamBPlayers: List<MatchSquadPlayer>,
    pendingRequests: List<MatchPlayerRequest>,
    isCaptainView: Boolean,
    isGuest: Boolean,
    myPendingApplicationTeam: String?,
    onRequestJoinClick: () -> Unit,
    onAcceptPlayer: (MatchPlayerRequest) -> Unit,
    onRejectPlayer: (MatchPlayerRequest) -> Unit
) {
    val totalConfirmed = teamAPlayers.size + teamBPlayers.size
    val totalCapacity = 14 // Fútbol 7 (7 vs 7)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. HERO BANNER: ENCUENTRO
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F3D24), Color(0xFF052012))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = LimeNeon, shape = RoundedCornerShape(50)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DarkForestGreen))
                                    Spacer(Modifier.width(6.dp))
                                    Text("FÚTBOL 7 • OFICIAL", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                            }
                            Surface(color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(50)) {
                                Text("Cancha 3 Sintética", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        // Enfrentamiento
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                                        Text("LG", fontWeight = FontWeight.Black, color = DarkForestGreen, fontSize = 15.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("Los Galácticos", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("${teamAPlayers.size}/7 listos", color = LimeNeon, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Text("VS", color = LimeNeon, fontSize = 22.sp, fontWeight = FontWeight.Black)

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFF1E3A8A)), contentAlignment = Alignment.Center) {
                                        Text("RC", fontWeight = FontWeight.Black, color = Color.White, fontSize = 15.sp)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("Rayo Chiclayano", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("${teamBPlayers.size}/7 listos", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text("Viernes 28 Feb • 20:00 hrs • Complejo Deportivo El Oasis", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // 2. TARJETA DE ÁRBITRO ASIGNADO
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Sports, null, tint = Color(0xFF7E22CE), modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Juan Carlos Pérez", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Spacer(Modifier.width(6.dp))
                            Surface(color = Color(0xFFF3E8FF), shape = RoundedCornerShape(4.dp)) {
                                Text("ÁRBITRO FEDERADO", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF7E22CE), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("Asoc. de Árbitros Lambayeque • ⭐ 4.9 (48 partidos)", fontSize = 11.sp, color = TextSecondary)
                        Text("Honorario: S/. 35.00 cubierto por cuota compartida", fontSize = 10.5.sp, color = Color(0xFF7E22CE), fontWeight = FontWeight.Medium)
                    }
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                        Text("CONFIRMADO ✓", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                }
            }
        }

        // 3. CANCHA DE FÚTBOL VISUAL 2D (TÁCTICA INTERACTIVA CON JUGADORES REGISTRADOS Y ANÓNIMOS)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PIZARRA TÁCTICA EN CANCHA 2D",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            "FORMATO 7v7",
                            color = ForestGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))

                val pitchPlayersA = remember(teamAPlayers) {
                    teamAPlayers.map { p ->
                        PitchPlayer(
                            id = p.id,
                            name = p.name,
                            number = p.number,
                            position = p.position,
                            rating = p.rating,
                            isCaptain = p.isCaptain,
                            isAnonymous = p.isAnonymous,
                            teamSide = "A"
                        )
                    }
                }
                val pitchPlayersB = remember(teamBPlayers) {
                    teamBPlayers.map { p ->
                        PitchPlayer(
                            id = p.id,
                            name = p.name,
                            number = p.number,
                            position = p.position,
                            rating = p.rating,
                            isCaptain = p.isCaptain,
                            isAnonymous = p.isAnonymous,
                            teamSide = "B"
                        )
                    }
                }

                SoccerPitch2D(
                    teamAPlayers = pitchPlayersA,
                    teamBPlayers = pitchPlayersB,
                    matchFormat = "Fútbol 7 (7 vs 7)",
                    modifier = Modifier.fillMaxWidth(),
                    onPlayerClick = { player ->
                        if (!player.isAnonymous) {
                            navController.navigate("player_card/${player.id}")
                        }
                    }
                )
            }
        }

        // 4. PANEL DEL CAPITÁN: GESTIÓN DE SOLICITUDES PENDIENTES
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCaptainView) Color(0xFFFFFBEB) else Color.White
                ),
                border = BorderStroke(1.2.dp, if (isCaptainView) Color(0xFFF59E0B) else BorderGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.AssignmentInd,
                                null,
                                tint = if (isCaptainView) Color(0xFFB45309) else ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Panel de Solicitudes (Capitán)",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isCaptainView) Color(0xFF78350F) else TextPrimary
                            )
                        }

                        val pendingCount = pendingRequests.count { it.status == "PENDIENTE" }
                        Surface(
                            color = if (pendingCount > 0) Color(0xFFFEF3C7) else Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (pendingCount > 0) Color(0xFFF59E0B) else BorderGray)
                        ) {
                            Text(
                                text = "$pendingCount PENDIENTES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (pendingCount > 0) Color(0xFFB45309) else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isCaptainView)
                            "Como Capitán, revisa a los Jugadores Libres que han solicitado incorporarse al partido y acéptalos o recházalos."
                        else
                            "Aquí el Capitán del equipo revisa y aprueba a los Jugadores Libres que solicitan cupo.",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    if (pendingRequests.isEmpty()) {
                        Text("No hay solicitudes registradas.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            pendingRequests.forEach { req ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        when (req.status) {
                                            "PENDIENTE" -> Color(0xFFF59E0B)
                                            "ACEPTADA" -> Color(0xFF16A34A)
                                            else -> Color(0xFFEF4444)
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (req.status) {
                                                        "PENDIENTE" -> Color(0xFFFEF3C7)
                                                        "ACEPTADA" -> Color(0xFFDCFCE7)
                                                        else -> Color(0xFFFEE2E2)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                req.avatarInitials,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 12.sp,
                                                color = when (req.status) {
                                                    "PENDIENTE" -> Color(0xFFB45309)
                                                    "ACEPTADA" -> DarkForestGreen
                                                    else -> Color(0xFF991B1B)
                                                }
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(req.playerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                                Spacer(Modifier.width(6.dp))
                                                Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                                    Text(req.position, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }
                                            Text("Postula a: ${req.requestedTeam}", fontSize = 11.sp, color = TextSecondary)
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    color = when (req.status) {
                                                        "PENDIENTE" -> Color(0xFFFEF3C7)
                                                        "ACEPTADA" -> Color(0xFFDCFCE7)
                                                        else -> Color(0xFFFEE2E2)
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        "ESTADO: ${req.status}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = when (req.status) {
                                                            "PENDIENTE" -> Color(0xFFB45309)
                                                            "ACEPTADA" -> ForestGreen
                                                            else -> Color(0xFFDC2626)
                                                        },
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Spacer(Modifier.width(6.dp))
                                                Text("${req.rating} OVR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                            }
                                        }

                                        // Botones de Aceptar / Rechazar para el Capitán
                                        if (req.status == "PENDIENTE") {
                                            if (isCaptainView) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    IconButton(
                                                        onClick = { onRejectPlayer(req) },
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFFEE2E2))
                                                    ) {
                                                        Icon(Icons.Outlined.Close, "Rechazar", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                                    }
                                                    IconButton(
                                                        onClick = { onAcceptPlayer(req) },
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFDCFCE7))
                                                    ) {
                                                        Icon(Icons.Outlined.Check, "Aceptar", tint = ForestGreen, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            } else {
                                                Text("Esperando Cap.", fontSize = 10.sp, color = TextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. PLANTILLAS CONFIRMADAS POR EQUIPO
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    text = "PLANTILLAS Y ALINEACIONES ($totalConfirmed / $totalCapacity)",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(8.dp))

                // Equipo A
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Equipo A: Los Galácticos FC (${teamAPlayers.size}/7)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkForestGreen)
                            Surface(color = DarkForestGreen, shape = RoundedCornerShape(6.dp)) {
                                Text("LOCAL", color = LimeNeon, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        teamAPlayers.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (!p.isAnonymous) {
                                            navController.navigate("player_card/${p.id}")
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${p.number}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DarkForestGreen, modifier = Modifier.width(26.dp))
                                    Text(p.name, fontSize = 12.5.sp, color = TextPrimary, fontWeight = if (p.isCaptain) FontWeight.Bold else FontWeight.Normal)
                                    if (p.isCaptain) {
                                        Spacer(Modifier.width(4.dp))
                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(3.dp)) {
                                            Text("CAPITÁN", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 3.dp))
                                        }
                                    }
                                    if (p.isAnonymous) {
                                        Spacer(Modifier.width(4.dp))
                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(3.dp)) {
                                            Text("INVITADO", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 3.dp))
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${p.position} • ${p.rating} OVR", fontSize = 11.sp, color = TextSecondary)
                                    if (!p.isAnonymous) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ver Carta", tint = DarkForestGreen, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Equipo B
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Equipo B: Rayo Chiclayano (${teamBPlayers.size}/7)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                            Surface(color = Color(0xFF1E3A8A), shape = RoundedCornerShape(6.dp)) {
                                Text("RIVAL", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        teamBPlayers.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        if (!p.isAnonymous) {
                                            navController.navigate("player_card/${p.id}")
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${p.number}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A), modifier = Modifier.width(26.dp))
                                    Text(p.name, fontSize = 12.5.sp, color = TextPrimary, fontWeight = if (p.isCaptain) FontWeight.Bold else FontWeight.Normal)
                                    if (p.isCaptain) {
                                        Spacer(Modifier.width(4.dp))
                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(3.dp)) {
                                            Text("CAPITÁN", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 3.dp))
                                        }
                                    }
                                    if (p.isAnonymous) {
                                        Spacer(Modifier.width(4.dp))
                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(3.dp)) {
                                            Text("INVITADO", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 3.dp))
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${p.position} • ${p.rating} OVR", fontSize = 11.sp, color = TextSecondary)
                                    if (!p.isAnonymous) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ver Carta", tint = Color(0xFF1E3A8A), modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. BOTÓN DE SOLICITUD PARA JUGADOR LIBRE
        item {
            Spacer(Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CUOTA POR JUGADOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Text("S/. 12.00", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }

                        if (myPendingApplicationTeam != null) {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                            ) {
                                Text(
                                    "Solicitud Pendiente en $myPendingApplicationTeam ⏳",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onRequestJoinClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("apply_to_team_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.SportsSoccer, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Solicitar Unirme a un Equipo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// TAB 2: CHAT DEL PARTIDO
// =====================================================================
@Composable
private fun MatchChatTab(
    messages: List<MatchChatMessage>,
    messageInput: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado de la Sala
        Surface(
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ForestGreen)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Chat de Coordinación • Exclusivo para participantes del partido",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen
                )
            }
        }

        // Lista de Mensajes
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.isMe
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    // Remitente y Hora
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
                    ) {
                        Text(
                            text = msg.senderName,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMe) ForestGreen else TextSecondary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = msg.time,
                            fontSize = 9.5.sp,
                            color = TextSecondary
                        )
                    }

                    // Burbuja
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        color = if (isMe) DarkForestGreen else Color.White,
                        border = BorderStroke(1.dp, if (isMe) DarkForestGreen else BorderGray),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.message,
                            fontSize = 13.sp,
                            color = if (isMe) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Barra inferior para escribir
        Surface(
            color = Color.White,
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = onMessageChange,
                    placeholder = { Text("Escribe un mensaje al grupo...", fontSize = 13.sp, color = TextSecondary) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("match_chat_input"),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = Color(0xFFF9FAFB),
                        unfocusedContainerColor = Color(0xFFF9FAFB)
                    )
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (messageInput.isNotBlank()) DarkForestGreen else Color(0xFFE5E7EB))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar mensaje",
                        tint = if (messageInput.isNotBlank()) LimeNeon else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// =====================================================================
// TAB 3: APUESTAS (POZO Y ESPECTADORES)
// =====================================================================
@Composable
private fun MatchBetsTab(
    potAmount: Double,
    userSelection: String,
    onUserSelectionChange: (String) -> Unit,
    userBetAmount: Double,
    onUserBetAmountChange: (Double) -> Unit,
    betsList: List<MatchBet>,
    onPlaceBet: () -> Unit,
    onNavigateToBetsHub: () -> Unit = {},
    onNavigateToPostMatch: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. POZO DEL PARTIDO (ENTRE EQUIPOS)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1B4D31)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.EmojiEvents, null, tint = LimeNeon, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("POZO DEL PARTIDO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LimeNeon, letterSpacing = 0.5.sp)
                                Text("Pacto entre Capitanes", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }

                        Surface(
                            color = Color(0xFF143823),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, LimeNeon.copy(alpha = 0.6f))
                        ) {
                            Text(
                                "EN CUSTODIA 🔒",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = LimeNeon,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Total Acumulado", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                            Text(
                                "S/. ${potAmount.toInt()}.00",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "S/. 50.00 Galácticos\nS/. 50.00 Rayo Chicl.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 0.8.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "🏆 El equipo ganador de los 60 minutos se lleva el pozo íntegro para cubrir la cancha y el festejo del tercer tiempo.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. APUESTAS DE ESPECTADORES Y COMUNIDAD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRONÓSTICO Y APUESTAS DE ESPECTADORES",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Apuesta a tu equipo favorito de Chiclayo y multiplica tu saldo:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(12.dp))

                    // Cuotas 1X2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Equipo A (Los Galácticos)", "Gana Galácticos", "x 1.85"),
                            Triple("Empate", "Empate", "x 3.20"),
                            Triple("Equipo B (Rayo Chiclayano)", "Gana Rayo Ch.", "x 2.10")
                        ).forEach { (id, label, multiplier) ->
                            val isSelected = userSelection == id
                            Surface(
                                onClick = { onUserSelectionChange(id) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) DarkForestGreen else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (isSelected) LimeNeon else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = multiplier,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) LimeNeon else DarkForestGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Chips de Monto
                    Text("MONTO A APOSTAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(5.0, 10.0, 20.0, 50.0).forEach { amount ->
                            val isSelected = userBetAmount == amount
                            Surface(
                                onClick = { onUserBetAmountChange(amount) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) LimeNeon else Color(0xFFF3F4F6),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "S/. ${amount.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) DarkForestGreen else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Resumen de Ganancia Potencial
                    val currentMultiplier = when (userSelection) {
                        "Equipo A (Los Galácticos)" -> 1.85
                        "Equipo B (Rayo Chiclayano)" -> 2.10
                        else -> 3.20
                    }
                    val potential = userBetAmount * currentMultiplier

                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ganancia Potencial:", fontSize = 12.sp, color = DarkForestGreen, fontWeight = FontWeight.Medium)
                            Text(
                                "S/. ${String.format(Locale.US, "%.2f", potential)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onPlaceBet,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_match_bet_button")
                    ) {
                        Icon(Icons.Outlined.MonetizationOn, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Apostar S/. ${userBetAmount.toInt()}.00 con Yape/Saldo",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 3. APUESTAS RECIENTES DE LA COMUNIDAD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "APUESTAS RECIENTES DEL PARTIDO",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    betsList.forEach { bet ->
                        Surface(
                            color = Color(0xFFFAFAFA),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(bet.bettorName, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                    Text("Apostó a: ${bet.teamChosen}", fontSize = 11.sp, color = TextSecondary)
                                    Text(bet.time, fontSize = 9.5.sp, color = TextSecondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "S/. ${bet.amount.toInt()}.00",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = DarkForestGreen
                                    )
                                    Text(
                                        "Gana S/. ${String.format(Locale.US, "%.2f", bet.potentialWin)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. ACCESO A TODAS LAS QUINIELAS Y ACTA FINAL
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBetsHub() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.2.dp, ForestGreen)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.MonetizationOn, null, tint = LimeNeon, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Gran Pozo y Quinielas Chiclayo",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = DarkForestGreen
                        )
                        Text(
                            "Pozo acumulado S/ 1,250.00 • 6 cuotas activas hoy",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onNavigateToPostMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Icon(Icons.Outlined.MilitaryTech, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Ver Acta Final y Calificación del Partido",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
