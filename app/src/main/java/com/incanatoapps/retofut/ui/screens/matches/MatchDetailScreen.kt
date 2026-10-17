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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class ConfirmedPlayer(
    val initials: String,
    val positionTag: String,
    val name: String,
    val role: String,
    val isOrganizer: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(navController: NavHostController, matchId: Int) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var isEnrolled by remember { mutableStateOf(false) }
    var currentConfirmedCount by remember { mutableStateOf(11) }

    val players = remember {
        listOf(
            ConfirmedPlayer("PM", "DEL", "Piero M. (Cap.)", "Organizador", isOrganizer = true),
            ConfirmedPlayer("CR", "MED", "Carlos R.", "Confirmado"),
            ConfirmedPlayer("BS", "DEF", "Beto S.", "Confirmado"),
            ConfirmedPlayer("DL", "ARQ", "Diego L.", "Confirmado"),
            ConfirmedPlayer("JT", "MED", "Jorge T.", "Confirmado"),
            ConfirmedPlayer("MA", "DEF", "Manuel A.", "Confirmado")
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

                Text(
                    text = "Detalles del Partido",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    Toast.makeText(context, "Enlace de la pichanga copiado al portapapeles", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = TextPrimary)
                }

                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkForestGreen)
                        .border(1.5.dp, LimeNeon, CircleShape)
                        .clickable { navController.navigate(Screen.Profile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // --- CONTENIDO DEL PARTIDO CON SCROLL ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 1. HERO BANNER DE LA CANCHA
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
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
                            // Badges superiores
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DarkForestGreen))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "ABIERTO PARA INSCRIPCIÓN",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DarkForestGreen
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.95f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "Cancha 3",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Texto inferior sobre la imagen
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "⚽ FULBITO AMISTOSO MASCULINO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LimeNeon
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Pichanga Nocturna Fútbol 7 -\nAmigos del Balón",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    lineHeight = 23.sp
                                )
                            }
                        }
                    }
                }

                // 2. TARJETA: CUPOS DISPONIBLES Y PROGRESO
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Groups, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cupos disponibles", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                }
                                Text(
                                    "$currentConfirmedCount de 14",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // Barra de progreso verde
                            val progress = currentConfirmedCount / 14f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE5E7EB))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LimeNeon)
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🟢 Solo quedan ${14 - currentConfirmedCount} cupos libres",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}% completo",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // 3. DETALLES DEL ENCUENTRO
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Detalles del encuentro", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
                        }

                        Spacer(Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // 1. Fecha y hora
                                DetailRow(
                                    icon = Icons.Outlined.CalendarToday,
                                    label = "FECHA Y HORA",
                                    value = "Viernes 28 de Febrero, 20:00 hrs",
                                    subtitle = "Duración: 60 minutos continuos (2 tiempos de 25m)"
                                )
                                HorizontalDivider(color = BorderGray, thickness = 0.7.dp, modifier = Modifier.padding(vertical = 12.dp))

                                // 2. Sede
                                DetailRow(
                                    icon = Icons.Outlined.LocationOn,
                                    label = "SEDE",
                                    value = "Complejo Deportivo El Oasis",
                                    subtitle = "Cancha Sintética 3, Chiclayo",
                                    actionText = "Ver en mapa ↗",
                                    onActionClick = { navController.navigate(Screen.SearchMatch.route) }
                                )
                                HorizontalDivider(color = BorderGray, thickness = 0.7.dp, modifier = Modifier.padding(vertical = 12.dp))

                                // 3. Modalidad
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.Sports, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("MODALIDAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                        Spacer(Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                                Text("Fútbol 7 (F7)", color = DarkForestGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                            Spacer(Modifier.width(6.dp))
                                            Text("Con arbitraje amateur colegiado", fontSize = 11.5.sp, color = TextSecondary)
                                        }
                                    }
                                }
                                HorizontalDivider(color = BorderGray, thickness = 0.7.dp, modifier = Modifier.padding(vertical = 12.dp))

                                // 4. Costo
                                DetailRow(
                                    icon = Icons.Outlined.Payments,
                                    label = "COSTO / CUOTA",
                                    value = "S/. 12.00",
                                    subtitle = "por jugador (incluye hidratación y chalecos)"
                                )
                            }
                        }
                    }
                }

                // 4. PARTICIPANTES CONFIRMADOS
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Groups, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Participantes confirmados", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
                            }
                            Text("$currentConfirmedCount futbolistas", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                        }

                        Spacer(Modifier.height(10.dp))

                        // Grilla de Jugadores 2 columnas
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (i in players.indices step 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PlayerBadgeCard(player = players[i], modifier = Modifier.weight(1f))
                                    if (i + 1 < players.size) {
                                        PlayerBadgeCard(player = players[i + 1], modifier = Modifier.weight(1f))
                                    } else {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Tarjeta de cupos vacantes disponibles
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFAFAFA),
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.AddCircleOutline, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("${14 - currentConfirmedCount} cupos vacantes disponibles", fontSize = 12.sp, color = TextPrimary)
                                }

                                Surface(
                                    onClick = {
                                        if (!isEnrolled) {
                                            isEnrolled = true
                                            currentConfirmedCount++
                                            Toast.makeText(context, "¡Inscripción confirmada para la pichanga!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "INSCRÍBETE AHORA",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. REGLAMENTO Y CONDICIONES
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Shield, null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Reglamento y Condiciones de Pichanga", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Llegar 15 minutos antes para armado de equipos equitativos. Uso obligatorio de zapatillas para gras sintético (sin toperoles de aluminio).",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // --- BARRA INFERIOR FIJA: CUOTA Y BOTÓN DE INSCRIPCIÓN ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CUOTA INDIVIDUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("S/. 12.00", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Spacer(Modifier.width(4.dp))
                            Text("Pago en cancha / Yape", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Medium)
                        }
                    }

                    Button(
                        onClick = {
                            isEnrolled = !isEnrolled
                            if (isEnrolled) {
                                currentConfirmedCount++
                                Toast.makeText(context, "¡Inscrito en la Pichanga! Te esperamos en El Oasis Cancha 3", Toast.LENGTH_LONG).show()
                            } else {
                                currentConfirmedCount--
                                Toast.makeText(context, "Inscripción cancelada", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnrolled) Color(0xFF16A34A) else DarkForestGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("match_detail_enroll_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.SportsSoccer, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isEnrolled) "Inscrito ✓" else "Inscribirme al partido",
                                fontSize = 13.5.sp,
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

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subtitle: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Text(value, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 11.5.sp, color = TextSecondary)
        }

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
private fun PlayerBadgeCard(player: ConfirmedPlayer, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (player.isOrganizer) DarkForestGreen else Color(0xFF1F2937)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(player.initials, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Surface(
                    color = LimeNeon,
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                ) {
                    Text(player.positionTag, fontSize = 7.sp, fontWeight = FontWeight.Black, color = DarkForestGreen, modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.dp))
                }
            }

            Spacer(Modifier.width(8.dp))

            Column {
                Text(player.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                Text(
                    text = player.role,
                    fontSize = 10.5.sp,
                    color = if (player.isOrganizer) TextSecondary else Color(0xFF16A34A),
                    fontWeight = if (player.isOrganizer) FontWeight.Normal else FontWeight.Medium
                )
            }
        }
    }
}
