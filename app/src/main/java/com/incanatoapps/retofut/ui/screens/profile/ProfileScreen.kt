package com.incanatoapps.retofut.ui.screens.profile

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.UserRole
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.components.FifaCard
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest = remember { sessionManager.isGuest() }
    var userRole by remember { mutableStateOf(sessionManager.fetchUserRole()) }
    val token = remember { sessionManager.fetchAuthToken() }
    val userName = remember { if (isGuest) "Invitado" else (sessionManager.fetchUserName() ?: "Piero Flores") }
    var userPosition by remember { mutableStateOf(sessionManager.fetchPreferredPosition()) }
    var showPositionSelectionDialog by remember { mutableStateOf(false) }
    var pendingPositionForRoleSwitch by remember { mutableStateOf(userPosition) }

    BackHandler {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }

    // Carga bajo demanda al ingresar a la pantalla de perfil
    LaunchedEffect(Unit) {
        if (token != null && token != "expo_guest_token" && !isGuest) {
            viewModel.loadProfile(token)
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Dialog para que el Árbitro configure su Posición de Juego al pasar a Modo Jugador
    if (showPositionSelectionDialog) {
        Dialog(onDismissRequest = { showPositionSelectionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚽", fontSize = 26.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "¡Modo Jugador para Árbitros!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Como árbitro colegiado, también puedes vestirte de corto y jugar pichangas. Elige tu posición en el campo:",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    val positions = listOf("Delantero", "Mediocampista", "Defensa", "Arquero")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        positions.chunked(2).forEach { rowPositions ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPositions.forEach { pos ->
                                    val isSelected = pendingPositionForRoleSwitch == pos
                                    Surface(
                                        onClick = { pendingPositionForRoleSwitch = pos },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) DarkForestGreen else Color(0xFFF3F4F6),
                                        border = BorderStroke(1.dp, if (isSelected) LimeNeon else BorderGray),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = pos,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                            color = if (isSelected) LimeNeon else TextPrimary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPositionSelectionDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                userPosition = pendingPositionForRoleSwitch
                                sessionManager.savePreferredPosition(pendingPositionForRoleSwitch)
                                userRole = UserRole.JUGADOR_LIBRE
                                sessionManager.saveUserRole(UserRole.JUGADOR_LIBRE)
                                showPositionSelectionDialog = false
                                Toast.makeText(
                                    context,
                                    "¡Modo Jugador activado! Tu posición es: $pendingPositionForRoleSwitch ⚽",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("A la Cancha ⚽", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.Black) },
            text = { Text("¿Estás seguro de que deseas salir de tu cuenta?") },
            confirmButton = {
                Button(
                    onClick = {
                        sessionManager.clearAuthToken()
                        showLogoutDialog = false
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // --- TOP BAR ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "MI PERFIL",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFF111827),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Estadísticas y ficha de jugador",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Botón Editar
                    IconButton(
                        onClick = { navController.navigate(Screen.EditProfile.route) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                            .testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar Perfil", tint = TextPrimary)
                    }
                }
            }

            // Banner Modo Invitado en Perfil
            if (isGuest) {
                item {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF93C5FD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDBEAFE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Estás en Modo Invitado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                Text(
                                    text = "Inicia sesión para tener tu ficha oficial, estadísticas guardadas y crear equipos.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF3B82F6),
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Button(
                                onClick = { navController.navigate(Screen.Login.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Acceder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // --- HERO CARD DE JUGADOR ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(DarkForestGreen, Color(0xFF144728))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar con insignia de capitán
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(CardWhite)
                                    .border(2.5.dp, LimeNeon, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    userName.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = DarkForestGreen
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        userName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Icon(Icons.Default.Verified, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                                }
                                Surface(
                                    color = when (userRole) {
                                        UserRole.CAPITAN -> Color(0xFFFEF3C7)
                                        UserRole.ARBITRO -> Color(0xFFF3E8FF)
                                        UserRole.ADMIN_CANCHA -> Color(0xFFFEE2E2)
                                        UserRole.JUGADOR_CON_EQUIPO -> Color(0xFFDBEAFE)
                                        UserRole.JUGADOR_LIBRE -> Color(0xFFDCFCE7)
                                        UserRole.INVITADO -> Color(0xFFF3F4F6)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = userRole.displayName.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when (userRole) {
                                            UserRole.CAPITAN -> Color(0xFF92400E)
                                            UserRole.ARBITRO -> Color(0xFF6B21A8)
                                            UserRole.ADMIN_CANCHA -> Color(0xFF991B1B)
                                            UserRole.JUGADOR_CON_EQUIPO -> Color(0xFF1E40AF)
                                            UserRole.JUGADOR_LIBRE -> Color(0xFF15803D)
                                            UserRole.INVITADO -> Color(0xFF374151)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    "${userPosition.uppercase()} • DORSAL #10",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0)
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Place, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Pimentel, Chiclayo", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            // OVR Badge
                            Surface(
                                color = ForestGreen,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("87", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("OVR", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // Quick buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("player_card/1") },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.3f).height(42.dp).testTag("view_fifa_card_button")
                            ) {
                                Icon(Icons.Default.ElectricBolt, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ficha FIFA 87", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "¡Mira mi ficha técnica oficial de jugador en ChocoFulbo! Rating 87 OVR • Delantero #10 • 29 Goles anotados."
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Compartir Ficha Técnica"))
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Compartir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- ESTADÍSTICAS DE TEMPORADA 2026 ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(
                        "ESTADÍSTICAS DE TEMPORADA 2026",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileStatCard("Partidos", "24", "18 Victorias (75%)", Icons.Outlined.SportsSoccer, Modifier.weight(1f))
                        ProfileStatCard("Goles", "29", "1.2 por partido", Icons.Default.SportsSoccer, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileStatCard("Asistencias", "14", "Pases de gol", Icons.Outlined.Share, Modifier.weight(1f))
                        ProfileStatCard("MVP", "8x 🌟", "Mejor del partido", Icons.Default.EmojiEvents, Modifier.weight(1f))
                    }
                }
            }

            // --- CARTA FIFA PREVIEW INTERACTIVA ---
            item {
                Surface(
                    onClick = { navController.navigate("player_card/1") },
                    shape = RoundedCornerShape(22.dp),
                    color = CardWhite,
                    border = BorderStroke(1.5.dp, LimeNeon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("fifa_card_preview_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = DarkForestGreen, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    "CARTA FIFA ULTIMATE OFICIAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LimeNeon,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text("87 OVR ORO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                        }

                        Spacer(Modifier.height(14.dp))

                        // Stats hexagonales FIFA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            FifaMiniStat("PAC", "89", "Ritmo")
                            FifaMiniStat("SHO", "91", "Tiro")
                            FifaMiniStat("PAS", "84", "Pase")
                            FifaMiniStat("DRI", "88", "Regate")
                            FifaMiniStat("DEF", "45", "Defensa")
                            FifaMiniStat("PHY", "78", "Físico")
                        }

                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Toca para ver tu carta en pantalla completa 3D",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = DarkForestGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // --- MIS CLUBES ACTIVOS ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "MIS CLUBES Y EQUIPOS",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        TextButton(onClick = { navController.navigate(Screen.MyTeam.route) }) {
                            Text("Ver todos", color = DarkForestGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Surface(
                        onClick = { navController.navigate("team_detail/1") },
                        shape = RoundedCornerShape(16.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LG", color = LimeNeon, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Los Galácticos FC", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Text("Capitán • 12 Jugadores • F7", fontSize = 11.sp, color = TextSecondary)
                            }
                            Surface(color = Color(0xFFF0FDF4), shape = RoundedCornerShape(8.dp)) {
                                Text("Activo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }

            // --- HISTORIAL DE PICHANGAS RECIENTES ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        "PICHANGAS RECIENTES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    listOf(
                        Triple("Complejo San Juan - Cancha 2", "Victoria 6 - 4 • Anotaste 2 Goles", "Ayer 19:30"),
                        Triple("Cancha Sintética El Faro", "Victoria 5 - 3 • Anotaste 1 Gol (MVP 🌟)", "Hace 3 días"),
                        Triple("El Oasis Pimentel", "Derrota 2 - 3 • Anotaste 1 Gol", "Hace 5 días")
                    ).forEach { (place, result, time) ->
                        Surface(
                            onClick = { navController.navigate("match_detail/1") },
                            shape = RoundedCornerShape(14.dp),
                            color = CardWhite,
                            border = BorderStroke(1.dp, BorderGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.SportsSoccer, null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(place, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text(result, fontSize = 11.sp, color = if (result.contains("Victoria")) Color(0xFF16A34A) else Color(0xFFDC2626))
                                }
                                Text(time, fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // --- GESTIÓN DE ROLES Y PORTALES ESPECIALES ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Text(
                        "GESTIÓN DE ROL Y PORTALES",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = BorderStroke(1.2.dp, BorderGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "MODO DE OPERACIÓN ACTIVO",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        "Alterna entre roles para probar cada portal",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                }

                                Surface(
                                    color = when (userRole) {
                                        UserRole.CAPITAN -> Color(0xFFFEF3C7)
                                        UserRole.ARBITRO -> Color(0xFFF3E8FF)
                                        UserRole.ADMIN_CANCHA -> Color(0xFFFEE2E2)
                                        UserRole.JUGADOR_CON_EQUIPO -> Color(0xFFDBEAFE)
                                        UserRole.JUGADOR_LIBRE -> Color(0xFFDCFCE7)
                                        UserRole.INVITADO -> Color(0xFFF3F4F6)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = userRole.displayName.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when (userRole) {
                                            UserRole.CAPITAN -> Color(0xFF92400E)
                                            UserRole.ARBITRO -> Color(0xFF6B21A8)
                                            UserRole.ADMIN_CANCHA -> Color(0xFF991B1B)
                                            UserRole.JUGADOR_CON_EQUIPO -> Color(0xFF1E40AF)
                                            UserRole.JUGADOR_LIBRE -> Color(0xFF15803D)
                                            UserRole.INVITADO -> Color(0xFF374151)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // 4 botones de alternancia de rol
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    Triple(UserRole.CAPITAN, "👑 Capitán", Color(0xFFFEF3C7)),
                                    Triple(UserRole.JUGADOR_LIBRE, "⚽ Jugador", Color(0xFFDCFCE7)),
                                    Triple(UserRole.ADMIN_CANCHA, "🏟️ Canchas", Color(0xFFFEE2E2)),
                                    Triple(UserRole.ARBITRO, "⚖️ Árbitro", Color(0xFFF3E8FF))
                                ).forEach { (role, label, bgTint) ->
                                    val isSelected = userRole == role
                                    Surface(
                                        onClick = {
                                            if (role == UserRole.JUGADOR_LIBRE && userRole == UserRole.ARBITRO) {
                                                // El árbitro quiere jugar: abrir diálogo de selección de posición
                                                pendingPositionForRoleSwitch = userPosition
                                                showPositionSelectionDialog = true
                                            } else {
                                                userRole = role
                                                sessionManager.saveUserRole(role)
                                                Toast.makeText(
                                                    context,
                                                    "Rol cambiado a: ${role.displayName}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) DarkForestGreen else bgTint,
                                        border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                                        modifier = Modifier.weight(1f).height(38.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else TextPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            // Banner especial de Doble Rol para Árbitros
                            if (userRole == UserRole.ARBITRO) {
                                Surface(
                                    color = Color(0xFFFAF5FF),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⚖️", fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Doble Rol Habilitado",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF6B21A8)
                                            )
                                            Text(
                                                "Como árbitro colegiado también puedes jugar pichangas ($userPosition).",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFF7E22CE)
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                pendingPositionForRoleSwitch = userPosition
                                                showPositionSelectionDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DarkForestGreen,
                                                contentColor = LimeNeon
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("Jugar ⚽", fontSize = 10.5.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Acceso directo al portal del rol seleccionado
                            when (userRole) {
                                UserRole.ADMIN_CANCHA -> {
                                    Column {
                                        Text(
                                            "🏟️ PANEL DE ADMINISTRADOR DE CANCHAS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                        Text(
                                            "Controla turnos (08:00 a 23:00 hrs), estado disponible/mantenimiento, tarifas por hora y reservas de clientes.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Button(
                                            onClick = { navController.navigate(Screen.CourtManagement.route) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DarkForestGreen,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                        ) {
                                            Icon(Icons.Outlined.Stadium, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Abrir Panel de Gestión de Canchas", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                        }
                                    }
                                }
                                UserRole.ARBITRO -> {
                                    Column {
                                        Text(
                                            "⚖️ PANEL DE ÁRBITRO FEDERADO CODAR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B21A8)
                                        )
                                        Text(
                                            "Figuras en el catálogo oficial de árbitros de Chiclayo. Puedes revisar partidos asignados, tarifas por hora y actas disciplinarias.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Button(
                                            onClick = { navController.navigate(Screen.RefereeDirectory.route) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DarkForestGreen,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                        ) {
                                            Icon(Icons.Outlined.Gavel, null, tint = LimeNeon, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Ver Directorio de Árbitros y Actas", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                        }
                                    }
                                }
                                UserRole.CAPITAN -> {
                                    Column {
                                        Text(
                                            "👑 ACCIONES DE CAPITÁN DE EQUIPO",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF92400E)
                                        )
                                        Text(
                                            "Crea pichangas en Chiclayo, convoca refuerzos o amigos anónimos, contrata árbitros oficiales y administra planteles.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { navController.navigate(Screen.CreateMatch.route) },
                                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Icon(Icons.Outlined.AddCircleOutline, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text("Crear Partido", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { navController.navigate(Screen.MyTeam.route) },
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.2.dp, DarkForestGreen),
                                                modifier = Modifier.weight(1f).height(44.dp)
                                            ) {
                                                Icon(Icons.Outlined.Groups, null, tint = DarkForestGreen, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text("Mis Equipos", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkForestGreen)
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    Column {
                                        Text(
                                            "⚽ ACCIONES DE JUGADOR LIBRE / PICHANGUERO",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            "Explora partidos que necesitan cupos en Patazca, Pimentel o La Victoria. Tu ficha FIFA 3D y valoraciones se actualizan en cada encuentro.",
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Button(
                                            onClick = { navController.navigate(Screen.Home.route) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                        ) {
                                            Icon(Icons.Outlined.Search, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Explorar Pichangas Cerca de Mí", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- AJUSTES Y CERRAR SESIÓN ---
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Notifications, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text("Notificaciones de partidos", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = LimeNeon, checkedTrackColor = DarkForestGreen)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Botón Cerrar Sesión
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFFEF2F2),
                            contentColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("logout_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Logout, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar Sesión", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, BorderGray),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Icon(icon, null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = DarkForestGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FifaMiniStat(label: String, value: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkForestGreen)
        Text(label, fontWeight = FontWeight.Black, fontSize = 10.sp, color = TextPrimary)
        Text(name, fontSize = 8.sp, color = TextSecondary)
    }
}
