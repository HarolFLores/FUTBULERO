package com.incanatoapps.retofut.ui.screens.rankings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardState
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardViewModel
import com.incanatoapps.retofut.ui.theme.*

data class StandingsTeam(
    val pos: Int,
    val id: Int,
    val name: String,
    val matches: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalDiff: Int,
    val points: Int,
    val form: List<String> = listOf("W", "W", "D", "W", "L")
)

data class FixtureMatch(
    val id: Int,
    val round: String,
    val date: String,
    val time: String,
    val pitch: String,
    val teamA: String,
    val teamB: String,
    val scoreA: Int? = null,
    val scoreB: Int? = null,
    val status: String // "FINALIZADO", "EN VIVO", "PROGRAMADO"
)

data class TopScorer(
    val id: Int,
    val rank: Int,
    val name: String,
    val team: String,
    val goals: Int,
    val matches: Int,
    val assists: Int,
    val avatarUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingsScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }
    val userName = remember { sessionManager.fetchUserName() ?: "Piero" }

    BackHandler {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }

    LaunchedEffect(token) {
        if (token != null && token != "expo_guest_token") {
            viewModel.loadDashboardData(token)
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tabla, 1: Fixture, 2: Goleadores, 3: Mis Torneos
    var selectedTournament by remember { mutableStateOf("Copa Chiclayo F7 2026") }
    var showInscribeDialog by remember { mutableStateOf(false) }
    var selectedTeamToInscribe by remember { mutableStateOf("Los Galácticos FC") }

    val tabs = listOf("Tabla de Posiciones", "Fixture & Rondas", "Goleadores", "Mis Torneos")

    // Standings data
    val defaultStandings = remember {
        listOf(
            StandingsTeam(1, 1, "Los Galácticos FC", 7, 6, 1, 0, 16, 19, listOf("W", "W", "W", "D", "W")),
            StandingsTeam(2, 2, "Deportivo Pimentel", 7, 5, 2, 0, 12, 17, listOf("W", "D", "W", "W", "D")),
            StandingsTeam(3, 3, "Rayo Chiclayano", 7, 4, 1, 2, 8, 13, listOf("W", "L", "W", "W", "D")),
            StandingsTeam(4, 4, "Sporting La Victoria", 7, 4, 0, 3, 4, 12, listOf("L", "W", "W", "L", "W")),
            StandingsTeam(5, 5, "Atlético San José", 7, 3, 1, 3, 1, 10, listOf("D", "W", "L", "W", "L")),
            StandingsTeam(6, 6, "Real Lambayeque F7", 7, 2, 2, 3, -2, 8, listOf("L", "D", "L", "W", "D")),
            StandingsTeam(7, 7, "Choco Boys Pimentel", 7, 1, 1, 5, -9, 4, listOf("L", "L", "D", "L", "L")),
            StandingsTeam(8, 8, "Cachorros Santa Victoria", 7, 0, 0, 7, -30, 0, listOf("L", "L", "L", "L", "L"))
        )
    }

    // Fixture matches data
    val defaultFixtures = remember {
        listOf(
            FixtureMatch(1, "Fecha 8 • Clausura", "Sábado 29 Feb", "18:00 hrs", "Cancha El Faro 1", "Los Galácticos FC", "Deportivo Pimentel", 2, 2, "EN VIVO"),
            FixtureMatch(2, "Fecha 8 • Clausura", "Sábado 29 Feb", "19:15 hrs", "Cancha El Faro 2", "Rayo Chiclayano", "Sporting La Victoria", null, null, "PROGRAMADO"),
            FixtureMatch(3, "Fecha 8 • Clausura", "Sábado 29 Feb", "20:30 hrs", "Complejo San Juan", "Atlético San José", "Real Lambayeque F7", null, null, "PROGRAMADO"),
            FixtureMatch(4, "Fecha 7 • Cuartos", "Viernes 28 Feb", "19:00 hrs", "El Oasis - Pimentel", "Los Galácticos FC", "Choco Boys Pimentel", 5, 1, "FINALIZADO"),
            FixtureMatch(5, "Fecha 7 • Cuartos", "Viernes 28 Feb", "20:30 hrs", "El Oasis - Pimentel", "Deportivo Pimentel", "Atlético San José", 3, 2, "FINALIZADO")
        )
    }

    // Top scorers data
    val defaultScorers = remember {
        listOf(
            TopScorer(1, 1, "Piero Flores", "Los Galácticos FC", 14, 7, 6),
            TopScorer(2, 2, "Christian Cueva Jr.", "Deportivo Pimentel", 11, 7, 4),
            TopScorer(3, 3, "Mateo Farfán", "Rayo Chiclayano", 9, 6, 3),
            TopScorer(4, 4, "Raúl Ruidíaz G.", "Sporting La Victoria", 8, 7, 2),
            TopScorer(5, 5, "Alexis Sánchez", "Atlético San José", 7, 7, 5),
            TopScorer(6, 6, "Gianluca Lapadula", "Real Lambayeque F7", 6, 6, 1)
        )
    }

    // Dialogo de Inscripción a Torneo
    if (showInscribeDialog) {
        Dialog(onDismissRequest = { showInscribeDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BrandGreenSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Inscribir Equipo al Torneo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Torneo: $selectedTournament\nCosto de inscripción: S/. 150.00 (Incluye arbitraje y trofeos)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(18.dp))
                    Text(
                        "SELECCIONA TU EQUIPO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))

                    listOf("Los Galácticos FC", "Rayo Chiclayano").forEach { teamName ->
                        val isSelected = selectedTeamToInscribe == teamName
                        Surface(
                            onClick = { selectedTeamToInscribe = teamName },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isSelected) DarkForestGreen else BorderGray),
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
                                    onClick = { selectedTeamToInscribe = teamName },
                                    colors = RadioButtonDefaults.colors(selectedColor = DarkForestGreen)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(teamName, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showInscribeDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                showInscribeDialog = false
                                Toast.makeText(
                                    context,
                                    "¡$selectedTeamToInscribe inscrito con éxito en $selectedTournament!",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirmar", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = {
            ChocoFulboBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // --- TOP HEADER ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                            .background(DarkForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "TORNEOS & COPAS",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF111827),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Ligas y posiciones oficiales • Chiclayo",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Botón inscribir
                    Surface(
                        onClick = { showInscribeDialog = true },
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LimeNeon),
                        modifier = Modifier.testTag("inscribe_team_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, tint = DarkForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Inscribir", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                        }
                    }
                }
            }

            // --- HERO TOURNAMENT BANNER ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DarkForestGreen, Color(0xFF1B4D2E))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = LimeNeon,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "TORNEO OFICIAL EN CURSO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text("8ª Fecha", color = Color(0xFFD1FAE5), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    selectedTournament,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Premio: S/. 3,000 en efectivo + Copa ChocoFulbo",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚽ Fútbol 7", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                    Text("•", color = Color.White.copy(alpha = 0.5f))
                                    Text("🏟 Sintético Pimentel", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                    Text("•", color = Color.White.copy(alpha = 0.5f))
                                    Text("👥 16 Equipos", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = LimeNeon,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showInscribeDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Icon(Icons.Default.GroupAdd, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Inscribir mi Equipo", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Reglamento oficial descargado", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Text("Reglamento", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- SELECTOR DE TORNEO (PILLS) ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tournamentList = listOf("Copa Chiclayo F7 2026", "Liga Nocturna Pimentel", "Torneo Relámpago F6")
                    tournamentList.forEach { tourn ->
                        val isSelected = selectedTournament == tourn
                        Surface(
                            onClick = { selectedTournament = tourn },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) DarkForestGreen else CardWhite,
                            border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tourn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) LimeNeon else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // --- TABS INTERNAS (TABLA, FIXTURE, GOLEADORES, MIS TORNEOS) ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LimeNeon else Color(0xFFF3F4F6),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("tournament_tab_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (index) {
                                    0 -> Icons.Outlined.FormatListNumbered
                                    1 -> Icons.Outlined.CalendarMonth
                                    2 -> Icons.Outlined.SportsSoccer
                                    else -> Icons.Outlined.Shield
                                }
                                Icon(
                                    icon,
                                    null,
                                    tint = if (isSelected) DarkForestGreen else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) DarkForestGreen else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- CONTENIDO SEGÚN LA PESTAÑA SELECCIONADA ---
            when (selectedTab) {
                0 -> {
                    // TABLA DE POSICIONES
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                            // Cabecera de la tabla
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkForestGreen,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LimeNeon, modifier = Modifier.width(24.dp))
                                    Text("EQUIPO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.weight(1f))
                                    Text("PJ", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                                    Text("G", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    Text("E", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    Text("P", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    Text("DG", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center)
                                    Text("PTS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = LimeNeon, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }

                    items(defaultStandings) { team ->
                        val isUserTeam = team.name.contains("Galácticos")
                        Surface(
                            onClick = { navController.navigate("team_detail/${team.id}") },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isUserTeam) Color(0xFFF0FDF4) else CardWhite,
                            border = BorderStroke(
                                1.dp,
                                if (isUserTeam) LimeNeon else BorderGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                                .testTag("team_standing_row_${team.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Posición
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (team.pos) {
                                                1 -> Color(0xFFFDE047) // Oro
                                                2 -> Color(0xFFE2E8F0) // Plata
                                                3 -> Color(0xFFFDBA74) // Bronce
                                                else -> Color(0xFFF3F4F6)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        team.pos.toString(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (team.pos in 1..3) Color(0xFF1E293B) else TextSecondary
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                // Nombre equipo y forma
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            team.name,
                                            fontWeight = if (isUserTeam) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isUserTeam) DarkForestGreen else TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isUserTeam) {
                                            Spacer(Modifier.width(6.dp))
                                            Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                                Text(
                                                    "MI EQUIPO",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = LimeNeon,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(top = 2.dp)) {
                                        team.form.takeLast(4).forEach { res ->
                                            val badgeColor = when (res) {
                                                "W" -> Color(0xFF22C55E)
                                                "D" -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(badgeColor)
                                            )
                                        }
                                    }
                                }

                                // Estadísticas
                                Text(team.matches.toString(), fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                                Text(team.won.toString(), fontSize = 12.sp, color = TextPrimary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                Text(team.drawn.toString(), fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                Text(team.lost.toString(), fontSize = 12.sp, color = TextSecondary, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                Text(
                                    (if (team.goalDiff > 0) "+${team.goalDiff}" else team.goalDiff.toString()),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (team.goalDiff >= 0) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    modifier = Modifier.width(32.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    team.points.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            color = Color(0xFFF9FAFB),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                                    Spacer(Modifier.width(6.dp))
                                    Text("1-4 Semifinales Copa Oro", fontSize = 10.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFE2E8F0)))
                                    Spacer(Modifier.width(6.dp))
                                    Text("5-8 Copa Plata", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // FIXTURE & RONDAS
                    item {
                        Text(
                            "PARTIDOS & JORNADAS",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }

                    items(defaultFixtures) { fix ->
                        Surface(
                            onClick = {
                                if (fix.status == "EN VIVO") {
                                    navController.navigate("live_match/${fix.id}")
                                } else {
                                    navController.navigate("match_detail/${fix.id}")
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            color = CardWhite,
                            border = BorderStroke(
                                1.dp,
                                if (fix.status == "EN VIVO") LimeNeon else BorderGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .testTag("fixture_match_${fix.id}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${fix.round} • ${fix.date}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Surface(
                                        color = when (fix.status) {
                                            "EN VIVO" -> Color(0xFFEF4444)
                                            "FINALIZADO" -> Color(0xFFE5E7EB)
                                            else -> DarkForestGreen
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            fix.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when (fix.status) {
                                                "EN VIVO" -> Color.White
                                                "FINALIZADO" -> TextPrimary
                                                else -> LimeNeon
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Equipo A
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(DarkForestGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                fix.teamA.take(2).uppercase(),
                                                color = LimeNeon,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            fix.teamA,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // Marcador o VS
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF3F4F6))
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (fix.scoreA != null && fix.scoreB != null) {
                                            Text(
                                                "${fix.scoreA} - ${fix.scoreB}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = if (fix.status == "EN VIVO") Color(0xFFDC2626) else DarkForestGreen
                                            )
                                        } else {
                                            Text(
                                                fix.time,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = TextPrimary
                                            )
                                        }
                                    }

                                    // Equipo B
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            fix.teamB,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.End
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1E293B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                fix.teamB.take(2).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Place, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(fix.pitch, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TOP GOLEADORES
                    item {
                        // Podio de los 3 mejores goleadores
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // #2 Plata
                            PodiumCard(
                                scorer = defaultScorers[1],
                                rank = 2,
                                height = 150.dp,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.weight(1f),
                                onPlayerClick = { navController.navigate("player_card/${defaultScorers[1].id}") }
                            )

                            // #1 Oro
                            PodiumCard(
                                scorer = defaultScorers[0],
                                rank = 1,
                                height = 175.dp,
                                color = Color(0xFFFDE047),
                                modifier = Modifier.weight(1.1f),
                                onPlayerClick = { navController.navigate("player_card/${defaultScorers[0].id}") }
                            )

                            // #3 Bronce
                            PodiumCard(
                                scorer = defaultScorers[2],
                                rank = 3,
                                height = 140.dp,
                                color = Color(0xFFFDBA74),
                                modifier = Modifier.weight(1f),
                                onPlayerClick = { navController.navigate("player_card/${defaultScorers[2].id}") }
                            )
                        }
                    }

                    item {
                        Text(
                            "TABLA DE GOLEADORES COMPLETA",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }

                    items(defaultScorers) { scorer ->
                        Surface(
                            onClick = { navController.navigate("player_card/${scorer.id}") },
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "#${scorer.rank}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (scorer.rank <= 3) DarkForestGreen else TextSecondary,
                                    modifier = Modifier.width(28.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DarkForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Person, null, tint = LimeNeon, modifier = Modifier.size(20.dp))
                                }

                                Spacer(Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        scorer.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        scorer.team,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "${scorer.goals}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = DarkForestGreen
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("⚽", fontSize = 12.sp)
                                    }
                                    Text(
                                        "${scorer.matches} PJ • ${(scorer.goals.toFloat() / scorer.matches).let { "%.1f".format(it) }} p/p",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // MIS TORNEOS
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                "TUS PARTICIPACIONES ACTIVAS",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(10.dp))

                            // Card de Los Galácticos FC en la Copa Chiclayo
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CardWhite,
                                border = BorderStroke(1.5.dp, LimeNeon),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(color = DarkForestGreen, shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                "FASE DE GRUPOS • 1° LUGAR",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = LimeNeon,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text("Copa Chiclayo F7", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                    }

                                    Spacer(Modifier.height(12.dp))
                                    Text("Los Galácticos FC", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Capitán: Piero Flores • 12 Jugadores", fontSize = 12.sp, color = TextSecondary)

                                    Spacer(Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("PUNTOS", fontSize = 10.sp, color = TextSecondary)
                                            Text("19", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                        }
                                        Column {
                                            Text("RECORD", fontSize = 10.sp, color = TextSecondary)
                                            Text("6G - 1E - 0P", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        }
                                        Column {
                                            Text("PRÓXIMO RIVAL", fontSize = 10.sp, color = TextSecondary)
                                            Text("Dep. Pimentel", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))
                                    Button(
                                        onClick = { navController.navigate("team_detail/1") },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = LimeNeon),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Administrar Equipo & Plantilla", fontWeight = FontWeight.Black)
                                        Spacer(Modifier.width(8.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
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

@Composable
private fun PodiumCard(
    scorer: TopScorer,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier,
    onPlayerClick: () -> Unit
) {
    Surface(
        onClick = onPlayerClick,
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        border = BorderStroke(1.5.dp, color),
        modifier = modifier.height(height)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rank.toString(),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    scorer.name.split(" ").firstOrNull() ?: scorer.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    scorer.team.take(12),
                    fontSize = 9.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Surface(
                color = DarkForestGreen,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "${scorer.goals} Goles",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = LimeNeon,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
