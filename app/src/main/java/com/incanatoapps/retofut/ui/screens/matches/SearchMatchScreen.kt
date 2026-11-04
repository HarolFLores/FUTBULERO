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
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
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
    val sessionManager = remember { SessionManager(context) }
    val isGuest = remember { sessionManager.isGuest() }
    var showGuestDialog by remember { mutableStateOf(false) }
    var showJoinTeamDialog by remember { mutableStateOf(false) }
    var selectedMatchToJoin by remember { mutableStateOf<MapMatchItem?>(null) }
    var chosenTeam by remember { mutableStateOf("Los Galácticos FC (Equipo A)") }
    var chosenPosition by remember { mutableStateOf("DEL (Delantero)") }
    var pendingRequestsByMatch by remember { mutableStateOf(mapOf<Int, String>()) }

    BackHandler { navController.popBackStack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Hoy") }
    var selectedMapPin by remember { mutableStateOf("Complejo San Juan") }
    // Paginación y carga diferida por partes para la lista de partidos
    var visibleMatchesCount by remember { mutableIntStateOf(3) }

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
                    navController.navigate(Screen.Notifications.route)
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

            // Banner de Modo Invitado
            if (isGuest) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Modo Invitado • Viendo dónde se arman las pichangas",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E40AF),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Acceder",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen,
                            modifier = Modifier.clickable { navController.navigate(Screen.Login.route) }
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

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
                        color = if (isSelected) ForestGreen else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isSelected) ForestGreen else BorderGray)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (filter == "Hoy") {
                                Icon(
                                    Icons.Outlined.CalendarToday,
                                    null,
                                    tint = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                text = filter,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
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

                    // Tarjetas de partidos con key única y paginación
                    items(matches.take(visibleMatchesCount), key = { it.id }) { match ->
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

                                    val isPending = pendingRequestsByMatch.containsKey(match.id)
                                    Button(
                                        onClick = {
                                            if (isHighlighted) {
                                                if (isGuest) {
                                                    showGuestDialog = true
                                                } else if (isPending) {
                                                    Toast.makeText(context, "Ya tienes una solicitud PENDIENTE para ${pendingRequestsByMatch[match.id]}. Esperando aprobación del Capitán.", Toast.LENGTH_LONG).show()
                                                } else {
                                                    selectedMatchToJoin = match
                                                    showJoinTeamDialog = true
                                                }
                                            } else {
                                                navController.navigate("match_detail/${match.id}")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPending) Color(0xFFD97706) else DarkForestGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = when {
                                                    isPending -> "Pendiente ⏳"
                                                    isHighlighted -> "Inscribirse"
                                                    else -> "Ver partido"
                                                },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (visibleMatchesCount < matches.size) {
                        item(key = "load_more_matches_btn") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(
                                    onClick = { visibleMatchesCount = (visibleMatchesCount + 3).coerceAtMost(matches.size) },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkForestGreen)
                                ) {
                                    Icon(Icons.Default.ExpandMore, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Cargar más pichangas (${matches.size - visibleMatchesCount} restantes)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Restricción para Invitado en Búsqueda de Partidos
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Inscripción a Pichanga",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = DarkForestGreen
                )
            },
            text = {
                Text(
                    text = "Estás explorando en Modo Invitado. Para inscribirte y reservar tu cupo en esta pichanga necesitas iniciar sesión con tu cuenta.",
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGuestDialog = false
                        navController.navigate(Screen.Login.route)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestDialog = false }) {
                    Text("Seguir Explorando", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // Modal para que un JUGADOR_LIBRE elija a cuál de los dos equipos solicitar unirse
    if (showJoinTeamDialog && selectedMatchToJoin != null) {
        val match = selectedMatchToJoin!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { showJoinTeamDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Groups,
                            contentDescription = null,
                            tint = DarkForestGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Solicitar Unirse al Partido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = DarkForestGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Como Jugador Libre, elige a cuál de los dos equipos deseas incorporarte. Tu solicitud quedará en estado PENDIENTE de aceptación por el Capitán.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    // Opciones de Equipos
                    Text(
                        "SELECCIONA EQUIPO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))

                    val teams = listOf(
                        Pair("Los Galácticos FC (Equipo A)", "Capitán: Piero M. • 2 cupos libres"),
                        Pair("Rayo Chiclayano (Equipo B)", "Capitán: Mateo F. • 1 cupo libre")
                    )

                    teams.forEach { (teamName, teamSubtitle) ->
                        val isSelected = chosenTeam == teamName
                        Surface(
                            onClick = { chosenTeam = teamName },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF9FAFB),
                            border = BorderStroke(1.5.dp, if (isSelected) ForestGreen else BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { chosenTeam = teamName },
                                    colors = RadioButtonDefaults.colors(selectedColor = DarkForestGreen)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(teamName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimary)
                                    Text(teamSubtitle, fontSize = 11.sp, color = if (isSelected) ForestGreen else TextSecondary)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Selector de posición
                    Text(
                        "TU POSICIÓN PREFERIDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("DEL", "MED", "DEF", "ARQ").forEach { pos ->
                            val isPosSelected = chosenPosition.startsWith(pos)
                            Surface(
                                onClick = { chosenPosition = pos },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isPosSelected) DarkForestGreen else Color(0xFFF3F4F6),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        pos,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPosSelected) LimeNeon else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showJoinTeamDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                pendingRequestsByMatch = pendingRequestsByMatch + (match.id to chosenTeam)
                                showJoinTeamDialog = false
                                Toast.makeText(
                                    context,
                                    "¡Solicitud enviada al Capitán de $chosenTeam! Estado: PENDIENTE ⏳",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Text("Enviar Solicitud", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
