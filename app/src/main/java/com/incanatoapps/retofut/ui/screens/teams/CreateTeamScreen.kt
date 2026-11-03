package com.incanatoapps.retofut.ui.screens.teams

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.UserRole
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

private data class PotentialPlayer(
    val id: Int,
    val initials: String,
    val name: String,
    val position: String,
    val info: String,
    var isInvited: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(
    navController: NavController,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()

    BackHandler { navController.popBackStack() }

    var teamName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Libre") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedShieldUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedShieldUri = uri
    }

    val categories = listOf("Libre", "Master (+35)", "Sub-20", "Empresarial")

    var playersList by remember {
        mutableStateOf(
            listOf(
                PotentialPlayer(1, "CM", "Carlos Mendoza", "MED", "Pimentel • 14 partidos", isInvited = false),
                PotentialPlayer(2, "BS", "Roberto 'Beto' Silva", "DEL", "Victoria • 9 goles", isInvited = true),
                PotentialPlayer(3, "MQ", "Manuel Quintana", "POR", "Santa Victoria • 1.84m", isInvited = false),
                PotentialPlayer(4, "DP", "David Paredes", "DEF", "José L. Ortiz • Capataz", isInvited = false)
            )
        )
    }

    val invitedCount = playersList.count { it.isInvited } + 2 // Base capitanes
    val filteredPlayers = if (searchQuery.isBlank()) playersList else playersList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.initials.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F3822))
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Crear Equipo",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "CHICLAYO GRASSROOTS",
                            color = Color(0xFF86EFAC),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Registra tu equipo para torneos oficiales en Chiclayo", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "Ayuda",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Se usa cast o se renderiza la barra estándar
            (navController as? androidx.navigation.NavHostController)?.let {
                ChocoFulboBottomBar(navController = it)
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // CARD 1: IDENTIDAD DEL EQUIPO [PASO 1 DE 2]
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(Color(0xFF4ADE80))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "IDENTIDAD DEL EQUIPO",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF111827),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = "PASO 1 DE 2",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Círculo Subir Escudo
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF9FAFB))
                                .border(1.5.dp, Color(0xFFD1D5DB), CircleShape)
                                .clickable { photoLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedShieldUri != null) {
                                AsyncImage(
                                    model = selectedShieldUri,
                                    contentDescription = "Escudo",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Subir Escudo",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF374151)
                                    )
                                }
                            }

                            // Botón verde +
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80))
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = DarkForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Formatos PNG, JPG (Máx. 5MB)",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // NOMBRE DEL EQUIPO *
                    Text(
                        text = "NOMBRE DEL EQUIPO *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Outlined.SportsSoccer, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                        },
                        placeholder = {
                            Text("Ej. Los Tigres de Patazca", fontSize = 13.5.sp, color = Color(0xFF9CA3AF))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkForestGreen,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    // MODALIDAD & SEDE
                    Text(
                        text = "MODALIDAD & SEDE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE5E7EB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Stadium,
                                        contentDescription = null,
                                        tint = Color(0xFF374151),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Fútbol 7 (Grass Sintético)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                    Text(
                                        text = "Canchas El Golazo Chiclayo",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = LimeNeon
                            ) {
                                Text(
                                    text = "OFICIAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // CATEGORÍA DEL TORNEO *
                    Text(
                        text = "CATEGORÍA DEL TORNEO *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827),
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryPill(categories[0], selectedCategory == categories[0], Modifier.weight(1f)) { selectedCategory = categories[0] }
                        CategoryPill(categories[1], selectedCategory == categories[1], Modifier.weight(1f)) { selectedCategory = categories[1] }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryPill(categories[2], selectedCategory == categories[2], Modifier.weight(1f)) { selectedCategory = categories[2] }
                        CategoryPill(categories[3], selectedCategory == categories[3], Modifier.weight(1f)) { selectedCategory = categories[3] }
                    }
                }
            }

            // ==========================================
            // CARD 2: INVITAR JUGADORES [3 de 10 seleccionados]
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INVITAR JUGADORES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111827),
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "$invitedCount de 10 seleccionados",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Mínimo 7 jugadores requeridos para registrar en lista oficial.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(Modifier.height(10.dp))

                    // Barra de progreso verde
                    LinearProgressIndicator(
                        progress = { (invitedCount / 10f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF4ADE80),
                        trackColor = Color(0xFFF3F4F6)
                    )

                    Spacer(Modifier.height(14.dp))

                    // Buscador de jugadores
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                        },
                        placeholder = {
                            Text("Buscar por nombre o apodo...", fontSize = 12.5.sp, color = Color(0xFF9CA3AF))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedBorderColor = Color(0xFFD1D5DB),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Lista de jugadores sugeridos para convocar
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredPlayers.forEach { player ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (player.isInvited) DarkForestGreen else Color(0xFFF3F4F6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = player.initials,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = if (player.isInvited) Color.White else Color(0xFF1F2937)
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = player.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = Color(0xFF111827)
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(3.dp),
                                                    color = if (player.position == "DEL") Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                                                ) {
                                                    Text(
                                                        text = player.position,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (player.position == "DEL") Color(0xFF15803D) else Color(0xFF4B5563),
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = player.info,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                    }

                                    // Botón Invitar o Invitado
                                    if (player.isInvited) {
                                        Surface(
                                            onClick = {
                                                playersList = playersList.map {
                                                    if (it.id == player.id) it.copy(isInvited = false) else it
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = LimeNeon,
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Check, null, tint = DarkForestGreen, modifier = Modifier.size(14.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Invitado ✓", fontSize = 11.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                            }
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = {
                                                playersList = playersList.map {
                                                    if (it.id == player.id) it.copy(isInvited = true) else it
                                                }
                                                Toast.makeText(context, "Invitación enviada a ${player.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                            border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                                        ) {
                                            Text("+ Invitar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // BOTÓN CREAR EQUIPO
            // ==========================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val trimmed = teamName.trim()
                        if (trimmed.isBlank()) {
                            Toast.makeText(context, "Ingresa un nombre para tu equipo", Toast.LENGTH_SHORT).show()
                        } else {
                            val token = sessionManager.fetchAuthToken() ?: ""
                            if (token.isNotEmpty()) {
                                viewModel.createTeam(
                                    context = context,
                                    token = token,
                                    nombre = trimmed,
                                    logoUri = selectedShieldUri,
                                    ciudad = "Chiclayo",
                                    descripcion = "Registrado para Torneo Apertura 2026",
                                    formato = "Fútbol 7 (Grass Sintético)"
                                )
                            }
                            // El creador del equipo pasa automáticamente a ser Capitán oficial
                            sessionManager.saveUserRole(UserRole.CAPITAN)
                            Toast.makeText(context, "¡Equipo '$trimmed' creado exitosamente! Ahora eres Capitán del plantel 👑", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_create_team_button")
                ) {
                    Text("Crear Equipo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }

                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Quedará registrado para el Torneo Apertura 2025",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF143826) else Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF4ADE80) else Color(0xFFE5E7EB)),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF374151)
            )
            if (isSelected) {
                Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, Color(0xFF9CA3AF), CircleShape)
                )
            }
        }
    }
}
