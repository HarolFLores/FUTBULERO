package com.incanatoapps.retofut.ui.screens.teams

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.CreateTeamRequest
import com.incanatoapps.retofut.data.network.model.Team
import kotlinx.coroutines.launch

sealed class TeamState {
    object Idle : TeamState()
    object Loading : TeamState()
    data class Success(val team: Team) : TeamState()
    data class Error(val message: String) : TeamState()
}

class TeamViewModel : ViewModel() {

    var teamState by mutableStateOf<TeamState>(TeamState.Idle)
        private set

    fun createTeam(token: String, nombre: String, logo: String?, ciudad: String, descripcion: String?, formato: String) {
        viewModelScope.launch {
            teamState = TeamState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val request = CreateTeamRequest(nombre, logo, ciudad, descripcion, formato)
                val response = RetrofitClient.authService.createTeam(bearerToken, request)
                val body = response.body()
                
                if (response.isSuccessful && body != null && body.success) {
                    teamState = TeamState.Success(body.data!!)
                } else {
                    teamState = TeamState.Error(body?.message ?: "Error al crear el equipo")
                }
            } catch (e: Exception) {
                teamState = TeamState.Error(e.message ?: "Error de conexión")
            }
        }
    }
    
    fun resetState() {
        teamState = TeamState.Idle
    }
}
