package com.incanatoapps.retofut.ui.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.TeamMember
import com.incanatoapps.retofut.data.network.model.UserResponse
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

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
    val state = viewModel.teamDetailState

    var showDeleteTeamDialog by remember { mutableStateOf(false) }
    var memberToRemove by remember { mutableStateOf<TeamMember?>(null) }
    var memberToPromote by remember { mutableStateOf<TeamMember?>(null) }
    var showInviteDialog by remember { mutableStateOf(false) }

    val imageLoader = remember(token) {
        ImageLoader.Builder(context)
            .okHttpClient {
                RetrofitClient.getOkHttpClient(token)
            }
            .build()
    }

    LaunchedEffect(teamId) {
        if (token != null) {
            viewModel.loadTeamDetails(token, teamId)
        }
    }

    if (showDeleteTeamDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTeamDialog = false },
            title = { Text("Eliminar Equipo") },
            text = { Text("¿Estás seguro de que deseas eliminar este equipo? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    if (token != null) {
                        viewModel.deleteTeam(token, teamId) {
                            navController.popBackStack()
                        }
                    }
                    showDeleteTeamDialog = false
                }) { Text("ELIMINAR", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTeamDialog = false }) { Text("CANCELAR") }
            }
        )
    }

    memberToRemove?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text("Expulsar Jugador") },
            text = { Text("¿Estás seguro de que deseas expulsar a ${member.user?.nombre} del equipo?") },
            confirmButton = {
                TextButton(onClick = {
                    if (token != null) {
                        viewModel.removeMember(token, teamId, member.userId) {
                            memberToRemove = null
                        }
                    }
                }) { Text("EXPULSAR", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) { Text("CANCELAR") }
            }
        )
    }

    memberToPromote?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToPromote = null },
            title = { Text("Transferir Capitanía") },
            text = { Text("¿Estás seguro de que deseas nombrar a ${member.user?.nombre} como el nuevo capitán? Perderás tus privilegios de administración.") },
            confirmButton = {
                TextButton(onClick = {
                    if (token != null) {
                        viewModel.transferCaptaincy(token, teamId, member.userId) {
                            memberToPromote = null
                        }
                    }
                }) { Text("TRANSFERIR", color = BrandGreenDeep) }
            },
            dismissButton = {
                TextButton(onClick = { memberToPromote = null }) { Text("CANCELAR") }
            }
        )
    }

    if (showInviteDialog) {
        InvitePlayerDialog(
            onDismiss = { showInviteDialog = false },
            onInvite = { userId ->
                if (token != null) {
                    viewModel.invitePlayer(token, teamId, userId) {
                        showInviteDialog = false
                    }
                }
            },
            viewModel = viewModel,
            imageLoader = imageLoader
        )
    }

    MainScaffold(
        navController = navController,
        title = "Detalle del Equipo"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is TeamState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
                is TeamState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red)
                }
                is TeamState.Success -> {
                    val team = state.team
                    val isCaptain = team.capitan?.id == currentUserId || 
                                   team.teamMembers?.any { it.userId == currentUserId && it.rol.lowercase() == "capitan" } == true

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isCaptain) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { showInviteDialog = true }) {
                                            Icon(Icons.Outlined.PersonAdd, "Invitar Jugador", tint = BrandGreen)
                                        }
                                        IconButton(onClick = { navController.navigate("edit_team/${team.id}") }) {
                                            Icon(Icons.Outlined.Edit, "Editar", tint = BrandGreen)
                                        }
                                        IconButton(onClick = { showDeleteTeamDialog = true }) {
                                            Icon(Icons.Outlined.Delete, "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(CardWhite)
                                        .border(2.dp, BrandGreen, RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!team.logo.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = team.logo,
                                            imageLoader = imageLoader,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Outlined.Groups, null, modifier = Modifier.size(50.dp), tint = BrandGreen)
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(team.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Place, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(Modifier.width(4.dp))
                                    Text(team.ciudad ?: "Sin ciudad", color = TextSecondary)
                                }
                            }
                        }

                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = CardWhite,
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TeamStatItem("PJ", "${team.teamStats?.partidosJugados ?: 0}")
                                    TeamStatItem("VIC", "${team.teamStats?.victorias ?: 0}")
                                    TeamStatItem("EMP", "${team.teamStats?.empates ?: 0}")
                                    TeamStatItem("DER", "${team.teamStats?.derrotas ?: 0}")
                                }
                            }
                        }

                        item {
                            Text("Plantilla del Equipo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        items(team.teamMembers ?: emptyList()) { member ->
                            MemberRow(
                                member = member, 
                                imageLoader = imageLoader,
                                isCaptainViewer = isCaptain,
                                onRemove = { memberToRemove = member },
                                onPromote = { memberToPromote = member },
                                onClick = { navController.navigate("player_card/${member.userId}") }
                            )
                        }
                        
                        item { Spacer(Modifier.height(100.dp)) }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun InvitePlayerDialog(
    onDismiss: () -> Unit,
    onInvite: (Int) -> Unit,
    viewModel: TeamViewModel,
    imageLoader: ImageLoader
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()
    val searchState = viewModel.searchPlayersState

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardWhite,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Buscar Jugador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (token != null && it.length >= 3) {
                            viewModel.searchPlayers(token, it)
                        }
                    },
                    label = { Text("Nombre del jugador...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.Search, null) }
                )
                
                Spacer(Modifier.height(16.dp))
                
                Box(modifier = Modifier.weight(1f)) {
                    when (searchState) {
                        is SearchPlayersState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BrandGreen)
                        is SearchPlayersState.Error -> Text(searchState.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                        is SearchPlayersState.Success -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(searchState.players) { player ->
                                    PlayerSearchRow(player, imageLoader) { onInvite(player.id) }
                                }
                            }
                        }
                        else -> Text("Escribe al menos 3 letras para buscar", modifier = Modifier.align(Alignment.Center), color = TextSecondary)
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("CERRAR", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun PlayerSearchRow(player: UserResponse, imageLoader: ImageLoader, onInvite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            if (!player.foto.isNullOrEmpty()) {
                AsyncImage(model = player.foto, imageLoader = imageLoader, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Outlined.Person, null, tint = BrandGreen)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(player.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(player.playerProfile?.posicionPrincipal ?: "Sin posición", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Button(
            onClick = onInvite,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandGreenSoft)
        ) {
            Text("Invitar", color = BrandGreenDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun TeamStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = BrandGreenDeep)
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
fun MemberRow(
    member: TeamMember, 
    imageLoader: ImageLoader, 
    isCaptainViewer: Boolean,
    onRemove: () -> Unit,
    onPromote: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp).clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                if (!member.user?.foto.isNullOrEmpty()) {
                    AsyncImage(
                        model = member.user?.foto,
                        imageLoader = imageLoader,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Person, null, tint = BrandGreen)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.user?.nombre ?: "Usuario", fontWeight = FontWeight.Bold)
                Text(
                    text = member.rol.replaceFirstChar { it.uppercase() } + " - " + member.estado.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (member.estado == "invitado") Color.Gray else if (member.rol.lowercase() == "capitan") BrandGreenDeep else TextSecondary
                )
            }
            
            if (member.rol.lowercase() == "capitan") {
                Surface(color = BrandGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("C", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Black, color = BrandGreenDeep, fontSize = 12.sp)
                }
            } else if (isCaptainViewer && member.estado == "activo") {
                // Opciones de gestión para el capitán sobre miembros activos
                Row {
                    IconButton(onClick = onPromote) {
                        Icon(Icons.Outlined.EmojiEvents, "Hacer Capitán", tint = BrandGreenDeep.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Outlined.PersonRemove, "Eliminar Jugador", tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            } else if (isCaptainViewer && member.estado == "invitado") {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Outlined.Cancel, "Revocar Invitación", tint = Color.Gray)
                }
            }
        }
    }
}
