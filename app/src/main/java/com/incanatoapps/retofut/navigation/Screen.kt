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

    // Módulos: Gestión de Canchas y Sistema de Torneos
    object CourtManagement : Screen("court_management", "Gestión de Canchas", Icons.Outlined.SportsSoccer)
    object Tournaments : Screen("tournaments", "Torneos y Copas", Icons.Outlined.EmojiEvents)
    object PlayerCard : Screen("player_card/{playerId}", "Ficha FIFA", Icons.Outlined.Badge) {
        fun createRoute(playerId: Int) = "player_card/$playerId"
    }
    object Notifications : Screen("notifications", "Notificaciones", Icons.Outlined.Notifications)
    object Payment : Screen("payment/{matchId}", "Pago de Cancha y Cuotas", Icons.Outlined.AccountBalanceWallet) {
        fun createRoute(matchId: Int = 1) = "payment/$matchId"
    }
    object CourtReservation : Screen("court_reservation/{courtId}", "Reservar Canchas", Icons.Outlined.SportsSoccer) {
        fun createRoute(courtId: Int = 1) = "court_reservation/$courtId"
    }
    object TeamChat : Screen("team_chat/{teamId}", "Chat del Plantel", Icons.Outlined.Chat) {
        fun createRoute(teamId: Int = 1) = "team_chat/$teamId"
    }
    object PostMatchReview : Screen("post_match_review/{matchId}", "Acta y Calificación", Icons.Outlined.StarRate) {
        fun createRoute(matchId: Int = 1) = "post_match_review/$matchId"
    }
    object RefereeDirectory : Screen("referee_directory", "Árbitros y Vocales", Icons.Outlined.Sports)
    object BetsHub : Screen("bets_hub", "Quinielas y Apuestas", Icons.Outlined.MonetizationOn)
}

val bottomBarScreens = listOf(
    Screen.Home,
    Screen.Tournaments,
    Screen.Rankings,
    Screen.Shop,
    Screen.Profile
)

val drawerScreens = listOf(
    Screen.Home,
    Screen.Tournaments,
    Screen.CourtManagement,
    Screen.MyTeam,
    Screen.Challenges,
    Screen.Rankings,
    Screen.Profile
)
