package com.incanatoapps.retofut.ui.screens.teams

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.InvitePlayerRequest
import com.incanatoapps.retofut.data.network.model.Team
import com.incanatoapps.retofut.data.network.model.TransferCaptaincyRequest
import com.incanatoapps.retofut.data.network.model.UserResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class TeamState {
    object Idle : TeamState()
    object Loading : TeamState()
    data class Success(val team: Team) : TeamState()
    data class Error(val message: String) : TeamState()
}

sealed class TeamsListState {
    object Loading : TeamsListState()
    data class Success(val teams: List<Team>) : TeamsListState()
    data class Error(val message: String) : TeamsListState()
}

sealed class SearchPlayersState {
    object Idle : SearchPlayersState()
    object Loading : SearchPlayersState()
    data class Success(val players: List<UserResponse>) : SearchPlayersState()
    data class Error(val message: String) : SearchPlayersState()
}

class TeamViewModel : ViewModel() {

    var teamState by mutableStateOf<TeamState>(TeamState.Idle)
        private set

    var teamsListState by mutableStateOf<TeamsListState>(TeamsListState.Loading)
        private set

    var teamDetailState by mutableStateOf<TeamState>(TeamState.Idle)
        private set

    var searchPlayersState by mutableStateOf<SearchPlayersState>(SearchPlayersState.Idle)
        private set

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        val cleanPath = path.trim().removePrefix("/")
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    fun loadMyTeams(token: String) {
        viewModelScope.launch {
            teamsListState = TeamsListState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.getTeams(bearerToken)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    val teams = body.data?.map { it.copy(logo = getFullImageUrl(it.logo)) } ?: emptyList()
                    teamsListState = TeamsListState.Success(teams)
                } else {
                    teamsListState = TeamsListState.Error(body?.message ?: "Error al cargar tus equipos")
                }
            } catch (e: Exception) {
                teamsListState = TeamsListState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun loadTeamDetails(token: String, id: Int) {
        viewModelScope.launch {
            teamDetailState = TeamState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.getTeam(bearerToken, id)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success && body.data != null) {
                    val team = body.data.copy(
                        logo = getFullImageUrl(body.data.logo),
                        teamMembers = body.data.teamMembers?.map { member ->
                            member.copy(user = member.user?.copy(foto = getFullImageUrl(member.user.foto)))
                        }
                    )
                    teamDetailState = TeamState.Success(team)
                } else {
                    teamDetailState = TeamState.Error(body?.message ?: "Error al cargar detalles del equipo")
                }
            } catch (e: Exception) {
                teamDetailState = TeamState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun createTeam(context: Context, token: String, nombre: String, logoUri: Uri?, ciudad: String?, descripcion: String?, formato: String?) {
        viewModelScope.launch {
            teamState = TeamState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val nombrePart = MultipartBody.Part.createFormData("nombre", nombre)
                val ciudadPart = ciudad?.let { MultipartBody.Part.createFormData("ciudad", it) }
                val descripcionPart = descripcion?.let { MultipartBody.Part.createFormData("descripcion", it) }
                val formatoPart = formato?.let { MultipartBody.Part.createFormData("formato_preferido", it) }
                val logoPart = logoUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("logo", file.name, requestFile)
                }
                val response = RetrofitClient.authService.createTeam(bearerToken, nombrePart, ciudadPart, descripcionPart, formatoPart, logoPart)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    val team = body.data!!.copy(logo = getFullImageUrl(body.data.logo))
                    teamState = TeamState.Success(team)
                } else {
                    teamState = TeamState.Error(body?.message ?: "Error al crear el equipo")
                }
            } catch (e: Exception) {
                teamState = TeamState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun updateTeam(context: Context, token: String, id: Int, nombre: String, logoUri: Uri?, ciudad: String?, descripcion: String?, formato: String?) {
        viewModelScope.launch {
            teamState = TeamState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val methodPart = MultipartBody.Part.createFormData("_method", "PUT")
                val nombrePart = MultipartBody.Part.createFormData("nombre", nombre)
                val ciudadPart = ciudad?.let { MultipartBody.Part.createFormData("ciudad", it) }
                val descripcionPart = descripcion?.let { MultipartBody.Part.createFormData("descripcion", it) }
                val formatoPart = formato?.let { MultipartBody.Part.createFormData("formato_preferido", it) }
                val logoPart = logoUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("logo", file.name, requestFile)
                }
                val response = RetrofitClient.authService.updateTeam(bearerToken, id, methodPart, nombrePart, ciudadPart, descripcionPart, formatoPart, logoPart)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    val team = body.data!!.copy(logo = getFullImageUrl(body.data.logo))
                    teamState = TeamState.Success(team)
                    loadTeamDetails(token, id)
                } else {
                    teamState = TeamState.Error(body?.message ?: "Error al actualizar equipo")
                }
            } catch (e: Exception) {
                teamState = TeamState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun deleteTeam(token: String, id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.deleteTeam(bearerToken, id)
                if (response.isSuccessful) onSuccess()
            } catch (e: Exception) {}
        }
    }

    fun removeMember(token: String, teamId: Int, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.removeTeamMember(bearerToken, teamId, userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                    loadTeamDetails(token, teamId)
                }
            } catch (e: Exception) {}
        }
    }

    fun searchPlayers(token: String, query: String) {
        viewModelScope.launch {
            searchPlayersState = SearchPlayersState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.searchPlayers(bearerToken, search = query)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    val players = body.data?.data?.map { it.copy(foto = getFullImageUrl(it.foto)) } ?: emptyList()
                    searchPlayersState = SearchPlayersState.Success(players)
                } else {
                    searchPlayersState = SearchPlayersState.Error(body?.message ?: "Error al buscar jugadores")
                }
            } catch (e: Exception) {
                searchPlayersState = SearchPlayersState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun invitePlayer(token: String, teamId: Int, userId: Int, onInviteSent: () -> Unit) {
        viewModelScope.launch {
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.invitePlayer(bearerToken, teamId, InvitePlayerRequest(userId))
                if (response.isSuccessful && response.body()?.success == true) {
                    onInviteSent()
                    loadTeamDetails(token, teamId)
                }
            } catch (e: Exception) {}
        }
    }

    fun joinTeam(token: String, teamId: Int, onJoined: () -> Unit) {
        viewModelScope.launch {
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.joinTeam(bearerToken, teamId)
                if (response.isSuccessful && response.body()?.success == true) {
                    onJoined()
                    loadMyTeams(token)
                }
            } catch (e: Exception) {}
        }
    }

    fun transferCaptaincy(token: String, teamId: Int, newCaptainId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val response = RetrofitClient.authService.transferCaptaincy(bearerToken, teamId, TransferCaptaincyRequest(newCaptainId))
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                    loadTeamDetails(token, teamId)
                }
            } catch (e: Exception) {}
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_team_logo_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return file
    }

    fun resetState() {
        teamState = TeamState.Idle
    }
}
