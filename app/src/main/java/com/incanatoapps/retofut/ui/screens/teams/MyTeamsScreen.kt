package com.incanatoapps.retofut.ui.screens.teams

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.Team
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class LocalTeamItem(
    val id: Int,
    val name: String,
    val city: String,
    val format: String,
    val role: String, // "Capitán", "Jugador"
    val isCaptain: Boolean,
    val playersCount: Int,
    val record: String,
    val winRate: String,
    val nextMatch: String,
    val colors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTeamsScreen(navController: NavHostController, viewModel: TeamViewModel = viewModel()) {
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
            viewModel.loadMyTeams(token)
        }
    }

    var selectedFilter by remember { mutableStateOf("Todos") }
    var hasPendingInvite by remember { mutableStateOf(true) }

    val filterOptions = listOf("Todos", "Donde soy Capitán", "Plantillas", "Invitaciones")

    val sampleTeams = remember {
        listOf(
            LocalTeamItem(
                id = 1,
                name = "Los Galácticos FC",
                city = "Pimentel, Chiclayo",
                format = "FÚTBOL 7",
                role = "CAPITÁN / FUNDADOR",
                isCaptain = true,
                playersCount = 12,
                record = "12V - 3E - 2D",
                winRate = "78% Victorias",
                nextMatch = "Sábado 18:00 vs Dep. Pimentel",
                colors = listOf(DarkForestGreen, LimeNeon)
            ),
            LocalTeamItem(
                id = 2,
                name = "Rayo Chiclayano",
                city = "La Victoria, Chiclayo",
                format = "FÚTBOL 6",
                role = "MEDIOCAMPISTA TITULAR",
                isCaptain = false,
                playersCount = 10,
                record = "8V - 2E - 4D",
                winRate = "61% Victorias",
                nextMatch = "Domingo 19:30 vs Sporting",
                colors = listOf(Color(0xFF1E3A8A), Color(0xFF60A5FA))
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = {
            ChocoFulboBottomBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateTeam.route) },
                containerColor = LimeNeon,
                contentColor = DarkForestGreen,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("fab_create_team")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Equipo", modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Crear Equipo", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // --- TOP BAR ---
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
                            Icon(Icons.Outlined.Groups, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "MIS EQUIPOS",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF111827),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Clubes, tácticas y plantillas activas",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Botón nuevo equipo
                    Surface(
                        onClick = { navController.navigate(Screen.CreateTeam.route) },
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LimeNeon),
                        modifier = Modifier.testTag("top_create_team_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, tint = DarkForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nuevo", fontSize = 12.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                        }
                    }
                }
            }

            // --- RESUMEN ESTADÍSTICO DE MIS EQUIPOS ---
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TeamStatSummaryItem("Equipos", "2", Icons.Outlined.Groups)
                        VerticalDivider(Modifier.height(36.dp), color = BorderGray)
                        TeamStatSummaryItem("Jugadores", "22", Icons.Outlined.Person)
                        VerticalDivider(Modifier.height(36.dp), color = BorderGray)
                        TeamStatSummaryItem("Efectividad", "78%", Icons.Outlined.TrendingUp)
                        VerticalDivider(Modifier.height(36.dp), color = BorderGray)
                        TeamStatSummaryItem("Trofeos", "3 🏆", Icons.Outlined.EmojiEvents)
                    }
                }
            }

            // --- BANNER DE INVITACIÓN PENDIENTE (SI LA HAY) ---
            if (hasPendingInvite) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF59E0B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Mail, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "INVITACIÓN DE EQUIPO",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }
                                Text("Hace 2 hrs", fontSize = 10.sp, color = TextSecondary)
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Deportivo Pimentel F7 te invitó a unirte como Delantero a su plantilla oficial.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        hasPendingInvite = false
                                        Toast.makeText(context, "¡Te has unido a Deportivo Pimentel F7!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = LimeNeon),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Text("Aceptar", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        hasPendingInvite = false
                                        Toast.makeText(context, "Invitación rechazada", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Text("Rechazar", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // --- FILTROS DE CATEGORÍA ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) DarkForestGreen else CardWhite,
                            border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) LimeNeon else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // --- LISTA DE EQUIPOS ---
            item {
                Text(
                    "EQUIPOS EN LOS QUE PARTICIPAS",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            items(sampleTeams) { team ->
                ModernTeamCard(
                    team = team,
                    onViewDetails = { navController.navigate("team_detail/${team.id}") },
                    onEditTeam = { navController.navigate("edit_team/${team.id}") },
                    onChallenge = { navController.navigate(Screen.CreateMatch.route) }
                )
            }

            // --- CTA: UNIRSE A OTRO EQUIPO O BUSCAR CONVOCATORIA ---
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CardWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Search, null, tint = DarkForestGreen, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "¿Buscas club o equipo para jugar?",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            "Explora partidos abiertos y clubes que buscan refuerzos en tu posición.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = { navController.navigate(Screen.SearchMatch.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = LimeNeon),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Explorar Partidos & Canchas", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamStatSummaryItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun ModernTeamCard(
    team: LocalTeamItem,
    onViewDetails: () -> Unit,
    onEditTeam: () -> Unit,
    onChallenge: () -> Unit
) {
    Surface(
        onClick = onViewDetails,
        shape = RoundedCornerShape(22.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, if (team.isCaptain) LimeNeon.copy(alpha = 0.8f) else BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("team_card_${team.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Escudo + Nombres + Chip de Rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Escudo geométrico con iniciales
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(team.colors)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        team.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            team.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        "${team.format} • ${team.city}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Rol chip
                Surface(
                    color = if (team.isCaptain) DarkForestGreen else Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (team.isCaptain) "CAPITÁN" else "JUGADOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (team.isCaptain) LimeNeon else TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Squad Preview: Chips de jugadores y total
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                    listOf("PF", "MC", "CF", "CR").forEach { initials ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(DarkForestGreen)
                                .border(1.5.dp, CardWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LimeNeon)
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "${team.playersCount} Jugadores en plantilla",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Estadísticas & Próximo partido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("RECORD", fontSize = 10.sp, color = TextSecondary)
                    Text(team.record, fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
                Column {
                    Text("RENDIMIENTO", fontSize = 10.sp, color = TextSecondary)
                    Text(team.winRate, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF16A34A))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("PRÓXIMO", fontSize = 10.sp, color = TextSecondary)
                    Text(team.nextMatch.take(18) + "...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = LimeNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f).height(42.dp)
                ) {
                    Text("Ver Plantilla", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(14.dp))
                }

                Button(
                    onClick = onChallenge,
                    colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("Retar", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                if (team.isCaptain) {
                    IconButton(
                        onClick = onEditTeam,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                    ) {
                        Icon(Icons.Outlined.Edit, "Editar", tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
