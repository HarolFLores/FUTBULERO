package com.incanatoapps.retofut.ui.screens.courts

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtTournamentsTab(
    navController: NavHostController,
    viewModel: CourtManagementViewModel,
    onCreateTournamentClick: () -> Unit
) {
    val context = LocalContext.current
    var tourneyFilter by remember { mutableStateOf("Todos") }

    val displayedTournaments = remember(tourneyFilter, viewModel.venueTournaments.toList()) {
        when (tourneyFilter) {
            "En Juego" -> viewModel.venueTournaments.filter { it.statusLabel.contains("Juego", ignoreCase = true) }
            "Inscripciones Abiertas" -> viewModel.venueTournaments.filter { it.quotaBadge != null || it.statusLabel.contains("Inscrip", ignoreCase = true) || it.statusLabel.contains("Abiert", ignoreCase = true) }
            else -> viewModel.venueTournaments.toList()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HEADER TITLE & CREATE TOURNAMENT BUTTON
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Torneos de Mi Sede",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )
                    Text(
                        text = "Competencias organizadas en ${viewModel.complexName}",
                        fontSize = 11.5.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onCreateTournamentClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(38.dp).testTag("create_tourney_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Crear Torneo",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // ==========================================
        // 1b. FILTER CHIPS
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "En Juego", "Inscripciones Abiertas").forEach { filterLabel ->
                    val isSel = tourneyFilter == filterLabel
                    Surface(
                        onClick = { tourneyFilter = filterLabel },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) ForestGreen else Color.White,
                        border = BorderStroke(1.dp, if (isSel) ForestGreen else BorderGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filterLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. TOURNAMENT CARD 1: TORNEO APERTURA F7
        // ==========================================
        item {
            val tourney1 = viewModel.venueTournaments.firstOrNull()
            if (tourney1 != null && displayedTournaments.contains(tourney1)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF3F4F6),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(tourney1.title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text(tourney1.courtLocation, fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            // Banner Status
                            Surface(
                                color = ForestGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = tourney1.statusLabel,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Stats: Pozo y Equipos
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Pozo de Premios:", fontSize = 10.5.sp, color = TextSecondary)
                                    Text(tourney1.prizePool, fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Equipos:", fontSize = 10.5.sp, color = TextSecondary)
                                    Text("${tourney1.teamsRegistered} / ${tourney1.maxTeams} Inscritos", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Botones de Acción
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate(Screen.Tournaments.route) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Ver Fixture & Cruces", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Surface(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "¡Inscríbete en el ${tourney1.title} en ${viewModel.complexName}! Pozo: ${tourney1.prizePool}. Descarga ChocoFulbo Chiclayo.")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Compartir Torneo"))
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = ForestGreen,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. TOURNAMENT CARD 2: COPA RELÁMPAGO NOCTURNA
        // ==========================================
        item {
            val tourney2 = viewModel.venueTournaments.getOrNull(1)
            if (tourney2 != null && displayedTournaments.contains(tourney2)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.FlashOn, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(tourney2.title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Text("${tourney2.dateLabel} • ${tourney2.formatDetail ?: "Formato F6"}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Inscripción y Cupo Badge
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Inscripción por equipo: ", fontSize = 11.sp, color = TextSecondary)
                                    Text("S/ ${"%.2f".format(tourney2.feePerTeam)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }

                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = tourney2.quotaBadge ?: "8 Equipos Cupo",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Gestión de cupos abierta (5/8 equipos confirmados)", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Outlined.Assignment, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Text("📋  Gestionar Cupos (5/8)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. ARBITRAJE CODAR CERTIFICADO BANNER
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.MilitaryTech, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(22.dp))
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Arbitraje CODAR Certificado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Incluye planilla digital y estadísticas de goleadores ChocoFulbo.",
                            fontSize = 11.sp,
                            color = ForestGreen,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
