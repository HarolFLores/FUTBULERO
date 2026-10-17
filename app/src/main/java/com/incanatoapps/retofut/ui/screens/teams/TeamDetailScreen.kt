package com.incanatoapps.retofut.ui.screens.teams

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class DetailPlayer(
    val id: Int,
    val name: String,
    val number: Int,
    val position: String,
    val rating: Int,
    val isCaptain: Boolean,
    val goals: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    navController: NavHostController,
    teamId: Int,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }
    val currentUserId = remember { sessionManager.fetchUserId() }

    BackHandler { navController.popBackStack() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Plantilla, 1: Partidos, 2: Estadísticas
    var showInviteDialog by remember { mutableStateOf(false) }
    var invitePlayerEmail by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val teamName = if (teamId == 1) "Los Galácticos FC" else "Rayo Chiclayano"
    val teamFormat = if (teamId == 1) "FÚTBOL 7" else "FÚTBOL 6"
    val teamCity = if (teamId == 1) "Pimentel, Chiclayo" else "La Victoria, Chiclayo"

    val roster = remember {
        listOf(
            DetailPlayer(1, "Piero Flores", 10, "DEL (DC)", 87, true, 14),
            DetailPlayer(2, "Mateo Farfán", 8, "MED (MC)", 84, false, 6),
            DetailPlayer(3, "Christian Cueva Jr.", 7, "MED (EXT)", 82, false, 8),
            DetailPlayer(4, "Carlos Zambrano R.", 4, "DEF (DFC)", 85, false, 2),
            DetailPlayer(5, "Renato Tapia S.", 6, "MED (MCD)", 83, false, 3),
            DetailPlayer(6, "Pedro Gallese Q.", 1, "POR (GK)", 86, false, 0),
            DetailPlayer(7, "Luis Advíncula B.", 17, "DEF (LD)", 84, false, 4),
            DetailPlayer(8, "Miguel Trauco O.", 22, "DEF (LI)", 80, false, 1)
        )
    }

    if (showInviteDialog) {
        Dialog(onDismissRequest = { showInviteDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(54.dp).clip(CircleShape).background(BrandGreenSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, null, tint = DarkForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Convocar Jugador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Envía una invitación para unirse a $teamName",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = invitePlayerEmail,
                        onValueChange = { invitePlayerEmail = it },
                        placeholder = { Text("Correo o teléfono del jugador") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showInviteDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                showInviteDialog = false
                                Toast.makeText(context, "¡Convocatoria enviada exitosamente!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Enviar", fontWeight = FontWeight.Black)
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
            // --- TOP APP BAR ---
            item {
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
                        "DETALLE DE EQUIPO",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )

                    IconButton(
                        onClick = { navController.navigate("edit_team/$teamId") },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = TextPrimary)
                    }
                }
            }

            // --- HERO BANNER EQUIPO ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DarkForestGreen, Color(0xFF144728))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Escudo
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(CardWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    teamName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = DarkForestGreen
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    teamName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    "$teamFormat • $teamCity",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "CAPITÁN: PIERO FLORES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // Métricas del equipo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(vertical = 10.dp, horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("JUGADORES", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("${roster.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("VICTORIAS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("12V", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TORNEOS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("3 🏆", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MEDIA OVR", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("84", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Botones de acción rápida
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showInviteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Convocar", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { navController.navigate(Screen.CreateMatch.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Outlined.SportsSoccer, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Retar a Partido", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- TABS (PLANTILLA, PARTIDOS, ESTADÍSTICAS) ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Plantilla (${roster.size})", "Partidos Recientes", "Tácticas").forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DarkForestGreen else Color(0xFFF3F4F6),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) LimeNeon else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- CONTENIDO DE PLANTILLA ---
            if (selectedTab == 0) {
                item {
                    Text(
                        "JUGADORES INSCRITOS EN EL CLUB",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                items(roster) { player ->
                    Surface(
                        onClick = { navController.navigate("player_card/${player.id}") },
                        shape = RoundedCornerShape(16.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, if (player.isCaptain) LimeNeon else BorderGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .testTag("player_roster_row_${player.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Dorsal
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "#${player.number}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = LimeNeon
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        player.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    if (player.isCaptain) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                "C",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = LimeNeon,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    "${player.position} • ${player.goals} Goles esta temporada",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            // Rating FIFA Badge
                            Surface(
                                color = if (player.rating >= 85) Color(0xFFFEF08A) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (player.rating >= 85) Color(0xFFEAB308) else BorderGray)
                            ) {
                                Text(
                                    "${player.rating} OVR",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (player.rating >= 85) Color(0xFF854D0E) else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // PARTIDOS
                items(listOf("Victoria 5 - 2 vs Dep. Pimentel", "Victoria 4 - 3 vs Rayo Chiclayano", "Empate 2 - 2 vs Sporting La Victoria")) { res ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF22C55E))
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(res, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            }
                            Text("Oficial F7", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            } else {
                // TÁCTICAS
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("FORMACIÓN PREDILECTA: 1 - 2 - 3 - 1 (F7)", fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarkForestGreen)
                            Spacer(Modifier.height(8.dp))
                            Text("Portero: Pedro Gallese", fontSize = 12.sp, color = TextPrimary)
                            Text("Defensas: Carlos Zambrano, Miguel Trauco", fontSize = 12.sp, color = TextPrimary)
                            Text("Medios: Renato Tapia, Mateo Farfán, Christian Cueva Jr.", fontSize = 12.sp, color = TextPrimary)
                            Text("Delantero: Piero Flores (#10)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                        }
                    }
                }
            }
        }
    }
}
