package com.incanatoapps.retofut.data.network.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class PaginatedResponse<T>(
    @SerializedName("current_page")
    val currentPage: Int,
    val data: List<T>,
    @SerializedName("first_page_url")
    val firstPageUrl: String?,
    @SerializedName("last_page")
    val lastPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val telefono: String?,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class AuthData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    val user: UserResponse?
)

data class UserResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val foto: String?,
    @SerializedName("player_profile")
    val playerProfile: PlayerProfile?,
    @SerializedName("player_stats")
    val playerStats: PlayerStats?
)

data class PlayerProfile(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("posicion_principal")
    val posicionPrincipal: String?,
    @SerializedName("rating_general")
    val ratingGeneral: Int,
    val ritmo: Int,
    val disparo: Int,
    val pase: Int,
    val regate: Int,
    val defensa: Int,
    val fisico: Int
)

data class PlayerStats(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val partidos: Int,
    val victorias: Int,
    val empates: Int,
    val derrotas: Int,
    val goles: Int,
    val asistencias: Int,
    val mvp: Int,
    val amarillas: Int,
    val rojas: Int,
    val reputacion: Int,
    @SerializedName("asistencia_pct")
    val asistenciaPct: String?
)

data class UpdateProfileRequest(
    val nombre: String? = null,
    val telefono: String? = null,
    @SerializedName("posicion_principal")
    val posicionPrincipal: String? = null,
    val foto: String? = null
)

data class PlayerCardResponse(
    val id: Int,
    val nombre: String,
    val foto: String?,
    val posicion: String?,
    val rating: Int,
    val stats: CardStats,
    val performance: CardPerformance
)

data class CardStats(
    val PAC: Int,
    val SHO: Int,
    val PAS: Int,
    val DRI: Int,
    val DEF: Int,
    val PHY: Int
)

data class CardPerformance(
    @SerializedName("partidos_jugados")
    val partidosJugados: Int,
    val goles: Int,
    val asistencias: Int
)

// --- Rankings & Teams ---

data class RankingPlayer(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?,
    val foto: String?,
    val estado: String?,
    @SerializedName("player_profile")
    val playerProfile: PlayerProfile?
)

data class Team(
    val id: Int,
    val nombre: String,
    val logo: String?,
    val ciudad: String?,
    val descripcion: String?,
    @SerializedName("formato_preferido")
    val formatoPreferido: String?,
    @SerializedName("rating_general")
    val ratingGeneral: Int,
    val partidos: Int,
    val victorias: Int,
    val empates: Int,
    val derrotas: Int,
    @SerializedName("goles_favor")
    val golesFavor: Int,
    @SerializedName("goles_contra")
    val golesContra: Int,
    @SerializedName("diferencia_goles")
    val diferenciaGoles: Int,
    @SerializedName("team_stats")
    val teamStats: TeamStats?,
    val capitan: UserResponse?,
    @SerializedName("team_members")
    val teamMembers: List<TeamMember>?
)

data class TeamMember(
    @SerializedName("user_id")
    val userId: Int,
    val rol: String,
    val estado: String,
    val user: UserResponse?
)

data class TeamStats(
    val id: Int,
    @SerializedName("team_id")
    val teamId: Int,
    @SerializedName("partidos_jugados")
    val partidosJugados: Int,
    val victorias: Int,
    val empates: Int,
    val derrotas: Int,
    @SerializedName("goles_favor")
    val golesFavor: Int,
    @SerializedName("goles_contra")
    val golesContra: Int,
    @SerializedName("diferencia_goles")
    val diferenciaGoles: Int,
    val reputacion: Int
)

data class CreateTeamRequest(
    val nombre: String,
    val logo: String?,
    val ciudad: String?,
    val descripcion: String?,
    @SerializedName("formato_preferido")
    val formatoPreferido: String?
)

data class UpdateTeamRequest(
    val nombre: String? = null,
    val logo: String? = null,
    val ciudad: String? = null,
    val descripcion: String? = null,
    @SerializedName("formato_preferido")
    val formatoPreferido: String? = null
)

data class InvitePlayerRequest(
    @SerializedName("user_id")
    val userId: Int
)

data class TeamInvitationResponse(
    @SerializedName("team_id")
    val teamId: Int,
    @SerializedName("user_id")
    val userId: Int,
    val rol: String,
    val estado: String
)

data class JoinTeamResponse(
    @SerializedName("team_id")
    val teamId: Int,
    @SerializedName("user_id")
    val userId: Int,
    val rol: String,
    val estado: String,
    @SerializedName("joined_at")
    val joinedAt: String?
)

data class TransferCaptaincyRequest(
    @SerializedName("new_capitan_id")
    val newCapitanId: Int
)
