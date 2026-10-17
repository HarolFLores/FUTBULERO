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
import com.incanatoapps.retofut.ui.screens.profile.EditProfileScreen
import com.incanatoapps.retofut.ui.screens.profile.PlayerCardScreen
import com.incanatoapps.retofut.ui.screens.profile.ProfileScreen
import com.incanatoapps.retofut.ui.screens.rankings.RankingsScreen
import com.incanatoapps.retofut.ui.screens.teams.CreateTeamScreen
import com.incanatoapps.retofut.ui.screens.teams.EditTeamScreen
import com.incanatoapps.retofut.ui.screens.teams.MyTeamsScreen
import com.incanatoapps.retofut.ui.screens.teams.TeamDetailScreen
import com.incanatoapps.retofut.ui.screens.matches.SearchMatchScreen
import com.incanatoapps.retofut.ui.screens.matches.CreateMatchScreen
import com.incanatoapps.retofut.ui.screens.matches.MatchDetailScreen
import com.incanatoapps.retofut.ui.screens.matches.LiveScoreScreen

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
        // Auth Flow
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

        // Main App
        composable(Screen.Home.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Rankings.route) {
            RankingsScreen(navController)
        }
        composable(Screen.Shop.route) {
            PlaceholderScreen(Screen.Shop.title)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
        composable(Screen.MyTeam.route) {
            MyTeamsScreen(navController)
        }
        composable(Screen.CreateTeam.route) {
            CreateTeamScreen(navController)
        }
        composable(Screen.Challenges.route) {
            PlaceholderScreen(Screen.Challenges.title)
        }
        
        // Detalle de Equipo
        composable(
            route = "team_detail/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            TeamDetailScreen(navController, teamId)
        }

        // Editar Equipo
        composable(
            route = "edit_team/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt("teamId") ?: 0
            EditTeamScreen(navController, teamId)
        }

        // Tarjeta de Jugador
        composable(
            route = "player_card/{playerId}",
            arguments = listOf(navArgument("playerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getInt("playerId") ?: 0
            PlayerCardScreen(navController, playerId)
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
