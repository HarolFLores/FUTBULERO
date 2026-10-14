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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.TeamMember
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
    val state = viewModel.teamDetailState
    val currentUser = remember { /* Obtener el usuario actual si es necesario para verificar permisos extra, pero el ID del capitan está en el equipo */ }

    LaunchedEffect(teamId) {
        val token = sessionManager.fetchAuthToken()
        if (token != null) {
            viewModel.loadTeamDetails(token, teamId)
        }
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
                    // Se asume que el backend retorna el ID del capitan o un campo que indique si el usuario actual es capitan
                    // Por ahora, habilitaremos los botones si team.capitan?.id es el del usuario (esto requeriría el ID del usuario actual)
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Header del Equipo con Acciones
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = { navController.navigate("edit_team/${team.id}") }) {
                                        Icon(Icons.Outlined.Edit, "Editar", tint = BrandGreen)
                                    }
                                    IconButton(onClick = { 
                                        val token = sessionManager.fetchAuthToken()
                                        if (token != null) {
                                            viewModel.deleteTeam(token, team.id) {
                                                navController.popBackStack()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Outlined.Delete, "Eliminar", tint = Color.Red)
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
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Outlined.Groups, null, modifier = Modifier.size(50.dp), tint = BrandGreen)
                                    }
                                }
                                
                                Spacer(Modifier.height(16.dp))
                                
                                Text(
                                    text = team.nombre,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Place, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(Modifier.width(4.dp))
                                    Text(team.ciudad ?: "Sin ciudad", color = TextSecondary)
                                }
                            }
                        }

                        // Estadísticas del Equipo
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

                        // Lista de Miembros
                        item {
                            Text(
                                "Plantilla del Equipo",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        items(team.teamMembers ?: emptyList()) { member ->
                            MemberRow(member) {
                                navController.navigate("player_card/${member.userId}")
                            }
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
fun TeamStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = BrandGreenDeep)
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
fun MemberRow(member: TeamMember, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                    text = member.rol.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (member.rol == "capitan") BrandGreenDeep else TextSecondary
                )
            }
            
            if (member.rol == "capitan") {
                Surface(
                    color = BrandGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "C",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Black,
                        color = BrandGreenDeep,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
