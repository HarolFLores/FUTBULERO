package com.incanatoapps.retofut.data.network

import com.incanatoapps.retofut.data.network.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    // --- Autenticación ---
    @POST("auth/register")
    @Headers("Accept: application/json")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    @Headers("Accept: application/json")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/forgot-password")
    @Headers("Accept: application/json")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    @Headers("Accept: application/json")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @GET("auth/me")
    @Headers("Accept: application/json")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>

    @POST("auth/logout")
    @Headers("Accept: application/json")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Unit>>

    @POST("auth/refresh")
    @Headers("Accept: application/json")
    suspend fun refresh(@Header("Authorization") token: String): Response<ApiResponse<AuthData>>

    // --- Perfiles ---
    @GET("profile")
    @Headers("Accept: application/json")
    suspend fun getMyProfile(@Header("Authorization") token: String): Response<ApiResponse<UserResponse>>

    @Multipart
    @POST("profile")
    @Headers("Accept: application/json")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part nombre: MultipartBody.Part? = null,
        @Part telefono: MultipartBody.Part? = null,
        @Part posicionPrincipal: MultipartBody.Part? = null,
        @Part foto: MultipartBody.Part? = null
    ): Response<ApiResponse<UserResponse>>

    @GET("players/{id}")
    @Headers("Accept: application/json")
    suspend fun getPlayerProfile(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<UserResponse>>

    @GET("players/{id}/card")
    @Headers("Accept: application/json")
    suspend fun getPlayerCard(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<PlayerCardResponse>>

    @GET("players")
    @Headers("Accept: application/json")
    suspend fun searchPlayers(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("posicion_principal") posicion: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 15
    ): Response<ApiResponse<PaginatedResponse<UserResponse>>>

    // --- Rankings ---
    @GET("rankings/players")
    @Headers("Accept: application/json")
    suspend fun getRankingPlayers(@Header("Authorization") token: String): Response<ApiResponse<List<RankingPlayer>>>

    @GET("rankings/teams")
    @Headers("Accept: application/json")
    suspend fun getRankingTeams(@Header("Authorization") token: String): Response<ApiResponse<List<Team>>>

    // --- Equipos ---
    @GET("teams")
    @Headers("Accept: application/json")
    suspend fun getTeams(@Header("Authorization") token: String): Response<ApiResponse<List<Team>>>

    @Multipart
    @POST("teams")
    @Headers("Accept: application/json")
    suspend fun createTeam(
        @Header("Authorization") token: String,
        @Part nombre: MultipartBody.Part,
        @Part ciudad: MultipartBody.Part? = null,
        @Part descripcion: MultipartBody.Part? = null,
        @Part formatoPreferido: MultipartBody.Part? = null,
        @Part logo: MultipartBody.Part? = null
    ): Response<ApiResponse<Team>>

    @GET("teams/{id}")
    @Headers("Accept: application/json")
    suspend fun getTeam(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Team>>

    @Multipart
    @POST("teams/{id}")
    @Headers("Accept: application/json")
    suspend fun updateTeam(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part method: MultipartBody.Part,
        @Part nombre: MultipartBody.Part? = null,
        @Part ciudad: MultipartBody.Part? = null,
        @Part descripcion: MultipartBody.Part? = null,
        @Part formatoPreferido: MultipartBody.Part? = null,
        @Part logo: MultipartBody.Part? = null
    ): Response<ApiResponse<Team>>

    @DELETE("teams/{id}")
    @Headers("Accept: application/json")
    suspend fun deleteTeam(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>

    @DELETE("teams/{id}/members/{user_id}")
    @Headers("Accept: application/json")
    suspend fun removeTeamMember(
        @Header("Authorization") token: String,
        @Path("id") teamId: Int,
        @Path("user_id") userId: Int
    ): Response<ApiResponse<Unit>>

    @POST("teams/{id}/invite")
    @Headers("Accept: application/json")
    suspend fun invitePlayer(
        @Header("Authorization") token: String,
        @Path("id") teamId: Int,
        @Body request: InvitePlayerRequest
    ): Response<ApiResponse<TeamInvitationResponse>>

    @POST("teams/{id}/join")
    @Headers("Accept: application/json")
    suspend fun joinTeam(
        @Header("Authorization") token: String,
        @Path("id") teamId: Int
    ): Response<ApiResponse<JoinTeamResponse>>

    @POST("teams/{id}/transfer-captaincy")
    @Headers("Accept: application/json")
    suspend fun transferCaptaincy(
        @Header("Authorization") token: String,
        @Path("id") teamId: Int,
        @Body request: TransferCaptaincyRequest
    ): Response<ApiResponse<Team>>

    // --- Media ---
    @GET("media")
    suspend fun getMedia(
        @Header("Authorization") token: String,
        @Query("path") path: String
    ): Response<ResponseBody>
}
