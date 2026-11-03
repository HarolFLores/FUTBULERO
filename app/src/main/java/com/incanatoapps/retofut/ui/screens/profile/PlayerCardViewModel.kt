package com.incanatoapps.retofut.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse
import kotlinx.coroutines.launch

sealed class PlayerCardState {
    object Idle : PlayerCardState()
    object Loading : PlayerCardState()
    data class Success(val card: PlayerCardResponse) : PlayerCardState()
    data class Error(val message: String) : PlayerCardState()
}

/**
 * PlayerCardViewModel optimizado para carga bajo demanda:
 * - Sin peticiones HTTP en bloque init {}.
 * - Estado inicial en Idle.
 * - Validación de caché por playerId para evitar llamadas redundantes a RetrofitClient.
 */
class PlayerCardViewModel : ViewModel() {

    var cardState by mutableStateOf<PlayerCardState>(PlayerCardState.Idle)
        private set

    var playerXp by mutableStateOf(1450)
        private set

    var playerLevel by mutableStateOf(6)
        private set

    var lastLevelUpOutcome by mutableStateOf<LevelUpOutcome?>(null)
        private set

    private var lastLoadedPlayerId: Int? = null

    fun applyPostMatchRating(
        rating: PostMatchRating,
        card: PlayerCardResponse
    ): LevelUpOutcome {
        val earnedXp = (rating.stars * 200) + (rating.goals * 120) + (rating.assists * 80) + (rating.defenseRecoveries * 60)
        val prevLevel = playerLevel
        playerXp += earnedXp

        val boosts = mutableMapOf<String, Int>()

        val pacBoost = if (rating.stars >= 4) 1 else 0
        val shoBoost = (rating.goals.coerceIn(0, 2)) + (if (rating.stars == 5) 1 else 0)
        val pasBoost = (rating.assists.coerceIn(0, 2)) + (if (rating.stars >= 4) 1 else 0)
        val driBoost = if (rating.stars >= 4) 1 else 0
        val defBoost = (rating.defenseRecoveries.coerceIn(0, 2))
        val phyBoost = if (rating.defenseRecoveries > 0 || rating.stars >= 4) 1 else 0

        if (pacBoost > 0) boosts["PAC"] = pacBoost
        if (shoBoost > 0) boosts["SHO"] = shoBoost
        if (pasBoost > 0) boosts["PAS"] = pasBoost
        if (driBoost > 0) boosts["DRI"] = driBoost
        if (defBoost > 0) boosts["DEF"] = defBoost
        if (phyBoost > 0) boosts["PHY"] = phyBoost

        val newStats = card.stats.copy(
            PAC = (card.stats.PAC + pacBoost).coerceAtMost(99),
            SHO = (card.stats.SHO + shoBoost).coerceAtMost(99),
            PAS = (card.stats.PAS + pasBoost).coerceAtMost(99),
            DRI = (card.stats.DRI + driBoost).coerceAtMost(99),
            DEF = (card.stats.DEF + defBoost).coerceAtMost(99),
            PHY = (card.stats.PHY + phyBoost).coerceAtMost(99)
        )

        val ovrBoost = if (rating.stars >= 3 || earnedXp >= 600 || boosts.values.sum() >= 2) 1 else 0
        val newOvr = (card.rating + ovrBoost).coerceAtMost(99)
        if (ovrBoost > 0) playerLevel += 1

        val newPerf = card.performance.copy(
            partidosJugados = card.performance.partidosJugados + 1,
            goles = card.performance.goles + rating.goals,
            asistencias = card.performance.asistencias + rating.assists
        )

        val updatedCard = card.copy(
            rating = newOvr,
            stats = newStats,
            performance = newPerf
        )

        cardState = PlayerCardState.Success(updatedCard)

        val outcome = LevelUpOutcome(
            previousOvr = card.rating,
            newOvr = newOvr,
            earnedXp = earnedXp,
            previousLevel = prevLevel,
            newLevel = playerLevel,
            previousStats = card.stats,
            newStats = newStats,
            levelUpAchieved = ovrBoost > 0,
            statBoosts = boosts
        )
        lastLevelUpOutcome = outcome
        return outcome
    }

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        val cleanPath = path.trim().removePrefix("/")
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    fun loadPlayerCard(token: String, playerId: Int, forceReload: Boolean = false) {
        if (!forceReload && cardState is PlayerCardState.Success && lastLoadedPlayerId == playerId) {
            return
        }
        viewModelScope.launch {
            cardState = PlayerCardState.Loading
            lastLoadedPlayerId = playerId
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.getPlayerCard(bearerToken, playerId)
                val body = response.body()
                
                if (response.isSuccessful && body != null && body.success && body.data != null) {
                    val card = body.data.copy(foto = getFullImageUrl(body.data.foto))
                    cardState = PlayerCardState.Success(card)
                } else {
                    cardState = PlayerCardState.Error(body?.message ?: "Error al cargar la tarjeta")
                }
            } catch (e: Exception) {
                cardState = PlayerCardState.Error(e.message ?: "Error de conexión")
            }
        }
    }
}
