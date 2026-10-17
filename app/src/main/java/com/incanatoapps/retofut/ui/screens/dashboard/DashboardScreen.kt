package com.incanatoapps.retofut.ui.screens.dashboard

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

data class AvailableMatch(
    val id: Int,
    val title: String,
    val format: String,
    val surface: String,
    val freeSpots: Int,
    val date: String,
    val location: String,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }
    val userName = remember { sessionManager.fetchUserName() ?: "Piero" }
    val userPosition = remember { sessionManager.fetchPreferredPosition() }

    var searchQuery by remember { mutableStateOf("") }
    var showMatchDetailsDialog by remember { mutableStateOf(false) }
    var selectedMatchForJoin by remember { mutableStateOf<AvailableMatch?>(null) }
    var showCreateMatchDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var joinedMatchIds by remember { mutableStateOf(setOf<Int>()) }

    // Redirigir a login si no hay sesión
    LaunchedEffect(token) {
        if (token == null) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val sampleMatches = remember {
        listOf(
            AvailableMatch(
                id = 1,
                title = "Pichanga Viernes Pimentel",
                format = "FÚTBOL 7 (F7)",
                surface = "Sintético",
                freeSpots = 3,
                date = "Viernes 28 Feb • 19:00 hrs",
                location = "Cancha Sintética El Faro, Pimentel",
                price = "S/. 12.00"
            ),
            AvailableMatch(
                id = 2,
                title = "Reto Sabatino: Los Galácticos",
                format = "FÚTBOL 6 (F6)",
                surface = "Sintético Premium",
                freeSpots = 2,
                date = "Sábado 1 Mar • 18:30 hrs",
                location = "Complejo Deportivo El Oasis, La Victoria",
                price = "S/. 10.00"
            ),
            AvailableMatch(
                id = 3,
                title = "Duelo Nocturno Patazca Central",
                format = "FÚTBOL 11 (F11)",
                surface = "Grass Natural",
                freeSpots = 4,
                date = "Domingo 2 Mar • 20:00 hrs",
                location = "Cancha Patazca, Chiclayo",
                price = "S/. 15.00"
            )
        )
    }

    val filteredMatches = remember(searchQuery, sampleMatches) {
        if (searchQuery.isBlank()) sampleMatches
        else sampleMatches.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true) ||
                    it.format.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = {
            ChocoFulboBottomBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // --- 1. BARRA SUPERIOR ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo ChocoFulbo Box
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.SportsSoccer,
                            contentDescription = null,
                            tint = LimeNeon,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "ChocoFulbo",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color(0xFF111827)
                    )

                    Spacer(Modifier.width(8.dp))

                    Surface(
                        color = LimeNeon.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "CHICLAYO AMATEUR",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Campanita de Notificaciones con punto verde
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6))
                            .clickable { showNotificationsDialog = true }
                            .testTag("notifications_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFF1F2937),
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(5.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LimeNeon)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Avatar de Jugador
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen)
                            .border(2.dp, LimeNeon, CircleShape)
                            .clickable { navController.navigate(Screen.Profile.route) }
                            .testTag("profile_avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // --- 2. SALUDO Y UBICACIÓN ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "¡Hola, $userName! 👋",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827)
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Chiclayo, Perú",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
            }

            // --- 3. BARRA DE BÚSQUEDA Y FILTRO ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Buscar partidos o equipos...", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF9CA3AF))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Limpiar", tint = Color(0xFF9CA3AF))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_matches_input"),
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

                    // Botón Filtro
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .clickable {
                                Toast.makeText(context, "Filtros aplicados para Chiclayo", Toast.LENGTH_SHORT).show()
                            }
                            .testTag("filter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Tune,
                            contentDescription = "Filtros",
                            tint = Color(0xFF1F2937),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
            }

            // --- 4. HERO CARD: PRÓXIMO PARTIDO ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { navController.navigate("match_detail/1") }
                        .testTag("next_match_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                // Arcos geométricos de fondo
                                val stroke = Stroke(width = 1.dp.toPx())
                                val color = Color.White.copy(alpha = 0.06f)
                                drawCircle(
                                    color = color,
                                    radius = size.width * 0.45f,
                                    center = Offset(size.width * 0.9f, size.height * 0.5f),
                                    style = stroke
                                )
                            }
                            .padding(20.dp)
                    ) {
                        Column {
                            // Fila Superior: Badge Próximo Partido + Titular
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(LimeNeon)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "PRÓXIMO PARTIDO",
                                        color = LimeNeon,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                Surface(
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "INSCRITO • TITULAR",
                                        color = DarkForestGreen,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Nombre del Partido
                            Text(
                                text = "Clásico Nocturno: Los Galácticos vs. Chiclayo FC",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                lineHeight = 22.sp
                            )

                            Spacer(Modifier.height(14.dp))

                            // Hora
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    tint = LimeNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Hoy, 20:30 hrs",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            // Cancha
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = LimeNeon,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Complejo Deportivo El Oasis - Cancha 2 (Fútbol 7)",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.5.sp
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            // Confirmados & Squad Completo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "14/14 confirmados",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Surface(
                                    color = Color(0xFF1B4D31),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, LimeNeon.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "Squad Completo",
                                        color = LimeNeon,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)
                            Spacer(Modifier.height(12.dp))

                            // Pie de Tarjeta: Camiseta y Ver Detalles
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Camiseta #10 • $userPosition",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { navController.navigate("match_detail/1") }
                                ) {
                                    Text(
                                        text = "Ver detalles",
                                        color = LimeNeon,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(14.dp))
            }

            // --- BANNER EN VIVO: MARCADOR EN VIVO ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { navController.navigate("live_match/1") }
                        .testTag("live_score_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, LimeNeon)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF1B4D31),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, LimeNeon)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                                Spacer(Modifier.width(4.dp))
                                Text("EN VIVO 82'", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = LimeNeon)
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Atl. Chiclayo 3 - 2 Los Caimanes FC", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Text("Torneo Clausura • El Oasis Cancha 2", fontSize = 11.sp, color = TextSecondary)
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ver en vivo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                                Spacer(Modifier.width(2.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkForestGreen, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            // --- 5. TRES ACCIONES RÁPIDAS ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Buscar partido
                    ActionCard(
                        title = "Buscar\npartido",
                        icon = Icons.Outlined.Explore,
                        iconBg = Color(0xFFF0FDF4),
                        iconTint = ForestGreen,
                        containerBg = Color.White,
                        textColor = TextPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Screen.SearchMatch.route)
                        }
                    )

                    // Mis equipos
                    ActionCard(
                        title = "Mis\nequipos",
                        icon = Icons.Outlined.Shield,
                        iconBg = Color(0xFFF0FDF4),
                        iconTint = ForestGreen,
                        containerBg = Color.White,
                        textColor = TextPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.MyTeam.route) }
                    )

                    // Crear partido
                    ActionCard(
                        title = "Crear\npartido",
                        icon = Icons.Outlined.Add,
                        iconBg = LimeNeon,
                        iconTint = DarkForestGreen,
                        containerBg = ForestGreen,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.CreateMatch.route) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(22.dp))
            }

            // --- 6. SECCIÓN: PARTIDOS DISPONIBLES ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Partidos disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111827)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFE5E7EB))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${filteredMatches.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }

                    Text(
                        text = "Ver todos ›",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            Toast.makeText(context, "Mostrando todos los partidos de Chiclayo", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
            }

            // Lista de Tarjetas de Partidos Disponibles
            items(filteredMatches, key = { it.id }) { match ->
                val isJoined = joinedMatchIds.contains(match.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Fila de tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFF3F4F6),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = match.format,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF374151),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "• ${match.surface}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            // Cupos libres pill
                            Surface(
                                color = if (isJoined) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = if (isJoined) "✓ Ya inscrito" else "● ${match.freeSpots} cupos libres",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isJoined) ForestGreen else Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Título del Partido
                        Text(
                            text = match.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(Modifier.height(6.dp))

                        // Fecha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarToday, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(match.date, fontSize = 12.5.sp, color = TextSecondary)
                        }

                        Spacer(Modifier.height(4.dp))

                        // Cancha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(match.location, fontSize = 12.5.sp, color = TextSecondary)
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = BorderGray, thickness = 1.dp)
                        Spacer(Modifier.height(12.dp))

                        // Fila de Cuota y Botones
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CUOTA POR JUGADOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Text(match.price, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { navController.navigate("match_detail/${match.id}") },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, BorderGray),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color(0xFFF9FAFB),
                                        contentColor = TextPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text("Detalles", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = {
                                        if (isJoined) {
                                            joinedMatchIds = joinedMatchIds - match.id
                                            Toast.makeText(context, "Cancelaste tu cupo", Toast.LENGTH_SHORT).show()
                                        } else {
                                            joinedMatchIds = joinedMatchIds + match.id
                                            Toast.makeText(context, "¡Te uniste a ${match.title}!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isJoined) Color(0xFF16A34A) else ForestGreen,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (isJoined) "Inscrito" else "Unirme", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Outlined.SportsSoccer, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
            }

            // --- 7. TARJETA TORNEO: COPA LA VICTORIA ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clickable { navController.navigate(Screen.Rankings.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícono Copa
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = LimeNeon,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "COPA LA VICTORIA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Torneo Relámpago",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Inscripciones abiertas • Premio S/. 1,500",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Flecha circular
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver torneo",
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Ver detalles del próximo partido
    if (showMatchDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showMatchDetailsDialog = false },
            title = {
                Text("Clásico Nocturno Chiclayo", fontWeight = FontWeight.Bold, color = DarkForestGreen)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Equipos: Los Galácticos vs. Chiclayo FC", fontSize = 13.sp)
                    Text("• Cancha: Complejo El Oasis - Cancha 2", fontSize = 13.sp)
                    Text("• Horario: Hoy 20:30 hrs (Llegada 20:15 hrs)", fontSize = 13.sp)
                    Text("• Árbitro: Colegiado oficial FPF Chiclayo", fontSize = 13.sp)
                    Text("• Tu dorsal: #10 (Titular - $userPosition)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMatchDetailsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }

    // Modal: Crear Pichanga / Partido
    if (showCreateMatchDialog) {
        var matchTitleInput by remember { mutableStateOf("") }
        var matchVenueInput by remember { mutableStateOf("Complejo El Oasis") }
        var matchPriceInput by remember { mutableStateOf("10.00") }

        AlertDialog(
            onDismissRequest = { showCreateMatchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.SportsSoccer, null, tint = ForestGreen)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Nuevo Partido", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = matchTitleInput,
                        onValueChange = { matchTitleInput = it },
                        label = { Text("Nombre de la pichanga") },
                        placeholder = { Text("Ej. Pichanga con amigos") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = matchVenueInput,
                        onValueChange = { matchVenueInput = it },
                        label = { Text("Cancha / Sede en Chiclayo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = matchPriceInput,
                        onValueChange = { matchPriceInput = it },
                        label = { Text("Cuota por jugador (S/.)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "¡Pichanga creada y publicada con éxito!", Toast.LENGTH_SHORT).show()
                        showCreateMatchDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publicar Partido")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateMatchDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }

    // Modal: Notificaciones
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Text("Notificaciones ChocoFulbo", fontWeight = FontWeight.Bold, color = DarkForestGreen)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🔔 Tu partido de hoy en El Oasis está confirmado a las 20:30 hrs.", fontSize = 13.sp)
                    Text("🏆 Se abrió la inscripción para la Copa La Victoria con S/. 1,500 en premios.", fontSize = 13.sp)
                    Text("👟 Nuevas canchas sintéticas agregadas en Pimentel.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }

    // Modal: Detalles de partido seleccionado
    selectedMatchForJoin?.let { match ->
        AlertDialog(
            onDismissRequest = { selectedMatchForJoin = null },
            title = {
                Text(match.title, fontWeight = FontWeight.Bold, color = DarkForestGreen)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Formato: ${match.format} (${match.surface})", fontSize = 13.sp)
                    Text("• Cancha: ${match.location}", fontSize = 13.sp)
                    Text("• Fecha y hora: ${match.date}", fontSize = 13.sp)
                    Text("• Cuota: ${match.price} por persona", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    Text("• Cupos disponibles: ${match.freeSpots}", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        joinedMatchIds = joinedMatchIds + match.id
                        Toast.makeText(context, "¡Te uniste a ${match.title}!", Toast.LENGTH_SHORT).show()
                        selectedMatchForJoin = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Unirme Ahora")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMatchForJoin = null }) {
                    Text("Cerrar", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = CardWhite
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    containerBg: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = if (containerBg == Color.White) BorderStroke(1.dp, BorderGray) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

// --- BARRA INFERIOR DE NAVEGACIÓN ESTILO CHOCOFULBO (4 TABS) ---
@Composable
fun ChocoFulboBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: PARTIDOS (Activo en Home)
            BottomBarItem(
                title = "PARTIDOS",
                icon = Icons.Outlined.SportsSoccer,
                isSelected = currentRoute == Screen.Home.route || currentRoute == null,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )

            // Tab 2: TORNEO
            BottomBarItem(
                title = "TORNEO",
                icon = Icons.Outlined.EmojiEvents,
                isSelected = currentRoute == Screen.Rankings.route,
                onClick = { navController.navigate(Screen.Rankings.route) }
            )

            // Tab 3: EQUIPOS
            BottomBarItem(
                title = "EQUIPOS",
                icon = Icons.Outlined.Groups,
                isSelected = currentRoute == Screen.MyTeam.route,
                onClick = { navController.navigate(Screen.MyTeam.route) }
            )

            // Tab 4: PERFIL
            BottomBarItem(
                title = "PERFIL",
                icon = Icons.Outlined.Person,
                isSelected = currentRoute == Screen.Profile.route,
                onClick = { navController.navigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        // Píldora verde neón activa como en el mockup
        Surface(
            onClick = onClick,
            color = LimeNeon,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.height(44.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = DarkForestGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
            }
        }
    } else {
        // Tab inactivo
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B7280)
            )
        }
    }
}

// Mantener compatibilidad con pantallas existentes que importan FloatingPremiumBottomNav y ModernDrawerContent
@Composable
fun FloatingPremiumBottomNav(navController: NavHostController) {
    ChocoFulboBottomBar(navController = navController)
}

@Composable
fun ModernDrawerContent(navController: NavHostController, onClose: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = CardWhite,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier = Modifier.width(300.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(BrandGreen.copy(alpha = 0.1f), Color.Transparent)
                        )
                    )
                    .padding(28.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, null, Modifier.size(32.dp), LimeNeon)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Piero", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("ChocoFulbo Chiclayo", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            val drawerItems = listOf(
                DrawerItemData(Icons.Outlined.Home, "Partidos", Screen.Home.route),
                DrawerItemData(Icons.Outlined.EmojiEvents, "Torneo", Screen.Rankings.route),
                DrawerItemData(Icons.Outlined.Groups, "Mis Equipos", Screen.MyTeam.route),
                DrawerItemData(Icons.Outlined.Person, "Mi Perfil", Screen.Profile.route)
            )

            drawerItems.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        onClose()
                        navController.navigate(item.route)
                    },
                    icon = { Icon(item.icon, null, tint = TextPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Surface(
                onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.Red.copy(alpha = 0.05f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Logout, null, tint = Color.Red.copy(alpha = 0.7f))
                    Spacer(Modifier.width(12.dp))
                    Text("Cerrar Sesión", color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class DrawerItemData(val icon: ImageVector, val label: String, val route: String)
