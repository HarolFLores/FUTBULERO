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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

data class DetailPlayer(
    val id: Int,
    val name: String,
    val number: Int,
    val position: String,
    val rating: Int,
    val isCaptain: Boolean,
    val goals: Int,
    val isAnonymous: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    navController: NavHostController,
    teamId: Int,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }
    val currentUserId = remember { sessionManager.fetchUserId() }

    BackHandler { navController.popBackStack() }

    // Carga bajo demanda solo para el equipo actual
    LaunchedEffect(teamId) {
        if (token != null && token != "expo_guest_token") {
            viewModel.loadTeamDetails(token, teamId)
        }
    }

    val userRole = remember { sessionManager.fetchUserRole() }
    val isCaptain = remember {
        sessionManager.isCaptain() || currentUserId == 1 || userRole == UserRole.CAPITAN
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Plantilla, 1: Partidos, 2: Estadísticas
    var showInviteDialog by remember { mutableStateOf(false) }
    var invitePlayerEmail by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Estados para Jugadores Anónimos / Invitados convocados por el Capitán
    var showAddAnonymousDialog by remember { mutableStateOf(false) }
    var anonName by remember { mutableStateOf("") }
    var anonPosition by remember { mutableStateOf("DEL (DC)") }
    var anonNumber by remember { mutableStateOf("") }
    var anonRating by remember { mutableIntStateOf(78) }
    var anonValidationError by remember { mutableStateOf<String?>(null) }

    // Estados para Transferencia de Capitanía ("Ceder la cinta de capitán")
    var showTransferCaptaincyDialog by remember { mutableStateOf(false) }
    var candidateForCaptaincy by remember { mutableStateOf<DetailPlayer?>(null) }
    var pendingCaptainTransferToName by remember { mutableStateOf(sessionManager.fetchPendingCaptaincyToName()) }
    var pendingCaptainTransferToId by remember { mutableIntStateOf(sessionManager.fetchPendingCaptaincyToId()) }

    val teamName = if (teamId == 1) "Los Galácticos FC" else "Rayo Chiclayano"
    val teamFormat = if (teamId == 1) "FÚTBOL 7" else "FÚTBOL 6"
    val teamCity = if (teamId == 1) "Pimentel, Chiclayo" else "La Victoria, Chiclayo"

    val roster = remember {
        mutableStateListOf(
            DetailPlayer(1, "Piero Flores", 10, "DEL (DC)", 87, true, 14, false),
            DetailPlayer(2, "Mateo Farfán", 8, "MED (MC)", 84, false, 6, false),
            DetailPlayer(3, "Christian Cueva Jr.", 7, "MED (EXT)", 82, false, 8, false),
            DetailPlayer(4, "Carlos Zambrano R.", 4, "DEF (DFC)", 85, false, 2, false),
            DetailPlayer(5, "Renato Tapia S.", 6, "MED (MCD)", 83, false, 3, false),
            DetailPlayer(6, "Pedro Gallese Q.", 1, "POR (GK)", 86, false, 0, false),
            DetailPlayer(7, "Luis Advíncula B.", 17, "DEF (LD)", 84, false, 4, false),
            DetailPlayer(8, "Miguel Trauco O.", 22, "DEF (LI)", 80, false, 1, false)
        )
    }

    if (showInviteDialog) {
        Dialog(onDismissRequest = { showInviteDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(54.dp).clip(CircleShape).background(BrandGreenSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, null, tint = DarkForestGreen, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("Convocar Jugador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Envía una invitación formal para unirse a $teamName",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = invitePlayerEmail,
                        onValueChange = { invitePlayerEmail = it },
                        placeholder = { Text("Correo o teléfono del jugador") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Atajo para invitar sin cuenta registrada
                    Surface(
                        onClick = {
                            showInviteDialog = false
                            showAddAnonymousDialog = true
                        },
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.PersonAddAlt1, null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "¿No tiene cuenta? Agregar como Invitado / Anónimo",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { showInviteDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                showInviteDialog = false
                                Toast.makeText(context, "¡Convocatoria enviada exitosamente!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Enviar", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal para que el Capitán agregue Jugadores Anónimos / Invitados sin cuenta registrada
    if (showAddAnonymousDialog) {
        Dialog(onDismissRequest = {
            showAddAnonymousDialog = false
            anonValidationError = null
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
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
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.PersonAddAlt1,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Jugador Anónimo / Invitado",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Agrega a un amigo a la plantilla ingresando solo su apodo y posición, sin que necesite una cuenta registrada.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Campo: Apodo o Nombre
                    Text(
                        "APODO O NOMBRE DEL JUGADOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = anonName,
                        onValueChange = {
                            anonName = it
                            if (anonValidationError != null) anonValidationError = null
                        },
                        placeholder = { Text("Ej: Beto (Vecino), Choche, Lucho") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("anon_player_name_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        isError = anonValidationError != null
                    )
                    if (anonValidationError != null) {
                        Text(
                            text = anonValidationError ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, start = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Selector de Posición
                    Text(
                        "POSICIÓN EN EL CAMPO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    val positions = listOf("DEL (DC)", "MED (MC)", "DEF (DFC)", "POR (GK)")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        positions.forEach { pos ->
                            val isSelected = anonPosition == pos
                            Surface(
                                onClick = { anonPosition = pos },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) DarkForestGreen else Color(0xFFF3F4F6),
                                border = BorderStroke(1.dp, if (isSelected) LimeNeon else BorderGray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = pos.split(" ").first(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = if (isSelected) LimeNeon else TextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Fila: Dorsal opcional & Media estimada
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "DORSAL (#)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = anonNumber,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) anonNumber = it
                                },
                                placeholder = { Text("Auto") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                        }

                        Column(modifier = Modifier.weight(1.1f)) {
                            Text(
                                "MEDIA ESTIMADA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, BorderGray),
                                color = Color(0xFFF9FAFB),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("$anonRating OVR", fontWeight = FontWeight.Black, fontSize = 13.5.sp, color = DarkForestGreen)
                                    Row {
                                        IconButton(
                                            onClick = { if (anonRating > 60) anonRating -= 2 },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                        IconButton(
                                            onClick = { if (anonRating < 95) anonRating += 2 },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                showAddAnonymousDialog = false
                                anonValidationError = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                val trimmed = anonName.trim()
                                if (trimmed.isBlank()) {
                                    anonValidationError = "Ingresa un nombre o apodo"
                                } else {
                                    val assignedNumber = anonNumber.toIntOrNull()
                                        ?: ((1..99) - roster.map { it.number }.toSet()).firstOrNull()
                                        ?: 99
                                    val newPlayer = DetailPlayer(
                                        id = (roster.maxOfOrNull { it.id } ?: 0) + 1,
                                        name = trimmed,
                                        number = assignedNumber,
                                        position = anonPosition,
                                        rating = anonRating,
                                        isCaptain = false,
                                        goals = 0,
                                        isAnonymous = true
                                    )
                                    roster.add(newPlayer)
                                    Toast.makeText(
                                        context,
                                        "¡Jugador invitado '$trimmed' (#$assignedNumber) agregado a la plantilla!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    anonName = ""
                                    anonNumber = ""
                                    showAddAnonymousDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_add_anon_player_button")
                        ) {
                            Text("Agregar", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Modal de confirmación para Ceder Cinta de Capitán a un compañero del plantel
    if (showTransferCaptaincyDialog && candidateForCaptaincy != null) {
        val candidate = candidateForCaptaincy!!
        Dialog(onDismissRequest = {
            showTransferCaptaincyDialog = false
            candidateForCaptaincy = null
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
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
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 28.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Ceder la Cinta de Capitán",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "¿Estás seguro de transferir la capitanía de $teamName a ${candidate.name} (#${candidate.number})?",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "⚠️ Al ceder la capitanía:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "• ${candidate.name} recibirá la notificación formal y decidirá si acepta o rechaza.\n• Si acepta, tendrá control de convocatorias, retos y torneos.\n• Tu perfil pasará automáticamente a Jugador de Plantel.",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showTransferCaptaincyDialog = false
                                candidateForCaptaincy = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                sessionManager.savePendingCaptaincyTransfer(
                                    fromName = "Piero Flores",
                                    toName = candidate.name,
                                    toPlayerId = candidate.id
                                )
                                pendingCaptainTransferToName = candidate.name
                                pendingCaptainTransferToId = candidate.id
                                showTransferCaptaincyDialog = false
                                Toast.makeText(
                                    context,
                                    "👑 Solicitud de capitanía enviada a ${candidate.name}. Esperando que acepte...",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkForestGreen,
                                contentColor = LimeNeon
                            ),
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Enviar Cinta 👑", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
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
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // --- TOP APP BAR ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
                    }

                    Text(
                        "DETALLE DE EQUIPO",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )

                    IconButton(
                        onClick = { navController.navigate("edit_team/$teamId") },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite)
                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = TextPrimary)
                    }
                }
            }

            // --- HERO BANNER EQUIPO ---
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
                            // Escudo
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(CardWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    teamName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = DarkForestGreen
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    teamName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    "$teamFormat • $teamCity",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA7F3D0)
                                )
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    color = LimeNeon,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "CAPITÁN: PIERO FLORES",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkForestGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // Métricas del equipo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(vertical = 10.dp, horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("JUGADORES", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("${roster.size}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("VICTORIAS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("12V", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TORNEOS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("3 🏆", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MEDIA OVR", fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("84", fontSize = 16.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Botones de acción rápida para el Capitán
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showAddAnonymousDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.1f).height(42.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Outlined.PersonAddAlt1, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("+ Anónimo", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                            }

                            Button(
                                onClick = { showInviteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(0.9f).height(42.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Convocar", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }

                            Button(
                                onClick = { navController.navigate(Screen.CreateMatch.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(0.9f).height(42.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Outlined.SportsSoccer, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Retar", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            }

            // --- TABS (PLANTILLA, PARTIDOS, ESTADÍSTICAS) ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Plantilla (${roster.size})", "Partidos Recientes", "Tácticas").forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Surface(
                            onClick = { selectedTab = index },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DarkForestGreen else Color(0xFFF3F4F6),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isSelected) LimeNeon else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // --- CONTENIDO DE PLANTILLA ---
            if (selectedTab == 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "JUGADORES INSCRITOS (${roster.size})",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )

                        if (isCaptain) {
                            Text(
                                text = "+ Agregar Anónimo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier
                                    .clickable { showAddAnonymousDialog = true }
                                    .padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                // Banner interactivo si hay una transferencia de capitanía en curso
                if (pendingCaptainTransferToName != null) {
                    item {
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFF59E0B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👑", fontSize = 18.sp)
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Transferencia de Capitanía",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.5.sp,
                                            color = Color(0xFF92400E)
                                        )
                                        Text(
                                            text = "Propuesta enviada a $pendingCaptainTransferToName. Esperando que acepte la cinta.",
                                            fontSize = 11.sp,
                                            color = Color(0xFFB45309),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            sessionManager.clearPendingCaptaincyTransfer()
                                            pendingCaptainTransferToName = null
                                            Toast.makeText(context, "Transferencia cancelada por el capitán actual", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFD97706)),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                    ) {
                                        Text("Cancelar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                    }

                                    Button(
                                        onClick = {
                                            val recipient = pendingCaptainTransferToName ?: ""
                                            val targetId = pendingCaptainTransferToId

                                            // Reasignar capitanía en el roster
                                            val updated = roster.map { p ->
                                                if (p.id == targetId || (targetId == -1 && p.name == recipient)) {
                                                    p.copy(isCaptain = true)
                                                } else {
                                                    p.copy(isCaptain = false)
                                                }
                                            }
                                            roster.clear()
                                            roster.addAll(updated)

                                            sessionManager.clearPendingCaptaincyTransfer()
                                            pendingCaptainTransferToName = null

                                            // El usuario creador pasa a jugador con equipo regular
                                            sessionManager.saveUserRole(UserRole.JUGADOR_CON_EQUIPO)

                                            Toast.makeText(
                                                context,
                                                "¡$recipient aceptó la capitanía! Ahora es el Capitán oficial de $teamName 👑",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkForestGreen,
                                            contentColor = LimeNeon
                                        ),
                                        modifier = Modifier.weight(1.3f).height(36.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                    ) {
                                        Text("Simular Aceptar Cinta ✓", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                items(roster, key = { it.id }) { player ->
                    Surface(
                        onClick = {
                            if (player.isAnonymous) {
                                Toast.makeText(context, "Jugador Invitado: ${player.name} (${player.position})", Toast.LENGTH_SHORT).show()
                            } else {
                                navController.navigate("player_card/${player.id}")
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = CardWhite,
                        border = BorderStroke(
                            1.dp,
                            when {
                                player.isCaptain -> LimeNeon
                                player.isAnonymous -> Color(0xFFF59E0B)
                                else -> BorderGray
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .testTag("player_roster_row_${player.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Dorsal
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (player.isAnonymous) Color(0xFF374151) else DarkForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "#${player.number}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (player.isAnonymous) Color(0xFFFDE68A) else LimeNeon
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        player.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    if (player.isCaptain) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(color = DarkForestGreen, shape = RoundedCornerShape(4.dp)) {
                                            Text(
                                                "C",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = LimeNeon,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    if (player.isAnonymous) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                        ) {
                                            Text(
                                                "INVITADO",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFFB45309),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    if (player.isAnonymous) "${player.position} • Jugador invitado sin cuenta"
                                    else "${player.position} • ${player.goals} Goles esta temporada",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }

                            // Rating FIFA Badge
                            Surface(
                                color = if (player.rating >= 85) Color(0xFFFEF08A) else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (player.rating >= 85) Color(0xFFEAB308) else BorderGray)
                            ) {
                                Text(
                                    "${player.rating} OVR",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (player.rating >= 85) Color(0xFF854D0E) else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Si es anónimo y el usuario es Capitán, permitir retirarlo de la plantilla
                            if (isCaptain && player.isAnonymous) {
                                Spacer(Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        roster.remove(player)
                                        Toast.makeText(context, "Jugador invitado '${player.name}' retirado", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.DeleteOutline,
                                        contentDescription = "Remover invitado",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else if (isCaptain && !player.isCaptain) {
                                // Opción para ceder la cinta de capitán a este compañero
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    onClick = {
                                        candidateForCaptaincy = player
                                        showTransferCaptaincyDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("👑", fontSize = 11.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Ceder Cinta",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                }
                            } else {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            } else if (selectedTab == 1) {
                // PARTIDOS
                items(listOf("Victoria 5 - 2 vs Dep. Pimentel", "Victoria 4 - 3 vs Rayo Chiclayano", "Empate 2 - 2 vs Sporting La Victoria")) { res ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF22C55E))
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(res, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            }
                            Text("Oficial F7", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            } else {
                // TÁCTICAS
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CardWhite,
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("FORMACIÓN PREDILECTA: 1 - 2 - 3 - 1 (F7)", fontWeight = FontWeight.Black, fontSize = 14.sp, color = DarkForestGreen)
                            Spacer(Modifier.height(8.dp))
                            Text("Portero: Pedro Gallese", fontSize = 12.sp, color = TextPrimary)
                            Text("Defensas: Carlos Zambrano, Miguel Trauco", fontSize = 12.sp, color = TextPrimary)
                            Text("Medios: Renato Tapia, Mateo Farfán, Christian Cueva Jr.", fontSize = 12.sp, color = TextPrimary)
                            Text("Delantero: Piero Flores (#10)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                        }
                    }
                }
            }
        }
    }
}
