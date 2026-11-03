package com.incanatoapps.retofut.ui.screens.tournaments

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class StandingsRow(
    val pos: Int,
    val teamName: String,
    val district: String,
    val initials: String,
    val pj: Int,
    val pg: Int,
    val pe: Int,
    val pp: Int,
    val dg: Int,
    val pts: Int,
    val isCurrentUserTeam: Boolean = false,
    val isQualified: Boolean = false,
    val isRelegation: Boolean = false
)

data class ScorerItem(
    val pos: Int,
    val name: String,
    val team: String,
    val goals: Int,
    val matches: Int
)

data class FairPlayItem(
    val pos: Int,
    val team: String,
    val yellowCards: Int,
    val redCards: Int,
    val score: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    navController: NavHostController,
    viewModel: TournamentsViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest = remember { sessionManager.isGuest() }
    val userName = remember { sessionManager.fetchUserName() ?: "Piero Mendoza" }

    BackHandler {
        if (viewModel.selectedTournament != null) {
            viewModel.selectedTournament = null
        } else {
            navController.popBackStack()
        }
    }

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Tabla, 1: Goleadores, 2: Fair Play
    var selectedFilterCategory by remember { mutableStateOf("Todos") } // Todos, Cerca de mí, Creados por mí
    var selectedCategoryDropdown by remember { mutableStateOf("Grupo A • Fútbol 7") }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Formulario de creación de torneo
    var newTourneyName by remember { mutableStateOf("Copa Chiclayo Verano 2026") }
    var newTourneyRole by remember { mutableStateOf(if (sessionManager.isFieldAdmin()) "ADMIN_CANCHA" else "CAPITAN") }
    var newTourneyOrganizerName by remember { mutableStateOf(if (newTourneyRole == "ADMIN_CANCHA") "Complejo El Oasis" else userName) }
    var newTourneyFormat by remember { mutableStateOf(TournamentFormat.RELAMPAGO) }
    var newTourneyGameFormat by remember { mutableStateOf("Fútbol 7") }
    var newTourneyVenue by remember { mutableStateOf("Complejo Deportivo El Oasis") }
    var newTourneyDate by remember { mutableStateOf("Sábado 31 Octubre • 18:00 hrs") }
    var newTourneyFee by remember { mutableStateOf("120") }
    var newTourneyPrize by remember { mutableStateOf("S/. 1,500 en efectivo + Trofeo") }
    var newTourneyMaxTeams by remember { mutableIntStateOf(8) }

    val standingsList = remember {
        listOf(
            StandingsRow(1, "Sporting Reque", "Reque, Chiclayo", "SR", 10, 7, 3, 0, 14, 24, isQualified = true),
            StandingsRow(2, "Atlético Chiclayo", "Distrito Centro", "AC", 10, 6, 3, 1, 9, 21, isCurrentUserTeam = true, isQualified = true),
            StandingsRow(3, "Los Caimanes FC", "Puerto Eten", "LC", 10, 6, 1, 3, 6, 19, isQualified = true),
            StandingsRow(4, "Deportivo Patazca", "Urb. Patazca", "DP", 10, 5, 2, 3, 4, 17, isQualified = true),
            StandingsRow(5, "Real Victoria", "La Victoria", "RV", 10, 4, 1, 5, -2, 13),
            StandingsRow(6, "Los Galácticos", "José L. Ortiz", "LG", 10, 2, 2, 6, -8, 8, isRelegation = true)
        )
    }

    val scorersList = remember {
        listOf(
            ScorerItem(1, "Piero Mendoza", "Atlético Chiclayo", 14, 10),
            ScorerItem(2, "K. Balarezo", "Los Caimanes FC", 11, 9),
            ScorerItem(3, "E. Merino", "Sporting Reque", 9, 10),
            ScorerItem(4, "Roberto Silva", "Deportivo Patazca", 7, 8),
            ScorerItem(5, "J. Pérez", "Atlético Chiclayo", 6, 10)
        )
    }

    val fairPlayList = remember {
        listOf(
            FairPlayItem(1, "Atlético Chiclayo", 1, 0, 98),
            FairPlayItem(2, "Sporting Reque", 3, 0, 92),
            FairPlayItem(3, "Deportivo Patazca", 5, 0, 85),
            FairPlayItem(4, "Real Victoria", 6, 1, 74),
            FairPlayItem(5, "Los Galácticos", 7, 1, 70),
            FairPlayItem(6, "Los Caimanes FC", 8, 2, 60)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkForestGreen)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LimeNeon),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.SportsSoccer,
                                contentDescription = null,
                                tint = DarkForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "ChocoFulbo",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            viewModel.showCreateTournamentDialog = true
                        }) {
                            Icon(
                                Icons.Outlined.AddCircleOutline,
                                contentDescription = "Crear Torneo",
                                tint = LimeNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(onClick = {
                            Toast.makeText(context, "Reglamento oficial de la Liga Amateur Chiclayo 2026", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Outlined.HelpOutline,
                                contentDescription = "Ayuda",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = { ChocoFulboBottomBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. HEADER CARD (TORNEO APERTURA 2026) MATCHING screen_ba3f1eac.png
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge Jornada
                            Surface(
                                color = DarkForestGreen,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "JORNADA 10 DE 14",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = LimeNeon,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Botón Trofeo
                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable {
                                        viewModel.showCreateTournamentDialog = true
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.EmojiEvents,
                                        contentDescription = "Torneo",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "Torneo Apertura 2026",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Liga 1 Amateur Chiclayo - Distrito Norte",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Fila de Categoría Dropdown y Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CATEGORÍA:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    onClick = { showCategoryMenu = !showCategoryMenu },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF9FAFB),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedCategoryDropdown,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Icon(Icons.Default.ArrowDropDown, null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Compartiendo tabla de posiciones de Chiclayo...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Outlined.Share, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // 2. SUBTABS (TABLA, GOLEADORES, FAIR PLAY)
            item {
                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Tabla", "Goleadores", "Fair Play").forEachIndexed { idx, title ->
                            val isSelected = selectedSubTab == idx
                            Surface(
                                onClick = { selectedSubTab = idx },
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) LimeNeon else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) DarkForestGreen else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. FILTROS SOLICITADOS POR EL USUARIO: Cerca de mí y Creados por mí
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todos", "Cerca de mí (Chiclayo)", "Creados por mí").forEach { filter ->
                        val isSel = selectedFilterCategory == filter
                        Surface(
                            onClick = { selectedFilterCategory = filter },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) DarkForestGreen else Color.White,
                            border = BorderStroke(1.dp, if (isSel) DarkForestGreen else BorderGray),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                                Text(
                                    text = filter,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 4. CONTENIDO SEGÚN SUBTAB SELECCIONADA
            when (selectedSubTab) {
                0 -> {
                    // TABLA GENERAL
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.TrendingUp, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "CLASIFICACIÓN GENERAL",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        text = "Actualizado: Hoy, 19:30",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                // Fila Encabezados de Columna
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("POS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(30.dp))
                                    Text("EQUIPO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.weight(1f))
                                    Text("PJ", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text("PG", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text("PE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text("PP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text("DG", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                                    Text("PTS", fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = TextPrimary, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                                }

                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                                Spacer(Modifier.height(4.dp))

                                // Filas de Equipos
                                standingsList.forEach { row ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Barra lateral de clasificación verde / roja
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(28.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    when {
                                                        row.isQualified -> LimeNeon
                                                        row.isRelegation -> Color(0xFFEF4444)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                        )

                                        Spacer(Modifier.width(6.dp))

                                        // Posición
                                        Row(
                                            modifier = Modifier.width(24.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${row.pos}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (row.isRelegation) Color(0xFFEF4444) else TextPrimary
                                            )
                                            if (row.pos == 1) {
                                                Text(" ▲", fontSize = 8.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                            } else if (row.pos == 2) {
                                                Text(" ☆", fontSize = 8.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Escudo circular + Nombre
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (row.pos) {
                                                        1 -> DarkForestGreen
                                                        2 -> ForestGreen
                                                        6 -> Color(0xFFFEE2E2)
                                                        else -> Color(0xFFF3F4F6)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = row.initials,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = when (row.pos) {
                                                    1, 2 -> LimeNeon
                                                    6 -> Color(0xFFEF4444)
                                                    else -> TextPrimary
                                                }
                                            )
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = row.teamName,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (row.isCurrentUserTeam) {
                                                    Spacer(Modifier.width(4.dp))
                                                    Surface(
                                                        color = DarkForestGreen,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "TÚ",
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = LimeNeon,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = row.district,
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }

                                        // Métricas numéricas
                                        Text("${row.pj}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                        Text("${row.pg}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                        Text("${row.pe}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                        Text("${row.pp}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                        Text(
                                            text = if (row.dg > 0) "+${row.dg}" else "${row.dg}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (row.dg > 0) ForestGreen else if (row.dg < 0) Color(0xFFEF4444) else TextSecondary,
                                            modifier = Modifier.width(26.dp),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "${row.pts}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            modifier = Modifier.width(28.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                                Spacer(Modifier.height(8.dp))

                                // Leyendas al pie de la tabla
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(LimeNeon))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Puestos 1 al 4: Clasificación a Semifinales (Copa de Oro)",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFEF4444)))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Puesto 6: Zona de Descenso / Eliminación directa",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // PRÓXIMO PARTIDO DE TU EQUIPO MATCHING screen_ba3f1eac.png
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clickable { navController.navigate("match_detail/1") },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.AccessTime, null, tint = LimeNeon, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "PRÓXIMO PARTIDO DE TU EQUIPO",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LimeNeon,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "Jueves 21:00",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Fila Enfrentamiento
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Local: Atlético Chiclayo
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("AC", fontWeight = FontWeight.Black, fontSize = 12.sp, color = DarkForestGreen)
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("Atlético\nChiclayo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, lineHeight = 15.sp)
                                            Text("2° Puesto • Local", color = LimeNeon, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // VS Central
                                    Text(
                                        text = "VS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = LimeNeon,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    // Visita: Sporting Reque
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Sporting\nReque", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, lineHeight = 15.sp, textAlign = TextAlign.End)
                                            Text("1° Puesto • Visita", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(LimeNeon),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("SR", fontWeight = FontWeight.Black, fontSize = 12.sp, color = DarkForestGreen)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 0.8.dp)
                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Stadium, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Complejo Recreativo El Triunfo", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                                    }
                                    Text("Cancha 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LimeNeon)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TABLA DE GOLEADORES
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("TABLA DE GOLEADORES (BOTA DE ORO)", fontWeight = FontWeight.Black, fontSize = 11.sp, color = ForestGreen)
                                Spacer(Modifier.height(10.dp))
                                scorersList.forEach { s ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${s.pos}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (s.pos == 1) ForestGreen else TextSecondary, modifier = Modifier.width(24.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (s.pos == 1) DarkForestGreen else Color(0xFFF3F4F6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Outlined.Person,
                                                null,
                                                tint = if (s.pos == 1) LimeNeon else TextPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text(s.team, fontSize = 11.sp, color = TextSecondary)
                                        }
                                        Text("${s.goals} goles", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = DarkForestGreen)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // FAIR PLAY
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("RANKING FAIR PLAY & CONDUCTA DEPORTIVA", fontWeight = FontWeight.Black, fontSize = 11.sp, color = ForestGreen)
                                Spacer(Modifier.height(10.dp))
                                fairPlayList.forEach { f ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${f.pos}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.width(24.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(f.team, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Text("🟨 ${f.yellowCards} Amarillas  •  🟥 ${f.redCards} Rojas", fontSize = 10.5.sp, color = TextSecondary)
                                        }
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                            Text("${f.score} pts", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ForestGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
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

    // DIÁLOGO CREAR NUEVO TORNEO (CAPITANES Y ADMIN_CANCHA)
    if (viewModel.showCreateTournamentDialog) {
        Dialog(onDismissRequest = { viewModel.showCreateTournamentDialog = false }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(18.dp)) {
                    item {
                        Text("Crear Nuevo Torneo", fontWeight = FontWeight.Black, fontSize = 17.sp, color = DarkForestGreen)
                        Text("Abierto para Administradores de Cancha y Capitanes", fontSize = 11.5.sp, color = TextSecondary)
                        Spacer(Modifier.height(12.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = newTourneyName,
                            onValueChange = { newTourneyName = it },
                            label = { Text("Nombre del Torneo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        Text("Rol Organizador:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf(Pair("ADMIN_CANCHA", "Admin Cancha"), Pair("CAPITAN", "Capitán")).forEach { (role, label) ->
                                val isSel = newTourneyRole == role
                                Surface(
                                    onClick = {
                                        newTourneyRole = role
                                        newTourneyOrganizerName = if (role == "ADMIN_CANCHA") "Complejo El Oasis" else userName
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) ForestGreen else TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        Text("Formato de Torneo:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf(TournamentFormat.RELAMPAGO, TournamentFormat.LLAVES_ELIMINATORIAS, TournamentFormat.FASE_GRUPOS).forEach { fmt ->
                                val isSel = newTourneyFormat == fmt
                                Surface(
                                    onClick = { newTourneyFormat = fmt },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) DarkForestGreen else Color(0xFFF3F4F6)
                                ) {
                                    Text(
                                        fmt.displayName.split(" ").first(),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        Text("Formato de Cancha:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("Fútbol 5", "Fútbol 7", "Fútbol 8", "Fútbol 11").forEach { gf ->
                                val isSel = newTourneyGameFormat == gf
                                Surface(
                                    onClick = { newTourneyGameFormat = gf },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) DarkForestGreen else Color(0xFFF3F4F6)
                                ) {
                                    Text(gf, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = newTourneyVenue,
                            onValueChange = { newTourneyVenue = it },
                            label = { Text("Sede Deportiva (Chiclayo)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = newTourneyPrize,
                            onValueChange = { newTourneyPrize = it },
                            label = { Text("Premio al Ganador") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(14.dp))
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.showCreateTournamentDialog = false },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    viewModel.createTournament(
                                        name = newTourneyName,
                                        organizerRole = newTourneyRole,
                                        organizerName = newTourneyOrganizerName,
                                        format = newTourneyFormat,
                                        gameFormat = newTourneyGameFormat,
                                        venueName = newTourneyVenue,
                                        startDate = newTourneyDate,
                                        entryFee = newTourneyFee.toDoubleOrNull() ?: 100.0,
                                        prizePool = newTourneyPrize,
                                        maxTeams = newTourneyMaxTeams
                                    )
                                    Toast.makeText(context, "¡Torneo creado exitosamente en Chiclayo!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Publicar", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
