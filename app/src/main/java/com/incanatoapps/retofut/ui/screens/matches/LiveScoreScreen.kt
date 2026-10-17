package com.incanatoapps.retofut.ui.screens.matches

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoreScreen(navController: NavHostController, matchId: Int) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var mvpVotes by remember { mutableStateOf(348) }
    var hasVoted by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "TORNEO CLAUSURA CHICLAYO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "En Vivo / Resultado",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827)
                    )
                }

                IconButton(onClick = {
                    Toast.makeText(context, "Compartiendo marcador en vivo de Chiclayo FC", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = TextPrimary)
                }

                IconButton(onClick = {
                    isFavorite = !isFavorite
                    Toast.makeText(context, if (isFavorite) "Añadido a tus partidos favoritos" else "Quitado de favoritos", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFEAB308) else TextPrimary
                    )
                }
            }

            // --- CONTENIDO DEL MARCADOR CON SCROLL ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. HERO SCOREBOARD CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    val stroke = Stroke(width = 1.dp.toPx())
                                    val color = Color.White.copy(alpha = 0.05f)
                                    drawCircle(
                                        color = color,
                                        radius = size.width * 0.4f,
                                        center = Offset(size.width * 0.5f, size.height * 0.5f),
                                        style = stroke
                                    )
                                }
                                .padding(18.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Fila superior: Badge EN VIVO + Fecha
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFF1B4D31),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, LimeNeon)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(LimeNeon))
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "EN VIVO (82')",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LimeNeon
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Fecha 7 • Grupo B",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Spacer(Modifier.height(18.dp))

                                // Fila de Equipos y Marcador
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Equipo Local: Atlético Chiclayo
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Shield, null, tint = DarkForestGreen, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text("Atlético\nChiclayo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                                        Text("LOCAL", color = LimeNeon, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                    }

                                    // Marcador Central
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("3", fontSize = 42.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                            Text(" - ", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
                                            Text("2", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                                        }
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "2do Tiempo",
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Equipo Visita: Los Caimanes FC
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.SportsSoccer, null, tint = DarkForestGreen, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text("Los Caimanes\nFC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                                        Text("VISITA", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }

                                Spacer(Modifier.height(18.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)
                                Spacer(Modifier.height(12.dp))

                                // Footer: Sede y Árbitro
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocationOn, null, tint = LimeNeon, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Complejo El Oasis, Cancha 2 • Chiclayo", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Sports, null, tint = LimeNeon, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("M. Santisteban", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. ESTADÍSTICAS DEL PARTIDO (3 CAJAS)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Posesión
                        StatBox(
                            title = "POSESIÓN",
                            statA = "54%",
                            statB = "46%",
                            sub = "54% vs 46%",
                            modifier = Modifier.weight(1f)
                        )

                        // Tiros al arco
                        StatBox(
                            title = "TIROS AL ARCO",
                            statA = "7",
                            statB = "5",
                            sub = "12 en total",
                            modifier = Modifier.weight(1f)
                        )

                        // Faltas
                        StatBox(
                            title = "FALTAS",
                            statA = "6",
                            statB = "9",
                            sub = "1 Amarilla",
                            subColor = Color(0xFFEAB308),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. LÍNEA DE TIEMPO (TIMELINE DE EVENTOS)
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Timeline, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Línea de Tiempo", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
                            }

                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "ORDEN CRONOLÓGICO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Eventos del Timeline
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // 80' Cambio Táctico
                            TimelineRow(
                                minute = "80'",
                                icon = Icons.Outlined.SwapHoriz,
                                iconBg = Color(0xFFF3F4F6),
                                iconTint = TextPrimary
                            ) {
                                Text("⇄ Cambio Táctico • Atl. Chiclayo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("Sale J. Pérez (10), entra D. Torres (14)", fontSize = 11.5.sp, color = TextSecondary)
                            }

                            // 72' ¡GOLAZO! Piero Mendoza (Tarjeta destacada)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.width(36.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("72'", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreen)
                                }
                                Spacer(Modifier.width(6.dp))
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text("¡GOLAZO! Remate de fuera", fontWeight = FontWeight.Black, fontSize = 12.sp, color = LimeNeon)
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text("P. Mendoza (Atlético Chiclayo)", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = Color.White)
                                        Spacer(Modifier.height(4.dp))
                                        Surface(
                                            color = Color(0xFF143823),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                "Bomba al ángulo superior derecho",
                                                color = LimeNeon,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // 65' Gol Los Caimanes
                            TimelineRow(
                                minute = "65'",
                                icon = Icons.Outlined.SportsSoccer,
                                iconBg = Color(0xFFF3F4F6),
                                iconTint = DarkForestGreen
                            ) {
                                Text("⚽ GOL • Los Caimanes FC", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("K. Balarezo (Los Caimanes FC)", fontSize = 11.5.sp, color = TextSecondary)
                            }

                            // 58' Tarjeta Amarilla
                            TimelineRow(
                                minute = "58'",
                                icon = Icons.Outlined.Warning,
                                iconBg = Color(0xFFFEF9C3),
                                iconTint = Color(0xFFCA8A04)
                            ) {
                                Text("🟨 Tarjeta Amarilla • Los Caimanes", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("E. Farro por infracción táctica en mediocampo.", fontSize = 11.5.sp, color = TextSecondary)
                            }

                            // 34' Gol Los Caimanes
                            TimelineRow(
                                minute = "34'",
                                icon = Icons.Outlined.SportsSoccer,
                                iconBg = Color(0xFFF3F4F6),
                                iconTint = DarkForestGreen
                            ) {
                                Text("⚽ GOL • Los Caimanes FC", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("R. Cárdenas (Los Caimanes FC)", fontSize = 11.5.sp, color = TextSecondary)
                            }

                            // 12' Gol Atlético Chiclayo
                            TimelineRow(
                                minute = "12'",
                                icon = Icons.Outlined.SportsSoccer,
                                iconBg = Color(0xFFDCFCE7),
                                iconTint = ForestGreen
                            ) {
                                Text("⚽ GOL • Atlético Chiclayo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("J. Pérez (Atlético Chiclayo)", fontSize = 11.5.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // 4. JUGADOR DESTACADO (MVP PROVISIONAL)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, LimeNeon)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jugador Destacado", fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextPrimary)
                                Surface(
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        "MVP PROVISIONAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        color = DarkForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(DarkForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Outlined.Person, null, tint = LimeNeon, modifier = Modifier.size(24.dp))
                                        Text("#8", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Piero Mendoza", fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextPrimary)
                                        Text("72%", fontWeight = FontWeight.Black, fontSize = 15.sp, color = ForestGreen)
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text("Volante Ofensivo • 1 Golazo, 1 Asistencia", fontSize = 12.sp, color = TextSecondary)
                                    Spacer(Modifier.height(6.dp))

                                    // Barra de votación
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFE5E7EB))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.72f)
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(LimeNeon)
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text("$mvpVotes votos de la hinchada", fontSize = 10.5.sp, color = TextSecondary)
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (!hasVoted) {
                                            mvpVotes++
                                            hasVoted = true
                                            Toast.makeText(context, "¡Voto registrado por Piero Mendoza!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Ya votaste por el MVP de este encuentro", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = LimeNeon),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.ThumbUp, null, tint = DarkForestGreen, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = if (hasVoted) "¡Ya Votaste!" else "Votar por MVP",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = DarkForestGreen
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable {
                                            Toast.makeText(context, "Ver estadísticas individuales de todos los jugadores", Toast.LENGTH_SHORT).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Tune, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
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
private fun StatBox(
    title: String,
    statA: String,
    statB: String,
    sub: String,
    modifier: Modifier = Modifier,
    subColor: Color = TextSecondary
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(statA, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(" vs ", fontSize = 10.sp, color = TextSecondary)
                Text(statB, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }
            Spacer(Modifier.height(2.dp))
            Text(sub, fontSize = 10.sp, color = subColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun TimelineRow(
    minute: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(minute, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
        }
        Spacer(Modifier.width(6.dp))
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(content = content)
            }
        }
    }
}
