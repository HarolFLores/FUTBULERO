package com.incanatoapps.retofut.ui.screens.teams

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTeamsScreen(
    navController: NavHostController,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = remember { sessionManager.fetchUserName() ?: "Piero" }

    BackHandler {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Mis equipos, 1: Explorar
    var hasPendingInvite by remember { mutableStateOf(true) }
    var inviteAccepted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F3822))
                    .statusBarsPadding()
            ) {
                // Barra superior de ChocoFulbo Chiclayo Amateur
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo ChocoFulbo con Badge Chiclayo Amateur
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x334ADE80))
                                .border(1.dp, Color(0x664ADE80), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SportsSoccer,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ChocoFulbo",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Surface(
                                color = Color(0x33000000),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "CHICLAYO AMATEUR",
                                    color = Color(0xFF86EFAC),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Acciones derechas: Notificaciones y Perfil
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x26000000))
                                .clickable { navController.navigate(Screen.Notifications.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(5.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80))
                                    .border(1.dp, Color(0xFF0F3822), CircleShape)
                            )
                        }

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF16A34A))
                                .border(1.5.dp, Color(0xFF4ADE80), CircleShape)
                                .clickable { navController.navigate(Screen.Profile.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            ChocoFulboBottomBar(navController = navController)
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // ENCABEZADO "Mis equipos" + BOTÓN "+ Crear equipo"
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mis equipos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111827)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Gestiona tus planteles, convocatorias y ligas",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Button(
                        onClick = { navController.navigate(Screen.CreateTeam.route) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("create_team_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Crear\nequipo",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // ==========================================
            // PÍLDORAS SELECTORAS: [👥 Mis equipos 2] vs [🧭 Explorar]
            // ==========================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Píldora 1: Mis equipos (2)
                    Surface(
                        onClick = { selectedTab = 0 },
                        shape = RoundedCornerShape(11.dp),
                        color = if (selectedTab == 0) Color(0xFF143826) else Color.Transparent,
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Groups,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color.White else Color(0xFF4B5563),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Mis equipos",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else Color(0xFF4B5563)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = if (selectedTab == 0) LimeNeon else Color(0xFFE5E7EB)
                            ) {
                                Text(
                                    text = if (inviteAccepted) "3" else "2",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedTab == 0) DarkForestGreen else Color(0xFF4B5563),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Píldora 2: Explorar
                    Surface(
                        onClick = { selectedTab = 1 },
                        shape = RoundedCornerShape(11.dp),
                        color = if (selectedTab == 1) Color(0xFF143826) else Color.Transparent,
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Explore,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.White else Color(0xFF4B5563),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Explorar",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // HERO BANNER: Torneo Apertura 2026
            // ==========================================
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Tournaments.route) },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0F3822)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF0F3822),
                                        Color(0xFF14532D)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(LimeNeon)
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(DarkForestGreen)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "INSCRIPCIONES ABIERTAS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "Edición Nocturna",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = "Torneo Apertura 2026",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "La pasión del barrio en cada jugada. 16 equipos por la copa distrital.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )

                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Inicia 15 de Marzo",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LimeNeon
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Bases del torneo",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textDecoration = TextDecoration.Underline
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN: Invitaciones pendientes (1) [POR RESPONDER]
            // ==========================================
            if (hasPendingInvite && !inviteAccepted) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Invitaciones pendientes",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = LimeNeon
                            ) {
                                Text(
                                    text = "1",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = "POR RESPONDER",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF6B7280),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Tarjeta Sporting Reque
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "SR",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1F2937)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Sporting Reque",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF111827)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = LimeNeon
                                        ) {
                                            Text(
                                                text = "NUEVA",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = DarkForestGreen,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Sports,
                                            contentDescription = null,
                                            tint = Color(0xFF6B7280),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Convocado por Enrique Merino (DT)",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Caja gris con datos de liga y puesto
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF9FAFB),
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("LIGA OFICIAL", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                        Spacer(Modifier.height(2.dp))
                                        Text("Distrital • Chiclayo Sur", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                    }
                                    Column {
                                        Text("PUESTO ASIGNADO", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                        Spacer(Modifier.height(2.dp))
                                        Text("🚩 Delantero titular", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Botones Rechazar / Aceptar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        hasPendingInvite = false
                                        Toast.makeText(context, "Invitación rechazada", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                                ) {
                                    Text("Rechazar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF374151))
                                }

                                Button(
                                    onClick = {
                                        inviteAccepted = true
                                        Toast.makeText(context, "¡Aceptaste la convocatoria de Sporting Reque!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkForestGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Aceptar", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN: Planteles activos (2) [Ver historial]
            // ==========================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Planteles activos",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = if (inviteAccepted) "3" else "2",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4B5563),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = "Ver historial",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563)
                    )
                }
            }

            // --- EQUIPO 1: Atlético Chiclayo (Destacado con borde verde neón) ---
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("team_card_atletico_chiclayo"),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, Color(0xFF4ADE80))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge AC
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AC",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Atlético Chiclayo",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.5.sp,
                                        color = Color(0xFF111827)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4ADE80))
                                    )
                                }

                                Spacer(Modifier.height(3.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = LimeNeon
                                    ) {
                                        Text(
                                            text = "CAPITÁN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DarkForestGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFF3F4F6)
                                    ) {
                                        Text(
                                            text = "Fútbol 7",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4B5563),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = { /* Menú de equipo */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color(0xFF9CA3AF))
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Caja PRÓXIMO ENCUENTRO
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkForestGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "PRÓXIMO ENCUENTRO",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6B7280),
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "vs. Real Marthoquinque",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "🕒 Sáb 18:00 hrs • Complejo El Faro",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Stats: POSICIÓN | PLANTILLA | EFECTIVIDAD
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("POSICIÓN", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.width(3.dp).height(12.dp).background(Color(0xFF4ADE80)))
                                    Spacer(Modifier.width(4.dp))
                                    Text("4° puesto", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF111827))
                                }
                            }

                            Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFFE5E7EB)))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PLANTILLA", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                Spacer(Modifier.height(2.dp))
                                Text("👥 12 jugadores", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF111827))
                            }

                            Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFFE5E7EB)))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("EFECTIVIDAD", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                                Spacer(Modifier.height(2.dp))
                                Text("+7 DG", fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = Color(0xFF16A34A))
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Botones: Chat Plantel y Ver equipo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = { navController.navigate("team_chat/1") },
                                shape = RoundedCornerShape(10.dp),
                                color = DarkForestGreen,
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Chat,
                                        contentDescription = null,
                                        tint = LimeNeon,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Chat Plantel",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                onClick = { navController.navigate("team_detail/1") },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF3F4F6),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ver equipo",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF1F2937),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- EQUIPO 2: Deportivo Patazca F.C. ---
            item {
                Surface(
                    onClick = { navController.navigate("team_detail/2") },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "DP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1F2937)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Deportivo Patazca F.C.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF111827)
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Rol: Mediocampista • ",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Text(
                                        text = "Fútbol 6",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🕒 Domingo 10:30 hrs • Cancha Patazca",
                                fontSize = 11.5.sp,
                                color = Color(0xFF4B5563)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = "Fecha 8",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B5563),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- BANNER INFERIOR: Copa Relámpago Pimentel ---
            item {
                Surface(
                    onClick = { navController.navigate(Screen.Tournaments.route) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F3822),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = Color(0xFF0F3822),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Copa Relámpago Pimentel",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Inscribe a tu escuadra este fin de semana",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Outlined.NorthEast,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
