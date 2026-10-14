package com.incanatoapps.retofut.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.incanatoapps.retofut.R
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse

@Composable
fun FifaCard(card: PlayerCardResponse, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }

    // Configurar un ImageLoader personalizado que incluya el token de autenticación
    val imageLoader = remember(token) {
        ImageLoader.Builder(context)
            .okHttpClient {
                RetrofitClient.getOkHttpClient(token)
            }
            .build()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val isGold = card.rating >= 80
    val isIcon = card.rating >= 90

    val cardGradient = when {
        isIcon -> Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0), Color(0xFF94A3B8)))
        isGold -> Brush.verticalGradient(listOf(Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFFB45309)))
        else -> Brush.verticalGradient(listOf(Color(0xFFE5E7EB), Color(0xFF9CA3AF), Color(0xFF4B5563)))
    }

    val borderColor = when {
        isIcon -> Color(0xFF64748B)
        isGold -> Color(0xFF78350F)
        else -> Color(0xFF1F2937)
    }

    val cardShape = GenericShape { size, _ ->
        moveTo(0f, size.height * 0.08f)
        lineTo(size.width * 0.12f, 0f)
        lineTo(size.width * 0.88f, 0f)
        lineTo(size.width, size.height * 0.08f)
        lineTo(size.width, size.height * 0.82f)
        lineTo(size.width * 0.5f, size.height)
        lineTo(0f, size.height * 0.82f)
        close()
    }

    Box(
        modifier = modifier
            .width(280.dp)
            .height(400.dp)
            .graphicsLayer {
                shadowElevation = 20.dp.toPx()
                shape = cardShape
                clip = true
            }
            .background(cardGradient)
            .border(4.dp, borderColor.copy(alpha = 0.8f), cardShape)
    ) {
        // Efecto de brillo premium
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = shimmerAlpha * 0.2f),
                            Color.White.copy(alpha = 0.0f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info Lateral: Rating, Posición, Bandera
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = card.rating.toString(),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        color = borderColor,
                        lineHeight = 52.sp
                    )
                    Text(
                        text = card.posicion ?: "EXT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = borderColor
                    )
                    Spacer(Modifier.height(8.dp))
                    // Bandera Placeholder
                    Box(Modifier.size(24.dp, 16.dp).background(borderColor.copy(alpha = 0.2f)))
                    Spacer(Modifier.height(4.dp))
                    // Logo Club Placeholder
                    Box(Modifier.size(24.dp).clip(CircleShape).background(borderColor.copy(alpha = 0.2f)))
                }
                
                Spacer(Modifier.width(16.dp))
                
                // Foto del Jugador con Marco Premium
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, borderColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(card.foto)
                            .crossfade(true)
                            .build(),
                        imageLoader = imageLoader, // Usamos el loader con token
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().scale(1.2f),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.ic_launcher_foreground)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            
            // Nombre Estilizado
            Text(
                text = (card.nombre.split(" ").firstOrNull() ?: "PRO PLAYER").uppercase(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = borderColor,
                letterSpacing = 2.sp
            )
            
            Spacer(Modifier.height(8.dp))
            Box(Modifier.width(200.dp).height(2.dp).background(borderColor.copy(alpha = 0.3f)))
            Spacer(Modifier.height(20.dp))

            // Stats Principales con Iconos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AttributeRow("PAC", card.stats.PAC, Icons.Default.ElectricBolt)
                    AttributeRow("SHO", card.stats.SHO, Icons.Default.SportsSoccer)
                    AttributeRow("PAS", card.stats.PAS, Icons.Default.SportsSoccer)
                }

                Box(Modifier.width(2.dp).height(90.dp).background(borderColor.copy(alpha = 0.2f)))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AttributeRow("DRI", card.stats.DRI, Icons.Default.ElectricBolt)
                    AttributeRow("DEF", card.stats.DEF, Icons.Default.Shield)
                    AttributeRow("PHY", card.stats.PHY, Icons.Default.Shield)
                }
            }

            Spacer(Modifier.weight(1f))

            // Footer de Rendimiento
            Surface(
                color = borderColor.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatFooterItem("PARTIDOS", card.performance.partidosJugados, borderColor)
                    StatFooterItem("GOLES", card.performance.goles, borderColor)
                    StatFooterItem("ASIST.", card.performance.asistencias, borderColor)
                }
            }
        }
    }
}

@Composable
private fun AttributeRow(label: String, value: Int, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = value.toString(),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color(0xFF1F2937),
            modifier = Modifier.width(28.dp)
        )
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937).copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StatFooterItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = color.copy(alpha = 0.7f))
    }
}
