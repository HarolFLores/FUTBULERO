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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class MapMatchItem(
    val id: Int,
    val name: String,
    val location: String,
    val distance: String,
    val time: String,
    val price: String,
    val spotsLeft: Int,
    val currentPlayers: Int,
    val totalPlayers: Int,
    val avatars: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMatchScreen(navController: NavHostController) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Hoy") }
    var selectedMapPin by remember { mutableStateOf("Complejo San Juan") }

    val filterOptions = listOf("Hoy", "Fin de semana", "Fútbol 7", "Canchas sintéticas", "Pimentel")

    val matches = remember {
        listOf(
            MapMatchItem(
                id = 1,
                name = "Complejo San Juan - Cancha 2",
                location = "A 1.2 km de tu ubicación",
                distance = "1.2 km",
                time = "Hoy 19:30 hrs",
                price = "S/. 12.00",
                spotsLeft = 3,
                currentPlayers = 11,
                totalPlayers = 14,
                avatars = listOf("JP", "CM", "AL", "+8")
            ),
            MapMatchItem(
                id = 2,
                name = "Pichanga Patazca F6",
                location = "A 2.5 km • Cancha Grass Sintético",
                distance = "2.5 km",
                time = "Hoy 21:00 hrs",
                price = "S/. 10.00",
                spotsLeft = 2,
                currentPlayers = 10,
                totalPlayers = 12,
                avatars = listOf("AR", "FG", "MT", "+7")
            ),
            MapMatchItem(
                id = 3,
                name = "Cancha El Gol - Nocturna",
                location = "A 3.1 km • Urb. La Victoria",
                distance = "3.1 km",
                time = "Hoy 22:00 hrs",
                price = "S/. 12.00",
                spotsLeft = 4,
                currentPlayers = 10,
                totalPlayers = 14,
                avatars = listOf("DR", "PL", "KS", "+6")
            )
        )
    }

    Scaffold(
        containerColor = Color.White,
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Regresar",
                        tint = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkForestGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = null,
                        tint = LimeNeon,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "ChocoFulbo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF111827)
                )

                Spacer(Modifier.weight(1f))

                IconButton(onClick = {
                    Toast.makeText(context, "No tienes notificaciones nuevas", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = TextPrimary)
                }

                IconButton(onClick = {
                    Toast.makeText(context, "Explora pichangas con cupos libres en el mapa de Chiclayo", Toast.LENGTH_LONG).show()
                }) {
                    Icon(Icons.Outlined.HelpOutline, contentDescription = "Ayuda", tint = TextPrimary)
                }
            }

            // --- BÚSQUEDA ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Buscar pichangas o complejos...", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, null, tint = Color(0xFF9CA3AF))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input_map"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable {
                            Toast.makeText(context, "Filtros aplicados", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Filtros", tint = TextPrimary)
                }
            }

            Spacer(Modifier.height(10.dp))

            // --- CHIPS DE FILTRO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        onClick = { selectedFilter = filter },
                        color = if (isSelected) LimeNeon else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isSelected) LimeNeon else BorderGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (filter == "Hoy") {
                                Icon(
                                    Icons.Outlined.CalendarToday,
                                    null,
                                    tint = if (isSelected) DarkForestGreen else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                text = filter,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkForestGreen else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- SECCIÓN MAPA INTERACTIVO DE CHICLAYO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFFE8ECE9))
            ) {
                // Dibujo de calles y bloques tipo mapa Chiclayo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val roadColor = Color(0xFFF7F9F7)
                    val blockColor = Color(0xFFDCE4DD)
                    val mainAvenue = Color(0xFFFFFFFF)

                    // Bloques de manzanas
                    drawRoundRect(
                        color = blockColor,
                        topLeft = Offset(20.dp.toPx(), 40.dp.toPx()),
                        size = Size(100.dp.toPx(), 70.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                    drawRoundRect(
                        color = blockColor,
                        topLeft = Offset(70.dp.toPx(), 140.dp.toPx()),
                        size = Size(90.dp.toPx(), 50.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )

                    // Calles
                    drawLine(roadColor, Offset(0f, 60.dp.toPx()), Offset(size.width, 100.dp.toPx()), strokeWidth = 14.dp.toPx())
                    drawLine(mainAvenue, Offset(0f, 150.dp.toPx()), Offset(size.width, 140.dp.toPx()), strokeWidth = 16.dp.toPx())
                    drawLine(roadColor, Offset(150.dp.toPx(), 0f), Offset(130.dp.toPx(), size.height), strokeWidth = 14.dp.toPx())
                    drawLine(roadColor, Offset(260.dp.toPx(), 0f), Offset(280.dp.toPx(), size.height), strokeWidth = 14.dp.toPx())

                    // Círculo de área de usuario
                    drawCircle(
                        color = LimeNeon.copy(alpha = 0.45f),
                        radius = 24.dp.toPx(),
                        center = Offset(110.dp.toPx(), 120.dp.toPx())
                    )
                    drawCircle(
                        color = ForestGreen,
                        radius = 8.dp.toPx(),
                        center = Offset(110.dp.toPx(), 120.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(110.dp.toPx(), 120.dp.toPx())
                    )
                }

                // Botones flotantes del mapa (superior derecho)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(4.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .clickable {
                                Toast.makeText(context, "Centrado en tu ubicación", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.MyLocation, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(4.dp, RoundedCornerShape(10.dp))
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .clickable {
                                Toast.makeText(context, "Cambiando vista de mapa", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Layers, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                    }
                }

                // PIN 1: Cancha El Gol
                Column(
                    modifier = Modifier
                        .offset(x = 30.dp, y = 70.dp)
                        .clickable { selectedMapPin = "Cancha El Gol" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                    }
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Cancha El Gol",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // PIN 2: Patazca F6
                Column(
                    modifier = Modifier
                        .offset(x = 230.dp, y = 65.dp)
                        .clickable { selectedMapPin = "Patazca F6" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                    }
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Patazca F6",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // PIN 3: Complejo San Juan (Seleccionado con Popover badge grande)
                Column(
                    modifier = Modifier
                        .offset(x = 85.dp, y = 14.dp)
                        .clickable { selectedMapPin = "Complejo San Juan" },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge destacado
                    Surface(
                        color = DarkForestGreen,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Complejo San Juan",
                                color = LimeNeon,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                            Text(
                                text = " • S/. 12 • ",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                            Surface(
                                color = Color(0xFFDC2626),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Faltan 3",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Triángulo apuntando al pin
                    Canvas(modifier = Modifier.size(10.dp, 6.dp)) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width / 2, size.height)
                            close()
                        }
                        drawPath(path, DarkForestGreen)
                    }

                    // Pin círculo
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen)
                            .border(2.dp, LimeNeon, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.SportsSoccer, null, tint = LimeNeon, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // --- BOTTOM DRAWER / LISTA DE PICHANGAS ABIERTAS ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 10.dp
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Barra de agarre
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(BorderGray)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    // Encabezado de la lista
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pichangas Abiertas",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = "Cerca de Chiclayo Centro",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "8 disponibles",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Tarjetas de partidos
                    items(matches) { match ->
                        val isHighlighted = match.id == 1
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    navController.navigate("match_detail/${match.id}")
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = if (isHighlighted) BorderStroke(1.8.dp, LimeNeon) else BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Título y Cupos faltantes
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = match.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Surface(
                                        color = Color(0xFFFEE2E2),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Outlined.Groups, null, tint = Color(0xFFDC2626), modifier = Modifier.size(13.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "Faltan ${match.spotsLeft} jugadores",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(4.dp))

                                // Distancia
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.NearMe, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(match.location, fontSize = 12.sp, color = TextSecondary)
                                }

                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
                                Spacer(Modifier.height(10.dp))

                                // Horario y Cuota
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF3F4F6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Schedule, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("HORARIO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                            Text(match.time, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF3F4F6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Payments, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text("CUOTA X JUGADOR", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                            Text(match.price, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Avatares y Botón Inscribirse
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Stack de avatares
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(modifier = Modifier.padding(end = 6.dp)) {
                                            match.avatars.forEachIndexed { idx, av ->
                                                Box(
                                                    modifier = Modifier
                                                        .offset(x = (-idx * 6).dp)
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(if (idx == 0) DarkForestGreen else Color(0xFF4B5563))
                                                        .border(1.5.dp, Color.White, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(av, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${match.currentPlayers} / ${match.totalPlayers} listos",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate("match_detail/${match.id}")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(if (isHighlighted) "Inscribirse" else "Ver partido", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.width(4.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(13.dp))
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
}
