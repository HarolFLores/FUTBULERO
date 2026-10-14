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

class PlayerCardViewModel : ViewModel() {

    var cardState by mutableStateOf<PlayerCardState>(PlayerCardState.Idle)
        private set

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        val cleanPath = path.trim().removePrefix("/")
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    fun loadPlayerCard(token: String, playerId: Int) {
        viewModelScope.launch {
            cardState = PlayerCardState.Loading
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
