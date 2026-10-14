package com.incanatoapps.retofut.ui.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.RankingPlayer
import com.incanatoapps.retofut.data.network.model.Team
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class DashboardState<out T> {
    object Loading : DashboardState<Nothing>()
    data class Success<T>(val data: T) : DashboardState<T>()
    data class Error(val message: String) : DashboardState<Nothing>()
}

class DashboardViewModel : ViewModel() {

    var playersRankingState by mutableStateOf<DashboardState<List<RankingPlayer>>>(DashboardState.Loading)
        private set

    var teamsState by mutableStateOf<DashboardState<List<Team>>>(DashboardState.Loading)
        private set

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        
        // Limpiar el path para evitar errores de ruta
        val cleanPath = path.trim().removePrefix("/")
        // Concatenar dinámicamente con el BASE_URL de RetrofitClient
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    fun loadDashboardData(token: String) {
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        viewModelScope.launch {
            coroutineScope {
                launch { loadPlayersRanking(bearerToken) }
                launch { loadTeamsRanking(bearerToken) }
            }
        }
    }

    private suspend fun loadPlayersRanking(token: String) {
        playersRankingState = DashboardState.Loading
        try {
            val response = RetrofitClient.authService.getRankingPlayers(token)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                val players = body.data?.map { it.copy(foto = getFullImageUrl(it.foto)) } ?: emptyList()
                playersRankingState = DashboardState.Success(players)
            } else {
                playersRankingState = DashboardState.Error(body?.message ?: "Error al cargar ranking")
            }
        } catch (e: Exception) {
            playersRankingState = DashboardState.Error(e.message ?: "Error de conexión")
        }
    }

    private suspend fun loadTeamsRanking(token: String) {
        teamsState = DashboardState.Loading
        try {
            val response = RetrofitClient.authService.getRankingTeams(token)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                val teams = body.data?.map { it.copy(logo = getFullImageUrl(it.logo)) } ?: emptyList()
                teamsState = DashboardState.Success(teams)
            } else {
                teamsState = DashboardState.Error(body?.message ?: "Error al cargar ranking de equipos")
            }
        } catch (e: Exception) {
            teamsState = DashboardState.Error(e.message ?: "Error de conexión")
        }
    }
}
