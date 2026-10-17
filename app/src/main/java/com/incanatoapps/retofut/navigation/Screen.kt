package com.incanatoapps.retofut.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    // Auth Screens
    object Login : Screen("login", "Login", Icons.Outlined.Login)
    object Register : Screen("register", "Registro", Icons.Outlined.PersonAdd)
    object ForgotPassword : Screen("forgot_password", "Olvidé Clave", Icons.Outlined.LockReset)
    object ResetPassword : Screen("reset_password", "Nueva Clave", Icons.Outlined.VpnKey)

    // Main App Screens
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Rankings : Screen("rankings", "Rankings", Icons.Outlined.EmojiEvents)
    object Shop : Screen("shop", "Shop", Icons.Outlined.ShoppingBag)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
    object EditProfile : Screen("edit_profile", "Editar Perfil", Icons.Outlined.Edit)
    
    // Internal/Other screens
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Outlined.Dashboard)
    object Challenges : Screen("challenges", "Retos", Icons.Outlined.MilitaryTech)
    object MyTeam : Screen("my_team", "Mis Equipos", Icons.Outlined.Groups)
    object CreateTeam : Screen("create_team", "Crear Equipo", Icons.Outlined.GroupAdd)
    object TeamDetail : Screen("team_detail/{teamId}", "Detalle Equipo", Icons.Outlined.Info)

    // ChocoFulbo Match Screens
    object SearchMatch : Screen("search_match", "Buscar Partido", Icons.Outlined.Search)
    object CreateMatch : Screen("create_match", "Crear Partido", Icons.Outlined.Add)
    object MatchDetail : Screen("match_detail/{matchId}", "Detalles del Partido", Icons.Outlined.SportsSoccer)
    object LiveMatch : Screen("live_match/{matchId}", "Marcador en Vivo", Icons.Outlined.SportsSoccer)
}

val bottomBarScreens = listOf(
    Screen.Home,
    Screen.Rankings,
    Screen.Shop,
    Screen.Profile
)

val drawerScreens = listOf(
    Screen.Home,
    Screen.MyTeam,
    Screen.Challenges,
    Screen.Rankings,
    Screen.Profile
)
