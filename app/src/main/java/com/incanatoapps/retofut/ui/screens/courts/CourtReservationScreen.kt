package com.incanatoapps.retofut.ui.screens.courts

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

data class TimeSlot(
    val timeRange: String,
    val price: String,
    val status: String, // Disponible, Casi lleno, Horario estelar
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourtReservationScreen(
    navController: NavHostController,
    courtId: Int = 1
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var selectedSlotIndex by remember { mutableIntStateOf(2) } // 20:00 - 21:00 (Turno Seleccionado)
    var isFavorite by remember { mutableStateOf(false) }

    val days = listOf("HOY\nVie 28", "MAÑANA\nSáb 01", "PRÓXIMO\nDom 02")

    val slots = remember {
        listOf(
            TimeSlot("18:00 - 19:00", "S/. 100", "Disponible"),
            TimeSlot("19:00 - 20:00", "S/. 120", "Casi lleno"),
            TimeSlot("20:00 - 21:00", "S/. 120", "Horario estelar", isSelected = true),
            TimeSlot("21:00 - 22:00", "S/. 110", "Disponible")
        )
    }

    val amenities = listOf(
        Pair("🌿", "Césped Sintético"),
        Pair("⚽", "Fútbol 7 & 5"),
        Pair("🅿️", "Estacionamiento vigilado"),
        Pair("🚿", "Duchas y vestuarios"),
        Pair("☕", "Cafetín / Hidratación"),
        Pair("💡", "Iluminación LED HD")
    )

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL TURNO (1H)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "S/. 120.00",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "/ hora",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "¡Turno reservado en Complejo El Oasis! Procediendo al pago de cuotas...", Toast.LENGTH_SHORT).show()
                            navController.navigate("payment/1")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("reserve_court_btn")
                    ) {
                        Text(
                            text = "Reservar Cancha",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. HERO FOTOGRAFÍA NOCTURNA ILUMINADA
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    // Fondo estilizado de campo sintético nocturno con focos LED
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF071F12),
                                    Color(0xFF0F3D24),
                                    Color(0xFF14532D)
                                )
                            )
                        )
                        // Líneas de cancha nocturna
                        val stroke = Stroke(width = 2.dp.toPx())
                        drawRect(
                            color = Color.White.copy(alpha = 0.25f),
                            topLeft = Offset(20f, 60f),
                            size = Size(size.width - 40f, size.height - 80f),
                            style = stroke
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.25f),
                            center = Offset(size.width / 2, size.height / 2 + 10f),
                            radius = 60f,
                            style = stroke
                        )
                        // Luces de reflectores LED superiores
                        drawCircle(
                            color = Color(0xFFFEF08A).copy(alpha = 0.35f),
                            center = Offset(size.width * 0.25f, 30f),
                            radius = 90f
                        )
                        drawCircle(
                            color = Color(0xFFFEF08A).copy(alpha = 0.35f),
                            center = Offset(size.width * 0.75f, 30f),
                            radius = 90f
                        )
                    }

                    // Botones de acción flotantes superiores
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Compartiendo ficha de la cancha Complejo El Oasis", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = Color.White)
                            }

                            IconButton(
                                onClick = {
                                    isFavorite = !isFavorite
                                    Toast.makeText(context, if (isFavorite) "Añadido a tus canchas favoritas" else "Removido de favoritos", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (isFavorite) Color(0xFFEF4444) else Color.White
                                )
                            }
                        }
                    }

                    // Badges flotantes en la parte inferior de la imagen
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFF0F3D24).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, LimeNeon)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "ABIERTO HOY HASTA LAS 23:30",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚽ GRAS SINTÉTICO PRO 50MM",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                            }
                        }
                    }
                }
            }

            // 2. DETALLES DE LA SEDE Y RESEÑAS
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Verified, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "SEDE VERIFICADA OFICIAL",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Complejo Deportivo El Oasis",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Star, null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(3.dp))
                                Text("4.8", fontWeight = FontWeight.Black, fontSize = 11.5.sp, color = ForestGreen)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "(120 reseñas de futbolistas)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("•", color = TextSecondary)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Ver opiniones",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Mostrando opiniones de capitanes de Chiclayo", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Tarjeta de Ubicación
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Place, null, tint = DarkForestGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Chiclayo, Lambayeque",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Av. Los Tréboles 450, Urb. Santa Victoria",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                            Spacer(Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.DirectionsCar, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("10 min en auto desde el centro", fontSize = 11.sp, color = TextSecondary)
                                }

                                Button(
                                    onClick = {
                                        try {
                                            val gmmIntentUri = Uri.parse("geo:0,0?q=Av.+Los+Tréboles+450,+Chiclayo")
                                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                            context.startActivity(mapIntent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Abriendo mapa hacia Urb. Santa Victoria...", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF3F4F6),
                                        contentColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Outlined.Navigation, null, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Cómo llegar", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. SERVICIOS E INFRAESTRUCTURA (6 ACTIVOS)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios e Infraestructura",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "6 ACTIVOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Chips de comodidades en 2 columnas
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        amenities.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (emoji, label) ->
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, BorderGray),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(emoji, fontSize = 13.sp)
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = label,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. HORARIOS DISPONIBLES (HOY) [CANCHA 1]
            item {
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarMonth, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Horarios Disponibles",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "CANCHA 1",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Selector de Días (HOY Vie 28, MAÑANA Sáb 01, PRÓXIMO Dom 02)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        days.forEachIndexed { index, dayLabel ->
                            val isSelected = selectedDayIndex == index
                            Surface(
                                onClick = { selectedDayIndex = index },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) DarkForestGreen else Color.White,
                                border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = dayLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Lista de Franjas Horarias
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        slots.forEachIndexed { idx, slot ->
                            val isSelected = selectedSlotIndex == idx
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSlotIndex = idx },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.5.dp, if (isSelected) LimeNeon else BorderGray)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    if (isSelected) {
                                        Surface(
                                            color = DarkForestGreen,
                                            shape = RoundedCornerShape(50),
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Outlined.Check, null, tint = LimeNeon, modifier = Modifier.size(12.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = "TURNO SELECCIONADO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = LimeNeon
                                                )
                                            }
                                        }
                                    }

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
                                                    .background(if (isSelected) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    if (isSelected) Icons.Outlined.SportsSoccer else Icons.Outlined.AccessTime,
                                                    contentDescription = null,
                                                    tint = if (isSelected) ForestGreen else TextSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                            Spacer(Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = slot.timeRange,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 14.sp,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = when (slot.status) {
                                                        "Horario estelar" -> "Horario estelar"
                                                        "Casi lleno" -> "• Casi lleno"
                                                        else -> "Disponible"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = when (slot.status) {
                                                        "Horario estelar" -> ForestGreen
                                                        "Casi lleno" -> Color(0xFFB45309)
                                                        else -> Color(0xFF16A34A)
                                                    }
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = slot.price,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "por hora",
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. LOCALIZACIÓN Y ACCESOS (MINI MAPA)
            item {
                Spacer(Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Map, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Localización y Accesos",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Mini mapa vectorial simulado
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(Color(0xFFEFF6EF))
                                // Calles
                                val roadColor = Color.White
                                drawRect(roadColor, topLeft = Offset(0f, 40f), size = Size(size.width, 24f))
                                drawRect(roadColor, topLeft = Offset(0f, 100f), size = Size(size.width, 30f))
                                drawRect(roadColor, topLeft = Offset(size.width * 0.45f, 0f), size = Size(26f, size.height))
                                drawRect(roadColor, topLeft = Offset(size.width * 0.75f, 0f), size = Size(20f, size.height))
                            }

                            // Pin central de la cancha
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DarkForestGreen)
                                    .border(2.dp, LimeNeon, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(22.dp))
                            }

                            // Tarjeta flotante inferior en el mapa
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Column {
                                            Text("El Oasis - Cancha 1 & 2", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                                            Text("Urb. Santa Victoria", fontSize = 9.5.sp, color = TextSecondary)
                                        }
                                    }

                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                        Text("10 min", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
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
