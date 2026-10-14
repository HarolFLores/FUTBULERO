package com.incanatoapps.retofut.ui.screens.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.RankingPlayer
import com.incanatoapps.retofut.data.network.model.Team
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardState
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardViewModel
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun RankingsScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }

    // ImageLoader con token para imágenes privadas
    val imageLoader = remember(token) {
        ImageLoader.Builder(context)
            .okHttpClient {
                RetrofitClient.getOkHttpClient(token)
            }
            .build()
    }

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.loadDashboardData(token)
        }
    }

    MainScaffold(
        navController = navController,
        title = "Rankings"
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppBackground,
                contentColor = BrandGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BrandGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Jugadores", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Equipos", fontWeight = FontWeight.Bold) }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (selectedTab == 0) {
                    PlayerRankingList(viewModel.playersRankingState, imageLoader) { playerId ->
                        navController.navigate("player_card/$playerId")
                    }
                } else {
                    TeamRankingList(viewModel.teamsState, imageLoader) { teamId ->
                        navController.navigate("team_detail/$teamId")
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerRankingList(state: DashboardState<List<RankingPlayer>>, imageLoader: ImageLoader, onPlayerClick: (Int) -> Unit) {
    when (state) {
        is DashboardState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        is DashboardState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.message, color = Color.Red)
        }
        is DashboardState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(state.data) { index, player ->
                    RankingPlayerRow(index + 1, player, imageLoader, onPlayerClick)
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TeamRankingList(state: DashboardState<List<Team>>, imageLoader: ImageLoader, onTeamClick: (Int) -> Unit) {
    when (state) {
        is DashboardState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandGreen)
        }
        is DashboardState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(state.message, color = Color.Red)
        }
        is DashboardState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(state.data) { index, team ->
                    RankingTeamRow(index + 1, team, imageLoader, onTeamClick)
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun RankingPlayerRow(position: Int, player: RankingPlayer, imageLoader: ImageLoader, onClick: (Int) -> Unit) {
    Surface(
        onClick = { onClick(player.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$position",
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Black,
                color = if (position <= 3) BrandGreenDeep else TextSecondary,
                fontSize = 18.sp
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                if (!player.foto.isNullOrEmpty()) {
                    AsyncImage(
                        model = player.foto,
                        imageLoader = imageLoader,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Person, null, tint = BrandGreen)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(player.nombre, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    player.playerProfile?.posicionPrincipal ?: "Sin posición",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${player.playerProfile?.ratingGeneral ?: 50}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = BrandGreenDeep
                )
                Text("OVR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}

@Composable
fun RankingTeamRow(position: Int, team: Team, imageLoader: ImageLoader, onClick: (Int) -> Unit) {
    Surface(
        onClick = { onClick(team.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$position",
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Black,
                color = if (position <= 3) BrandGreenDeep else TextSecondary,
                fontSize = 18.sp
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                if (!team.logo.isNullOrEmpty()) {
                    AsyncImage(
                        model = team.logo,
                        imageLoader = imageLoader,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Outlined.Groups, null, tint = BrandGreen)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.nombre, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "${team.partidos} PJ • ${team.victorias} Victorias",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${team.ratingGeneral}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = BrandGreenDeep
                )
                Text("RATING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }
    }
}
