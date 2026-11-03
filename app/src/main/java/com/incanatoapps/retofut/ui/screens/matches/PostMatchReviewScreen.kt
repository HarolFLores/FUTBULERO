package com.incanatoapps.retofut.ui.screens.matches

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMatchReviewScreen(
    navController: NavHostController,
    matchId: Int = 1
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var userRatingStars by remember { mutableIntStateOf(5) }
    var rewardsClaimed by remember { mutableStateOf(false) }
    var selectedAspects by remember {
        mutableStateOf(setOf("Puntual", "Imparcial", "Buen manejo de faltas", "Dejó jugar"))
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
                // Barra Superior con Título y Marcador Final
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTA Y CALIFICACIÓN FINAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = LimeNeon,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Torneo Apertura 2026 • Fecha 5",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Compartiendo acta oficial del encuentro", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = Color.White)
                    }
                }

                // Píldora del Resultado Final
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Atlético Chiclayo
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("AC", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Atlético\nChiclayo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                lineHeight = 13.sp
                            )
                        }

                        // Marcador + Tiempo Completo
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "3 - 2",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = LimeNeon
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(LimeNeon))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "FINALIZADO (TIEMPO COMPLETO)",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // Los Caimanes FC
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Los\nCaimanes FC",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                lineHeight = 13.sp,
                                textAlign = TextAlign.End
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFC2410C)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LC", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "¡Carta FIFA actualizada con 88 OVR! Tus stats ya están en la tabla oficial.", Toast.LENGTH_LONG).show()
                            navController.navigate("player_card/1")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_and_update_fifa_card_btn")
                    ) {
                        Text(
                            text = "Guardar y Actualizar mi Carta FIFA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = LimeNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tus estadísticas se sincronizarán con la tabla oficial del torneo",
                        fontSize = 10.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. HERO GAMIFICACIÓN: ¡SUBISTE DE NIVEL!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {
                            // Badge ¡Subiste de Nivel!
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    color = Color(0xFF1B4D31),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, LimeNeon)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("✪", color = LimeNeon, fontSize = 11.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "¡SUBISTE DE NIVEL!",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LimeNeon,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text("✪", color = LimeNeon, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = "¡Partidazo Histórico, Piero!",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Tu valoración deportiva ascendió tras la victoria en El Oasis",
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(16.dp))

                            // Fila con Miniatura de Carta FIFA + Métricas
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Tarjeta Estilo FIFA (Dorado / Especial)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFD97706),
                                    border = BorderStroke(2.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier
                                        .width(135.dp)
                                        .height(180.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("88", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                                                Text("MCO", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFFFEF3C7))
                                                Text("🇵🇪", fontSize = 10.sp)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(DarkForestGreen),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("AC", color = LimeNeon, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        // Foto de jugador
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                                .border(2.dp, Color(0xFFFDE68A), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Person, null, tint = DarkForestGreen, modifier = Modifier.size(34.dp))
                                        }

                                        Spacer(Modifier.height(6.dp))
                                        Surface(color = Color(0xFF143823), shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                "UP! P. MENDOZA",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Black,
                                                color = LimeNeon,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }

                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "91 RIT • 89 PAS • 86 REG",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(Modifier.width(12.dp))

                                // Columna de Subida de OVR y XP
                                Column(modifier = Modifier.weight(1f)) {
                                    // Media General Box
                                    Surface(
                                        color = Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("MEDIA GENERAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f))
                                            Spacer(Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    "87",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.5f),
                                                    textDecoration = TextDecoration.LineThrough
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text("88", fontSize = 24.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                Spacer(Modifier.width(4.dp))
                                                Text("OVR", fontSize = 13.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                                Spacer(Modifier.width(8.dp))
                                                Surface(color = Color(0xFFD97706), shape = RoundedCornerShape(4.dp)) {
                                                    Text(
                                                        "+1",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Progreso XP
                                    Surface(
                                        color = Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Progreso a Nivel 10", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                Text("+180 XP", fontSize = 10.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                                            }
                                            Spacer(Modifier.height(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Color.White.copy(alpha = 0.2f))
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.82f)
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(LimeNeon)
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("2,480 / 2,800 XP", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                                                Text("82%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LimeNeon)
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Badge Nivel 9: Volante Estrella
                                    Surface(
                                        color = Color(0xFF143823),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, LimeNeon),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Outlined.MilitaryTech, null, tint = LimeNeon, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "Nivel 9: Volante Estrella",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. RENDIMIENTO EN CANCHA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                Icon(Icons.Outlined.Assessment, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Rendimiento en Cancha 2", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = TextPrimary)
                            }
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(50)) {
                                Text(
                                    "75 Min Jugados",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Golazo
                            Surface(
                                color = Color(0xFFF9FAFB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("1", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ForestGreen)
                                    Text("⚽ GOLAZO", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Minuto 72'", fontSize = 9.sp, color = TextSecondary)
                                }
                            }

                            // Asistencia
                            Surface(
                                color = Color(0xFFF9FAFB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("1", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                                    Text("🎯 ASISTENCIA", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Pase a J. Pérez", fontSize = 9.sp, color = TextSecondary)
                                }
                            }

                            // Robos
                            Surface(
                                color = Color(0xFFF9FAFB),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("2", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
                                    Text("🛡️ ROBOS", fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    Text("Recuperaciones", fontSize = 9.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Reclamar Recompensas
                        Surface(
                            onClick = {
                                if (!rewardsClaimed) {
                                    rewardsClaimed = true
                                    Toast.makeText(context, "¡+50 ChocoMonedas acreditadas a tu cuenta!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Ya reclamaste las recompensas de este partido", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (rewardsClaimed) Color(0xFFF3F4F6) else Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, if (rewardsClaimed) BorderGray else LimeNeon),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.CardGiftcard, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (rewardsClaimed) "✓ Recompensas Reclamadas (+50 ChocoMonedas)" else "Reclamar Recompensas del Partido (+50 ChocoMonedas)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForestGreen
                                )
                            }
                        }
                    }
                }
            }

            // 3. VOTACIÓN AL JUGADOR DEL PARTIDO (MVP)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                Icon(Icons.Outlined.EmojiEvents, null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Votación al Jugador del Partido (MVP)", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = TextPrimary)
                            }
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(50)) {
                                Text(
                                    "Votación Cerrada",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Elegido por los jugadores de ambos equipos y los hinchas presentes:",
                            fontSize = 11.5.sp,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(12.dp))

                        // Candidato 1: Piero Mendoza (Ganador)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, LimeNeon),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(DarkForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Person, null, tint = LimeNeon, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Piero Mendoza", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextPrimary)
                                        Spacer(Modifier.width(4.dp))
                                        Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                            Text("TÚ", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = LimeNeon, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("Atlético Chiclayo • 1G, 1A", fontSize = 11.sp, color = TextSecondary)
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFFE5E7EB))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.72f)
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(ForestGreen)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("72%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ForestGreen)
                                    Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                        Text("MVP OFICIAL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Candidato 2: K. Balarezo
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE5E7EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("KB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("K. Balarezo", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                    Text("Los Caimanes FC • 1G (Tiro Libre)", fontSize = 10.5.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("18%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("6 votos", fontSize = 9.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        // Candidato 3: Carlos M.
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE5E7EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("CM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Carlos M.", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                    Text("Atlético Chiclayo • 7 Atajadas", fontSize = 10.5.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("10%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("3 votos", fontSize = 9.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // 4. CALIFICACIÓN DEL ARBITRAJE OFICIAL
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                Icon(Icons.Outlined.Balance, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Calificación del Arbitraje Oficial", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = TextPrimary)
                            }
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                Text("Obligatorio", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Tarjeta Árbitro Asignado
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, BorderGray),
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
                                        .background(Color(0xFFDCFCE7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Sports, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("M. Santisteban", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextPrimary)
                                    Text("Colegiado de Árbitros de Chiclayo", fontSize = 11.sp, color = TextSecondary)
                                    Text("Carnet FPF Reg. #4829", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
                                Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(6.dp)) {
                                    Text("Fútbol 7 Pro", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Selector de 5 estrellas
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEFCE8),
                            border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tu calificación: $userRatingStars.0 / 5.0 (Excelente labor)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF854D0E)
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    (1..5).forEach { star ->
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = "Estrella $star",
                                            tint = if (star <= userRatingStars) Color(0xFFEAB308) else Color(0xFFD1D5DB),
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { userRatingStars = star }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Pulsa las estrellas para calibrar el desempeño arbitral",
                                    fontSize = 9.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text("Aspectos destacados del partido:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(Modifier.height(6.dp))

                        // Chips de valoración
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Puntual", "Imparcial").forEach { aspect ->
                                val isSel = selectedAspects.contains(aspect)
                                Surface(
                                    onClick = {
                                        selectedAspects = if (isSel) selectedAspects - aspect else selectedAspects + aspect
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = if (isSel) Color(0xFFDCFCE7) else Color.White,
                                    border = BorderStroke(1.dp, if (isSel) ForestGreen else BorderGray),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSel) {
                                            Icon(Icons.Default.Check, null, tint = ForestGreen, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                        }
                                        Text(aspect, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) ForestGreen else TextPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Buen manejo de faltas", "Dejó jugar").forEach { aspect ->
                                val isSel = selectedAspects.contains(aspect)
                                Surface(
                                    onClick = {
                                        selectedAspects = if (isSel) selectedAspects - aspect else selectedAspects + aspect
                                    },
                                    shape = RoundedCornerShape(50),
                                    color = if (isSel) Color(0xFFDCFCE7) else Color.White,
                                    border = BorderStroke(1.dp, if (isSel) ForestGreen else BorderGray),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSel) {
                                            Icon(Icons.Default.Check, null, tint = ForestGreen, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                        }
                                        Text(aspect, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) ForestGreen else TextPrimary)
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
