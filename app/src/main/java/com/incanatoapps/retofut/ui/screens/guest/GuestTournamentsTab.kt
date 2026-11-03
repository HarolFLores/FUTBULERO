package com.incanatoapps.retofut.ui.screens.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

data class GuestTeamStanding(
    val pos: Int,
    val acronym: String,
    val name: String,
    val pj: Int,
    val dg: String,
    val pts: Int,
    val badgeBg: Color,
    val badgeTextColor: Color
)

@Composable
fun GuestTournamentsTab(
    onRestrictedActionClick: () -> Unit
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Tabla de Posiciones, 1: Fixture & Resultados

    val standings = remember {
        listOf(
            GuestTeamStanding(
                pos = 1,
                acronym = "AC",
                name = "Atlético Chiclayo",
                pj = 3,
                dg = "+7",
                pts = 9,
                badgeBg = DarkForestGreen,
                badgeTextColor = LimeNeon
            ),
            GuestTeamStanding(
                pos = 2,
                acronym = "LG",
                name = "Los Galácticos FC",
                pj = 3,
                dg = "+4",
                pts = 7,
                badgeBg = Color(0xFF86EFAC),
                badgeTextColor = DarkForestGreen
            ),
            GuestTeamStanding(
                pos = 3,
                acronym = "SR",
                name = "Sporting Reque",
                pj = 3,
                dg = "+2",
                pts = 6,
                badgeBg = Color(0xFFE5E7EB),
                badgeTextColor = TextPrimary
            ),
            GuestTeamStanding(
                pos = 4,
                acronym = "DP",
                name = "Defensor Pimentel",
                pj = 3,
                dg = "-1",
                pts = 4,
                badgeBg = Color(0xFFE5E7EB),
                badgeTextColor = TextPrimary
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("guest_tournaments_tab"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HEADER: CAMPEONATOS OFICIALES & BTN LOCK
        // ==========================================
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CAMPEONATOS OFICIALES",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = ForestGreen,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Torneos Chiclayo 2025",
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )

                    // Botón verde pill Inscribir Equipo con candado
                    Surface(
                        onClick = onRestrictedActionClick,
                        shape = RoundedCornerShape(20.dp),
                        color = LimeNeon,
                        modifier = Modifier.testTag("inscribir_equipo_guest_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.AddCircleOutline,
                                contentDescription = null,
                                tint = DarkForestGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Inscribir Equipo",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("🔒", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. HERO CARD: TORNEO APERTURA F7
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = DarkForestGreen,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LimeNeon
                        ) {
                            Text(
                                text = "FECHA 4 EN JUEGO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 12.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Pozo: S/ 3,000",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LimeNeon
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Torneo Apertura F7 Chiclayo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Sede: Complejo San Juan & El Oasis • 16 Equipos",
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // ==========================================
        // 3. SEGMENTED TABS: TABLA DE POSICIONES / FIXTURE
        // ==========================================
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF3F4F6),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = { selectedSection = 0 },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedSection == 0) Color.White else Color.Transparent,
                        shadowElevation = if (selectedSection == 0) 1.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Tabla de Posiciones",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedSection == 0) DarkForestGreen else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }

                    Surface(
                        onClick = { selectedSection = 1 },
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedSection == 1) Color.White else Color.Transparent,
                        shadowElevation = if (selectedSection == 1) 1.dp else 0.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Fixture & Resultados",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedSection == 1) DarkForestGreen else TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. CONTENT ACCORDING TO TAB
        // ==========================================
        if (selectedSection == 0) {
            // TABLA DE POSICIONES
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Header Table Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "EQUIPO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "PJ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                modifier = Modifier.width(32.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "DG",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "PTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                        // Data Rows
                        standings.forEachIndexed { index, team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = team.pos.toString(),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (team.pos <= 2) ForestGreen else TextSecondary,
                                    modifier = Modifier.width(24.dp),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.width(6.dp))

                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = team.badgeBg,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = team.acronym,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = team.badgeTextColor
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = team.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = team.pj.toString(),
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.width(32.dp),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = team.dg,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (team.dg.startsWith("+")) ForestGreen else Color(0xFFDC2626),
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = team.pts.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.width(36.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (index < standings.size - 1) {
                                HorizontalDivider(color = Color(0xFFF9FAFB), thickness = 1.dp)
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Footer Row: ¿Quieres jugar aquí con tus amigos? Inscribirme ➔
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRestrictedActionClick() },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "¿Quieres jugar aquí con tus amigos?",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Inscribirme",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = DarkForestGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // FIXTURE & RESULTADOS (FECHA 4)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Partidos Programados - Fecha 4",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )

                        Spacer(Modifier.height(10.dp))

                        FixtureMatchItem(
                            time = "Hoy 20:30",
                            teamA = "Atlético Chiclayo",
                            teamB = "Los Galácticos FC",
                            court = "Complejo El Oasis • Cancha 2",
                            status = "En vivo en 45 min",
                            isLive = true
                        )

                        Spacer(Modifier.height(8.dp))

                        FixtureMatchItem(
                            time = "Sábado 19:00",
                            teamA = "Sporting Reque",
                            teamB = "Defensor Pimentel",
                            court = "Canchas Patazca • Cancha Principal",
                            status = "Programado",
                            isLive = false
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = onRestrictedActionClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkForestGreen,
                                contentColor = LimeNeon
                            )
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Inscribir mi escuadra a Fecha 5", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FixtureMatchItem(
    time: String,
    teamA: String,
    teamB: String,
    court: String,
    status: String,
    isLive: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(time, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLive) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = status,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLive) ForestGreen else TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "$teamA  vs  $teamB",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = court,
                fontSize = 9.5.sp,
                color = TextSecondary
            )
        }
    }
}
