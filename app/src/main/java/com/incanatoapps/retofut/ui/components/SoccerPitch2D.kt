package com.incanatoapps.retofut.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.DarkForestGreen
import com.incanatoapps.retofut.ui.theme.ForestGreen
import com.incanatoapps.retofut.ui.theme.LimeNeon

data class PitchPlayer(
    val id: Int,
    val name: String,
    val number: Int,
    val position: String, // DEL, MED, DEF, POR
    val rating: Int = 80,
    val isCaptain: Boolean = false,
    val isAnonymous: Boolean = false,
    val teamSide: String = "A" // "A" = Local, "B" = Visitante
)

enum class PitchViewMode {
    TEAM_A,
    TEAM_B,
    FULL_PITCH
}

/**
 * Cancha de Fútbol 2D en Compose con renderizado Canvas de alta precisión
 * y colocación táctica de jugadores registrados y anónimos según el formato del partido.
 */
@Composable
fun SoccerPitch2D(
    teamAPlayers: List<PitchPlayer>,
    teamBPlayers: List<PitchPlayer>,
    matchFormat: String = "Fútbol 7",
    modifier: Modifier = Modifier,
    onPlayerClick: ((PitchPlayer) -> Unit)? = null
) {
    var viewMode by remember { mutableStateOf(PitchViewMode.FULL_PITCH) }
    var selectedPlayerForDialog by remember { mutableStateOf<PitchPlayer?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F3D24))
            .padding(12.dp)
    ) {
        // Selector de Visualización Táctica
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.SportsSoccer,
                    contentDescription = null,
                    tint = LimeNeon,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "PIZARRA TÁCTICA 2D ($matchFormat)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            // Chips para alternar vista
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    Pair(PitchViewMode.FULL_PITCH, "Ambos"),
                    Pair(PitchViewMode.TEAM_A, "Local"),
                    Pair(PitchViewMode.TEAM_B, "Visita")
                ).forEach { (mode, label) ->
                    val isSelected = viewMode == mode
                    Surface(
                        onClick = { viewMode = mode },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ForestGreen else Color(0xFF14532D),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Leyenda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(LimeNeon))
            Spacer(Modifier.width(4.dp))
            Text("Capitán (C)", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.8f))
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
            Spacer(Modifier.width(4.dp))
            Text("Invitado Anónimo", fontSize = 9.5.sp, color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(12.dp))
            Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White))
            Spacer(Modifier.width(4.dp))
            Text("Registrado", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.8f))
        }

        // --- CANCHA 2D EN CANVAS ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
        ) {
            val pitchWidth = maxWidth
            val pitchHeight = maxHeight

            // Dibujo de la Cancha de Césped con líneas oficiales
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Franjas alternadas de corte de césped
                val stripes = 10
                val stripeHeight = h / stripes
                val lightGrass = Color(0xFF15803D)
                val darkGrass = Color(0xFF166534)
                for (i in 0 until stripes) {
                    drawRect(
                        color = if (i % 2 == 0) lightGrass else darkGrass,
                        topLeft = Offset(0f, i * stripeHeight),
                        size = Size(w, stripeHeight)
                    )
                }

                val linePaintColor = Color.White.copy(alpha = 0.85f)
                val strokeWidth = 3.dp.toPx()
                val margin = 16.dp.toPx()

                // Líneas de límite de campo
                drawRect(
                    color = linePaintColor,
                    topLeft = Offset(margin, margin),
                    size = Size(w - 2 * margin, h - 2 * margin),
                    style = Stroke(width = strokeWidth)
                )

                // Línea Media
                val midY = h / 2f
                drawLine(
                    color = linePaintColor,
                    start = Offset(margin, midY),
                    end = Offset(w - margin, midY),
                    strokeWidth = strokeWidth
                )

                // Círculo Central y Punto Central
                val centerRadius = 45.dp.toPx()
                drawCircle(
                    color = linePaintColor,
                    radius = centerRadius,
                    center = Offset(w / 2f, midY),
                    style = Stroke(width = strokeWidth)
                )
                drawCircle(
                    color = linePaintColor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(w / 2f, midY)
                )

                // Área Grande Superior (Equipo A o Visitante)
                val boxWidth = w * 0.58f
                val boxHeight = h * 0.17f
                val boxLeft = (w - boxWidth) / 2f
                drawRect(
                    color = linePaintColor,
                    topLeft = Offset(boxLeft, margin),
                    size = Size(boxWidth, boxHeight),
                    style = Stroke(width = strokeWidth)
                )

                // Área Chica Superior
                val smallBoxWidth = w * 0.32f
                val smallBoxHeight = h * 0.07f
                val smallBoxLeft = (w - smallBoxWidth) / 2f
                drawRect(
                    color = linePaintColor,
                    topLeft = Offset(smallBoxLeft, margin),
                    size = Size(smallBoxWidth, smallBoxHeight),
                    style = Stroke(width = strokeWidth)
                )

                // Punto Penal Superior
                drawCircle(
                    color = linePaintColor,
                    radius = 3.dp.toPx(),
                    center = Offset(w / 2f, margin + boxHeight * 0.72f)
                )

                // Área Grande Inferior (Equipo B o Local)
                val bottomBoxTop = h - margin - boxHeight
                drawRect(
                    color = linePaintColor,
                    topLeft = Offset(boxLeft, bottomBoxTop),
                    size = Size(boxWidth, boxHeight),
                    style = Stroke(width = strokeWidth)
                )

                // Área Chica Inferior
                val bottomSmallBoxTop = h - margin - smallBoxHeight
                drawRect(
                    color = linePaintColor,
                    topLeft = Offset(smallBoxLeft, bottomSmallBoxTop),
                    size = Size(smallBoxWidth, smallBoxHeight),
                    style = Stroke(width = strokeWidth)
                )

                // Punto Penal Inferior
                drawCircle(
                    color = linePaintColor,
                    radius = 3.dp.toPx(),
                    center = Offset(w / 2f, h - margin - boxHeight * 0.72f)
                )

                // Arcos de esquina
                val cornerRadius = 12.dp.toPx()
                // Top-Left
                drawArc(
                    color = linePaintColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(margin - cornerRadius, margin - cornerRadius),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Top-Right
                drawArc(
                    color = linePaintColor,
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - margin - cornerRadius, margin - cornerRadius),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Bottom-Left
                drawArc(
                    color = linePaintColor,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(margin - cornerRadius, h - margin - cornerRadius),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidth)
                )
                // Bottom-Right
                drawArc(
                    color = linePaintColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w - margin - cornerRadius, h - margin - cornerRadius),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidth)
                )
            }

            // --- POSICIONAMIENTO DE LOS JUGADORES EN LA CANCHA ---
            // Coordenadas normalizadas (xRatio: 0.1..0.9, yRatio: 0.05..0.95)
            // Según la formación de Fútbol 7 (1 POR, 3 DEF, 2 MED, 1 DEL)
            // Mitad Superior: Equipo A (Rayo / Rival o Local según vista)
            // Mitad Inferior: Equipo B

            val showTeamA = viewMode == PitchViewMode.FULL_PITCH || viewMode == PitchViewMode.TEAM_A
            val showTeamB = viewMode == PitchViewMode.FULL_PITCH || viewMode == PitchViewMode.TEAM_B

            // Posiciones relativas para Equipo Superior (Mitad Arriba: 0.06f a 0.45f)
            val topTeamCoords = when (viewMode) {
                PitchViewMode.TEAM_A -> listOf(
                    // En vista de solo este equipo, se distribuye en toda la cancha (1 POR, 3 DEF, 2 MED, 1 DEL)
                    Offset(0.50f, 0.88f), // POR
                    Offset(0.20f, 0.68f), // DEF Izq
                    Offset(0.50f, 0.70f), // DEF Cen
                    Offset(0.80f, 0.68f), // DEF Der
                    Offset(0.33f, 0.42f), // MED Izq
                    Offset(0.67f, 0.42f), // MED Der
                    Offset(0.50f, 0.18f), // DEL
                    Offset(0.20f, 0.22f)  // Suplente / 8vo si aplica
                )
                else -> listOf(
                    Offset(0.50f, 0.07f), // POR
                    Offset(0.24f, 0.19f), // DEF Izq
                    Offset(0.50f, 0.17f), // DEF Cen
                    Offset(0.76f, 0.19f), // DEF Der
                    Offset(0.35f, 0.33f), // MED Izq
                    Offset(0.65f, 0.33f), // MED Der
                    Offset(0.50f, 0.44f), // DEL
                    Offset(0.80f, 0.44f)
                )
            }

            // Posiciones relativas para Equipo Inferior (Mitad Abajo: 0.55f a 0.94f)
            val bottomTeamCoords = when (viewMode) {
                PitchViewMode.TEAM_B -> listOf(
                    Offset(0.50f, 0.88f), // POR
                    Offset(0.20f, 0.68f), // DEF Izq
                    Offset(0.50f, 0.70f), // DEF Cen
                    Offset(0.80f, 0.68f), // DEF Der
                    Offset(0.33f, 0.42f), // MED Izq
                    Offset(0.67f, 0.42f), // MED Der
                    Offset(0.50f, 0.18f), // DEL
                    Offset(0.20f, 0.22f)
                )
                else -> listOf(
                    Offset(0.50f, 0.56f), // DEL
                    Offset(0.35f, 0.67f), // MED Izq
                    Offset(0.65f, 0.67f), // MED Der
                    Offset(0.24f, 0.81f), // DEF Izq
                    Offset(0.50f, 0.83f), // DEF Cen
                    Offset(0.76f, 0.81f), // DEF Der
                    Offset(0.50f, 0.93f), // POR
                    Offset(0.80f, 0.56f)
                )
            }

            // Renderizar Equipo A
            if (showTeamA) {
                teamAPlayers.forEachIndexed { index, player ->
                    val coord = topTeamCoords.getOrElse(index) { Offset(0.5f, 0.3f) }
                    val x = pitchWidth * coord.x
                    val y = pitchHeight * coord.y

                    PlayerPinOnPitch(
                        player = player,
                        isTeamA = true,
                        modifier = Modifier
                            .offset(x = x - 26.dp, y = y - 26.dp),
                        onClick = {
                            selectedPlayerForDialog = player
                            onPlayerClick?.invoke(player)
                        }
                    )
                }
            }

            // Renderizar Equipo B
            if (showTeamB) {
                teamBPlayers.forEachIndexed { index, player ->
                    val coord = bottomTeamCoords.getOrElse(index) { Offset(0.5f, 0.7f) }
                    val x = pitchWidth * coord.x
                    val y = pitchHeight * coord.y

                    PlayerPinOnPitch(
                        player = player,
                        isTeamA = false,
                        modifier = Modifier
                            .offset(x = x - 26.dp, y = y - 26.dp),
                        onClick = {
                            selectedPlayerForDialog = player
                            onPlayerClick?.invoke(player)
                        }
                    )
                }
            }
        }
    }

    // Modal de Detalle de Jugador al tocar la ficha en la Cancha
    if (selectedPlayerForDialog != null) {
        val player = selectedPlayerForDialog!!
        AlertDialog(
            onDismissRequest = { selectedPlayerForDialog = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (player.isAnonymous) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${player.number}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (player.isAnonymous) Color(0xFFB45309) else DarkForestGreen
                    )
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(player.name, fontWeight = FontWeight.Black, fontSize = 17.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = ForestGreen, shape = RoundedCornerShape(4.dp)) {
                            Text(player.position, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                        if (player.isCaptain) {
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                Text("CAPITÁN", color = Color(0xFF92400E), fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                            Spacer(Modifier.width(6.dp))
                        }
                        if (player.isAnonymous) {
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color(0xFFF59E0B))) {
                                Text("INVITADO ANÓNIMO", color = Color(0xFFB45309), fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        } else {
                            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                                Text("REGISTRADO ✓", color = Color(0xFF1E40AF), fontWeight = FontWeight.Black, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Rating General Estimado: ${player.rating} OVR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = DarkForestGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (player.isAnonymous) "Convocado directamente por el Capitán sin necesidad de cuenta registrada en FUTBULERO."
                        else "Futbolista activo con cuenta oficial y estadísticas vinculadas.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedPlayerForDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun PlayerPinOnPitch(
    player: PitchPlayer,
    isTeamA: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val jerseyBg = when {
        player.isAnonymous -> Color(0xFFF59E0B) // Ámbar dorado para anónimos
        isTeamA -> Color(0xFF0F3D24)          // Verde oscuro equipo A
        else -> Color(0xFF1E3A8A)             // Azul marino equipo B
    }

    val borderColor = when {
        player.isCaptain -> LimeNeon
        player.isAnonymous -> Color(0xFFFEF08A)
        else -> Color.White
    }

    Column(
        modifier = modifier
            .width(54.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ficha Circular
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(jerseyBg)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${player.number}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (player.isAnonymous) Color(0xFF78350F) else Color.White
            )

            // Distintivo Capitán
            if (player.isCaptain) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(LimeNeon),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "C",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )
                }
            }
        }

        Spacer(Modifier.height(2.dp))

        // Nombre / Apodo del jugador debajo
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.5.dp, if (player.isAnonymous) Color(0xFFF59E0B) else Color.White.copy(alpha = 0.5f))
        ) {
            Text(
                text = if (player.isAnonymous) "${player.name.take(6)}*" else player.name.split(" ").first().take(8),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (player.isAnonymous) Color(0xFFFDE68A) else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
            )
        }
    }
}
