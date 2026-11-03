package com.incanatoapps.retofut.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.incanatoapps.retofut.ui.screens.auth.ForgotPasswordScreen
import com.incanatoapps.retofut.ui.screens.auth.LoginScreen
import com.incanatoapps.retofut.ui.screens.auth.RegisterScreen
import com.incanatoapps.retofut.ui.screens.auth.ResetPasswordScreen
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardScreen
import com.incanatoapps.retofut.ui.screens.dashboard.DashboardViewModel
import com.incanatoapps.retofut.ui.screens.profile.EditProfileScreen
import com.incanatoapps.retofut.ui.screens.profile.PlayerCardScreen
import com.incanatoapps.retofut.ui.screens.profile.PlayerCardViewModel
import com.incanatoapps.retofut.ui.screens.profile.ProfileScreen
import com.incanatoapps.retofut.ui.screens.profile.ProfileViewModel
import com.incanatoapps.retofut.ui.screens.rankings.RankingsScreen
import com.incanatoapps.retofut.ui.screens.teams.CreateTeamScreen
import com.incanatoapps.retofut.ui.screens.teams.EditTeamScreen
import com.incanatoapps.retofut.ui.screens.teams.MyTeamsScreen
import com.incanatoapps.retofut.ui.screens.teams.TeamDetailScreen
import com.incanatoapps.retofut.ui.screens.teams.TeamViewModel
import com.incanatoapps.retofut.ui.screens.matches.SearchMatchScreen
import com.incanatoapps.retofut.ui.screens.matches.CreateMatchScreen
import com.incanatoapps.retofut.ui.screens.matches.MatchDetailScreen
import com.incanatoapps.retofut.ui.screens.matches.LiveScoreScreen
import com.incanatoapps.retofut.ui.screens.courts.CourtManagementScreen
import com.incanatoapps.retofut.ui.screens.courts.CourtManagementViewModel
import com.incanatoapps.retofut.ui.screens.tournaments.TournamentsScreen
import com.incanatoapps.retofut.ui.screens.tournaments.TournamentsViewModel
import com.incanatoapps.retofut.ui.screens.notifications.NotificationsScreen
import com.incanatoapps.retofut.ui.screens.payments.PaymentScreen
import com.incanatoapps.retofut.ui.screens.teams.TeamChatScreen
import com.incanatoapps.retofut.ui.screens.courts.CourtReservationScreen
import com.incanatoapps.retofut.ui.screens.matches.PostMatchReviewScreen
import com.incanatoapps.retofut.ui.screens.matches.BetsHubScreen
import com.incanatoapps.retofut.ui.screens.referees.RefereeDirectoryScreen
import com.incanatoapps.retofut.ui.screens.referees.RefereePortalScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(300)
            )
        }
    ) {
        // Auth Flow (ViewModels son creados únicamente al entrar a cada composable)
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(navController)
        }

        // Main App con ViewModels estrictamente acotados al ciclo de vida de su propia ruta (backStackEntry)
        composable(Screen.Home.route) { backStackEntry ->
            val viewModel: DashboardViewModel = viewModel(backStackEntry)
            DashboardScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Rankings.route) { backStackEntry ->
            val viewModel: DashboardViewModel = viewModel(backStackEntry)
            RankingsScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Shop.route) {
            PlaceholderScreen(Screen.Shop.title)
        }
        composable(Screen.Profile.route) { backStackEntry ->
            val viewModel: ProfileViewModel = viewModel(backStackEntry)
            ProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.EditProfile.route) { backStackEntry ->
            val viewModel: ProfileViewModel = viewModel(backStackEntry)
            EditProfileScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.MyTeam.route) { backStackEntry ->
            val viewModel: TeamViewModel = viewModel(backStackEntry)
            MyTeamsScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.CreateTeam.route) { backStackEntry ->
            val viewModel: TeamViewModel = viewModel(backStackEntry)
            CreateTeamScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Challenges.route) {
            PlaceholderScreen(Screen.Challenges.title)
        }
        
        // Detalle de Equipo (ViewModel acotado a la entrada de navegación específica del equipo)
        composable(
            route = "team_detail/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            val viewModel: TeamViewModel = viewModel(backStackEntry)
            TeamDetailScreen(navController = navController, teamId = teamId, viewModel = viewModel)
        }

        // Editar Equipo
        composable(
            route = "edit_team/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            val viewModel: TeamViewModel = viewModel(backStackEntry)
            EditTeamScreen(navController = navController, teamId = teamId, viewModel = viewModel)
        }

        // Tarjeta de Jugador (ViewModel acotado al jugador consultado)
        composable(
            route = "player_card/{playerId}",
            arguments = listOf(navArgument("playerId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0
            val viewModel: PlayerCardViewModel = viewModel(backStackEntry)
            PlayerCardScreen(navController = navController, playerId = playerId, viewModel = viewModel)
        }

        // ChocoFulbo Match Flows
        composable(Screen.SearchMatch.route) {
            SearchMatchScreen(navController)
        }
        composable(Screen.CreateMatch.route) {
            CreateMatchScreen(navController)
        }
        composable(
            route = "match_detail/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 1
            MatchDetailScreen(navController, matchId)
        }
        composable(
            route = "live_match/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 1
            LiveScoreScreen(navController, matchId)
        }

        // Panel de Gestión de Canchas (Lazy Loading ViewModel acotado al ciclo de vida de la ruta)
        composable(Screen.CourtManagement.route) { backStackEntry ->
            val viewModel: CourtManagementViewModel = viewModel(backStackEntry)
            CourtManagementScreen(navController = navController, viewModel = viewModel)
        }

        // Sistema de Torneos y Copas (Lazy Loading ViewModel acotado al ciclo de vida de la ruta)
        composable(Screen.Tournaments.route) { backStackEntry ->
            val viewModel: TournamentsViewModel = viewModel(backStackEntry)
            TournamentsScreen(navController = navController, viewModel = viewModel)
        }

        // Pantalla de Notificaciones
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        // Pago de Cancha y Cuotas
        composable(
            route = "payment/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 1
            PaymentScreen(navController = navController, matchId = matchId)
        }

        // Reservar Canchas
        composable(
            route = "court_reservation/{courtId}",
            arguments = listOf(navArgument("courtId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val courtId = backStackEntry.arguments?.getInt("courtId") ?: 1
            CourtReservationScreen(navController = navController, courtId = courtId)
        }

        // Chat del Plantel / Equipo
        composable(
            route = "team_chat/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 1
            TeamChatScreen(navController = navController, teamId = teamId)
        }

        // Calificación y Acta Post-Partido (Ficha FIFA, MVP, Árbitro)
        composable(
            route = "post_match_review/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.IntType; defaultValue = 1 })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getInt("matchId") ?: 1
            PostMatchReviewScreen(navController = navController, matchId = matchId)
        }

        // Directorio Oficial de Árbitros CODAR
        composable(Screen.RefereeDirectory.route) {
            RefereeDirectoryScreen(navController = navController)
        }

        // Portal Exclusivo del Árbitro CODAR
        composable(Screen.RefereePortal.route) {
            RefereePortalScreen(navController = navController)
        }

        // Quinielas y Apuestas de Pichangas Chiclayo
        composable(Screen.BetsHub.route) {
            BetsHubScreen(navController = navController)
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
    }
}
