package com.incanatoapps.retofut.ui.screens.teams

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.Team
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun MyTeamsScreen(navController: NavHostController, viewModel: TeamViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()
    val state = viewModel.teamsListState

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.loadMyTeams(token)
        }
    }

    MainScaffold(
        navController = navController,
        title = "Mis Equipos"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is TeamsListState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
                is TeamsListState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { 
                        if (token != null) viewModel.loadMyTeams(token)
                    }, colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                        Text("Reintentar", color = TextPrimary)
                    }
                }
                is TeamsListState.Success -> {
                    if (state.teams.isEmpty()) {
                        EmptyTeamsPlaceholder()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(state.teams) { team ->
                                val userStatus = team.teamMembers?.find { it.userId == sessionManager.fetchUserId() }?.estado ?: "activo"

                                MyTeamItem(
                                    team = team,
                                    status = userStatus,
                                    onAccept = {
                                        if (token != null) {
                                            viewModel.joinTeam(token, team.id) {}
                                        }
                                    },
                                    onClick = {
                                        if (userStatus == "activo") {
                                            navController.navigate("team_detail/${team.id}")
                                        }
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateTeam.route) },
                containerColor = BrandGreen,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Equipo", modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun MyTeamItem(
    team: Team,
    status: String,
    onAccept: () -> Unit,
    onClick: () -> Unit
) {
    val isInvited = status == "invitado"

    Surface(
        onClick = if (!isInvited) onClick else ({}),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = if (isInvited) BrandGreenSoft.copy(alpha = 0.3f) else CardWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, if (isInvited) BrandGreen else BorderGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AppBackground)
                        .border(1.dp, BrandGreenSoft, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!team.logo.isNullOrEmpty()) {
                        AsyncImage(model = team.logo, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    } else {
                        Icon(Icons.Outlined.Groups, null, modifier = Modifier.size(36.dp), tint = BrandGreenDeep)
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = team.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextPrimary)
                    Text(text = team.ciudad ?: "Sin ciudad", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                if (isInvited) {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text("ACEPTAR", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }
                } else {
                    Surface(color = BrandGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(14.dp)) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${team.ratingGeneral}", fontWeight = FontWeight.Black, color = BrandGreenDeep, fontSize = 18.sp)
                            Text("OVR", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = BrandGreenDeep)
                        }
                    }
                }
            }

            if (isInvited) {
                Text(
                    "¡Has sido invitado a unirte a este equipo!",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGreenDeep,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = BorderGray.copy(alpha = 0.4f))
                Spacer(Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        StatMiniItem("PJ", "${team.teamStats?.partidosJugados ?: 0}")
                        StatMiniItem("VIC", "${team.teamStats?.victorias ?: 0}")
                        StatMiniItem("DER", "${team.teamStats?.derrotas ?: 0}")
                    }

                    val isCaptain = team.teamMembers?.find { it.userId == team.capitan?.id }?.userId == team.capitan?.id // Simplificado
                    if (team.capitan != null) {
                        Surface(color = Color(0xFF1F2937), shape = RoundedCornerShape(12.dp)) {
                            Text("EQUIPO ACTIVO", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMiniItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextPrimary)
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
fun EmptyTeamsPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(40.dp)).background(CardWhite), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Groups, null, modifier = Modifier.size(64.dp), tint = BorderGray)
        }
        Spacer(Modifier.height(24.dp))
        Text("Sin equipos activos", fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
        Text("Parece que aún no perteneces a ningún equipo. ¡Crea el tuyo y empieza el juego!", color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
    }
}
