package com.incanatoapps.retofut.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.RankingPlayer
import com.incanatoapps.retofut.data.network.model.Team
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.components.*
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun DashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    var selectedDate by remember { mutableStateOf("16") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }

    // Configurar el ImageLoader con el token para cargar imágenes privadas
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
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    MainScaffold(
        navController = navController,
        title = "FootBolero"
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text("¡Hola, Pro! 👋", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Text("Encuentra tu próximo reto", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }

            item {
                DateSelector(selectedDate = selectedDate) { selectedDate = it }
            }

            item {
                SoftSearchBar(modifier = Modifier.padding(horizontal = 24.dp))
            }

            // Ranking de Jugadores
            item {
                RankingHeader("Top Jugadores", "Ver todos") {
                    navController.navigate(Screen.Rankings.route)
                }
            }
            item {
                when (val state = viewModel.playersRankingState) {
                    is DashboardState.Loading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                    is DashboardState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.padding(24.dp))
                    is DashboardState.Success -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(state.data) { index, player ->
                                PlayerRankingItem(index + 1, player, imageLoader) {
                                    navController.navigate("player_card/${player.id}")
                                }
                            }
                        }
                    }
                }
            }

            // Ranking de Equipos
            item {
                RankingHeader("Mejores Equipos", "Ver ranking") {
                    navController.navigate(Screen.Rankings.route)
                }
            }
            item {
                when (val state = viewModel.teamsState) {
                    is DashboardState.Loading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                    is DashboardState.Error -> Text(state.message, color = Color.Red, modifier = Modifier.padding(24.dp))
                    is DashboardState.Success -> {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.data.take(5).forEachIndexed { index, team ->
                                TeamRankingRow(index + 1, team, imageLoader) {
                                    navController.navigate("team_detail/${team.id}")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Partidos sugeridos",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(3) { index ->
                SoftMatchCard(
                    teamA = if (index == 0) "Madrid FC" else "Titanes",
                    teamB = if (index == 0) "Barca FC" else "Spartans",
                    time = "20:00",
                    location = "Cancha Central Pro",
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
fun RankingHeader(title: String, action: String, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = action, 
            style = MaterialTheme.typography.labelMedium, 
            color = BrandGreenDeep, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun PlayerRankingItem(position: Int, player: RankingPlayer, imageLoader: ImageLoader, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(24.dp),
        color = CardWhite,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(BrandGreen.copy(alpha = 0.1f))
                        .border(1.dp, BrandGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!player.foto.isNullOrEmpty()) {
                        AsyncImage(
                            model = player.foto,
                            imageLoader = imageLoader,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Outlined.Person, null, Modifier.size(32.dp), TextPrimary)
                    }
                }
                Surface(
                    color = BrandGreen,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(position.toString(), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(player.nombre.split(" ").first(), fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                "${player.playerProfile?.ratingGeneral ?: 50} OVR • ${player.playerProfile?.posicionPrincipal ?: "EXT"}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun TeamRankingRow(position: Int, team: Team, imageLoader: ImageLoader, onClick: () -> Unit) {
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
            Text(
                text = position.toString(),
                modifier = Modifier.width(24.dp),
                fontWeight = FontWeight.Black,
                color = TextSecondary
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppBackground),
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
                    Icon(Icons.Outlined.Groups, null, tint = BrandGreenDeep)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.nombre, fontWeight = FontWeight.Bold)
                Text("${team.partidos} PJ • ${team.victorias} Victorias", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = BorderGray)
        }
    }
}

@Composable
fun ModernDrawerContent(navController: NavHostController, onClose: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = CardWhite,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier = Modifier.width(300.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header con Perfil
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
                            .background(BrandGreen)
                            .border(2.dp, CardWhite, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, null, Modifier.size(32.dp), TextPrimary)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Pro Player", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Nivel Oro • 1,250 pts", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Items del Drawer
            val drawerItems = listOf(
                DrawerItemData(Icons.Outlined.Home, "Inicio", Screen.Home.route),
                DrawerItemData(Icons.Outlined.Person, "Mi Perfil", Screen.Profile.route),
                DrawerItemData(Icons.Outlined.Groups, "Mis Equipos", Screen.MyTeam.route),
                DrawerItemData(Icons.Outlined.EmojiEvents, "Rankings", Screen.Rankings.route),
                DrawerItemData(Icons.Outlined.Settings, "Ajustes", Screen.Home.route)
            )

            drawerItems.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.label, fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { 
                        onClose()
                        navController.navigate(item.route) 
                    },
                    icon = { Icon(item.icon, null, tint = TextPrimary.copy(alpha = 0.6f)) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            // Logout
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

@Composable
fun FloatingPremiumBottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .height(72.dp)
                .shadow(20.dp, shape = RoundedCornerShape(24.dp)),
            color = Color(0xFF1F2937),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab(Icons.Outlined.Home, "Home", currentRoute == Screen.Home.route) { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
                NavTab(Icons.Outlined.Groups, "Equipos", currentRoute == Screen.MyTeam.route) { 
                    navController.navigate(Screen.MyTeam.route) 
                }
                NavTab(Icons.Outlined.EmojiEvents, "Rankings", currentRoute == Screen.Rankings.route) { 
                    navController.navigate(Screen.Rankings.route)
                }
                NavTab(Icons.Outlined.Person, "Perfil", currentRoute == Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route)
                }
            }
        }
    }
}

@Composable
private fun NavTab(
    icon: ImageVector, 
    label: String, 
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val size by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 40.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val containerColor = if (isSelected) BrandGreen else Color.Transparent
    val contentColor = if (isSelected) TextPrimary else Color.White.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, 
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
    }
}

data class DrawerItemData(val icon: ImageVector, val label: String, val route: String)
