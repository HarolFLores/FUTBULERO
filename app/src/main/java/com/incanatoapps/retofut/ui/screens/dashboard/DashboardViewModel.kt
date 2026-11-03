package com.incanatoapps.retofut.ui.screens.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.RankingPlayer
import com.incanatoapps.retofut.data.network.model.Team
import kotlinx.coroutines.launch

sealed class DashboardState<out T> {
    object Idle : DashboardState<Nothing>()
    object Loading : DashboardState<Nothing>()
    data class Success<T>(val data: T) : DashboardState<T>()
    data class Error(val message: String) : DashboardState<Nothing>()
}

/**
 * DashboardViewModel optimizado con carga bajo demanda (On-Demand Fetching) y Lazy Loading.
 * - Sin bloques `init {}` que disparen peticiones HTTP prematuras.
 * - Estados iniciales en `Idle` hasta que la UI activa solicita datos mediante `LaunchedEffect`.
 * - Funciones de carga independientes con caché en memoria para evitar saturación de red y recomposiciones.
 */
class DashboardViewModel : ViewModel() {

    var playersRankingState by mutableStateOf<DashboardState<List<RankingPlayer>>>(DashboardState.Idle)
        private set

    var teamsState by mutableStateOf<DashboardState<List<Team>>>(DashboardState.Idle)
        private set

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        val cleanPath = path.trim().removePrefix("/")
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    /**
     * Carga bajo demanda del ranking de jugadores (goleadores).
     * Si los datos ya se cargaron exitosamente y no se fuerza la recarga, reutiliza el estado en memoria.
     */
    fun loadPlayersRanking(token: String, forceReload: Boolean = false) {
        if (!forceReload && playersRankingState is DashboardState.Success) {
            return
        }
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        viewModelScope.launch {
            playersRankingState = DashboardState.Loading
            try {
                val response = RetrofitClient.authService.getRankingPlayers(bearerToken)
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
    }

    /**
     * Carga bajo demanda del ranking de equipos (tabla de posiciones).
     * Si los datos ya se cargaron exitosamente y no se fuerza la recarga, reutiliza el estado en memoria.
     */
    fun loadTeamsRanking(token: String, forceReload: Boolean = false) {
        if (!forceReload && teamsState is DashboardState.Success) {
            return
        }
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        viewModelScope.launch {
            teamsState = DashboardState.Loading
            try {
                val response = RetrofitClient.authService.getRankingTeams(bearerToken)
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

    /**
     * Carga completa solo si es requerida por una vista compuesta.
     */
    fun loadDashboardData(token: String, forceReload: Boolean = false) {
        loadPlayersRanking(token, forceReload)
        loadTeamsRanking(token, forceReload)
    }
}
