package com.incanatoapps.retofut.ui.screens.matches

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

data class MatchBetItem(
    val id: Int,
    val league: String,
    val time: String,
    val formatBadge: String,
    val venue: String,
    val teamA: String,
    val teamB: String,
    val initialsA: String,
    val initialsB: String,
    val oddA: String,
    val oddDraw: String,
    val oddB: String,
    val poolTotal: String,
    val favorPercent: Int,
    var selectedOdd: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetsHubScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var userBalance by remember { mutableDoubleStateOf(45.00) }
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var selectedBetMatch1 by remember { mutableStateOf("A") } // A, Draw, B
    var selectedBetMatch2 by remember { mutableStateOf<String?>(null) }
    var betAmount by remember { mutableDoubleStateOf(10.00) }
    val potentialReturn = betAmount * 1.85

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkForestGreen)
                    .statusBarsPadding()
            ) {
                // Top Row: Back, Title, Notification, Avatar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Quinielas & Apuestas",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            navController.navigate(Screen.Notifications.route)
                        }) {
                            Box {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(LimeNeon)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        IconButton(onClick = {
                            navController.navigate("player_card/1")
                        }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen)
                                    .border(1.dp, LimeNeon, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("PM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                        }
                    }
                }

                // Sub-Bar: Ubicación y Saldo de Billetera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "POZO DEL BARRIO • CHICLAYO",
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
                        Row(
                            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Saldo ", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("S/ ${String.format("%.2f", userBalance)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                onClick = {
                                    userBalance += 20.0
                                    Toast.makeText(context, "¡Recarga de S/ 20.00 simulada con Yape exitosa!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(50),
                                color = LimeNeon
                            ) {
                                Text(
                                    text = "+ Recargar",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        },
        bottomBar = {
            Surface(
                color = DarkForestGreen,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                                Spacer(Modifier.width(6.dp))
                                Text("1 partido seleccionado", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.85f))
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Tu apuesta: S/ ${String.format("%.2f", betAmount)}",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Ganancia: ", fontSize = 11.5.sp, color = Color.White)
                                Text("S/ ${String.format("%.2f", potentialReturn)}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                        }

                        Button(
                            onClick = {
                                if (userBalance >= betAmount) {
                                    userBalance -= betAmount
                                    Toast.makeText(context, "¡Apuesta de S/ 10.00 registrada para el triunfo de Atlético Chiclayo!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Saldo insuficiente. Recarga con Yape para apostar", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LimeNeon,
                                contentColor = DarkForestGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                            modifier = Modifier.testTag("confirm_bet_button")
                        ) {
                            Text(
                                text = "Confirmar Apuesta",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkForestGreen, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, null, tint = LimeNeon, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Apuestas comunitarias reguladas por ChocoFulbo",
                            fontSize = 9.5.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
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
            // 1. HERO POZO ACUMULADO (GRAN POZO DE LA JORNADA 10)
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
                        // Fondo cancha sutil
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val stroke = Stroke(1.dp.toPx())
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = size.width * 0.45f,
                                center = Offset(size.width * 0.5f, size.height * 0.5f),
                                style = stroke
                            )
                        }

                        Column {
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
                                        Text("🔥", fontSize = 10.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "GRAN POZO DE LA JORNADA 10",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LimeNeon
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Outlined.AccessTime, null, tint = LimeNeon, modifier = Modifier.size(11.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "02h 45m",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "POZO ACUMULADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "S/ 1,250.00",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = LimeNeon
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Groups, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("148 hinchas apostando", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.85f))
                                }
                                Text("Mínimo S/ 2.00", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
                            }

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Pozo de la jornada seleccionado", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LimeNeon,
                                    contentColor = DarkForestGreen
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Text(
                                    text = "Apostar en el Pozo",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkForestGreen, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
            }

            // 2. SEGMENTED TABS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Partidos Hoy (6)", "Clásicos del Barrio", "Mis Apuestas").forEachIndexed { idx, tabTitle ->
                        val isSel = selectedCategoryTab == idx
                        Surface(
                            onClick = { selectedCategoryTab = idx },
                            shape = RoundedCornerShape(50),
                            color = if (isSel) DarkForestGreen else Color.White,
                            border = BorderStroke(1.dp, if (isSel) DarkForestGreen else BorderGray),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                                Text(
                                    text = tabTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) LimeNeon else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 3. CARTELERA EN VIVO
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cartelera en Vivo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF16A34A)))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "6 CUOTAS ACTIVAS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }
            }

            // PARTIDO 1: Atlético Chiclayo vs Los Galácticos
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
                            Text(
                                text = "CHICLAYO CUP • HOY 20:30",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                Text("F7 Nocturno", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Place, null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Complejo El Oasis Cancha 2", fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(Modifier.height(10.dp))

                        // Enfrentamiento
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(DarkForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("AC", color = LimeNeon, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Atlético Chic...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Local", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Text("VS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextSecondary)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Los Galácticos", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Visita", fontSize = 10.sp, color = TextSecondary)
                                }
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("LG", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // 3 Botones de Cuota
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Gana Local (x1.85) - SELECCIONADO
                            Surface(
                                onClick = { selectedBetMatch1 = "A" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch1 == "A") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch1 == "A") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Gana Atl. Chicla...", fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                                    Text("x1.85", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (selectedBetMatch1 == "A") DarkForestGreen else TextPrimary)
                                }
                            }

                            // Empate (x3.20)
                            Surface(
                                onClick = { selectedBetMatch1 = "Draw" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch1 == "Draw") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch1 == "Draw") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Empate", fontSize = 9.sp, color = TextSecondary)
                                    Text("x3.20", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (selectedBetMatch1 == "Draw") DarkForestGreen else TextPrimary)
                                }
                            }

                            // Gana Visita (x2.10)
                            Surface(
                                onClick = { selectedBetMatch1 = "B" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch1 == "B") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch1 == "B") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Gana Galácticos", fontSize = 9.sp, color = TextSecondary, maxLines = 1)
                                    Text("x2.10", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (selectedBetMatch1 == "B") DarkForestGreen else TextPrimary)
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Pozo del Partido
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9FAFB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Savings, null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Pozo del partido: ", fontSize = 10.5.sp, color = TextSecondary)
                                        Text("S/ 340.00", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    }
                                    Text("72% a favor", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
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
                                            .background(LimeNeon)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // PARTIDO 2: Sporting Reque vs Los Caimanes FC
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
                            Text(
                                text = "TORNEO CLAUSURA • HOY 21:30",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                Text("F8 Libre", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Place, null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Complejo Deportivo La Victoria", fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(ForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SR", color = LimeNeon, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Sporting Reque", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Local", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Text("VS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextSecondary)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Los Caimanes...", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("Visita", fontSize = 10.sp, color = TextSecondary)
                                }
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFC2410C)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("LC", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Cuotas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { selectedBetMatch2 = "A" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch2 == "A") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch2 == "A") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Gana Reque", fontSize = 9.sp, color = TextSecondary)
                                    Text("x1.60", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }

                            Surface(
                                onClick = { selectedBetMatch2 = "Draw" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch2 == "Draw") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch2 == "Draw") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Empate", fontSize = 9.sp, color = TextSecondary)
                                    Text("x2.90", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }

                            Surface(
                                onClick = { selectedBetMatch2 = "B" },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedBetMatch2 == "B") Color(0xFFDCFCE7) else Color(0xFFF9FAFB),
                                border = BorderStroke(1.2.dp, if (selectedBetMatch2 == "B") ForestGreen else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Gana Caimanes", fontSize = 9.sp, color = TextSecondary)
                                    Text("x2.40", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Pozo
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9FAFB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Savings, null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Pozo del partido: ", fontSize = 10.5.sp, color = TextSecondary)
                                        Text("S/ 210.00", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                    }
                                    Text("58% a favor", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                }
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
                                            .fillMaxWidth(0.58f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(LimeNeon)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. CARD PROMOCIONAL: ¿TIENES TU PROPIA PICHANGA?
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.MilitaryTech, null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "¿Tienes tu propia pichanga?",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.5.sp,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Crea un pozo cerrado con tus amigos y administra pagos directo en la app.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}
