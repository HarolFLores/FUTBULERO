package com.incanatoapps.retofut.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.PlayerCardResponse
import com.incanatoapps.retofut.data.network.model.UserResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: UserResponse, val card: PlayerCardResponse? = null) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    data class Success(val message: String) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

data class PostMatchRating(
    val stars: Int, // 1 a 5
    val goals: Int,
    val assists: Int,
    val defenseRecoveries: Int,
    val matchTitle: String = "Pichanga Oficial"
)

data class LevelUpOutcome(
    val previousOvr: Int,
    val newOvr: Int,
    val earnedXp: Int,
    val previousLevel: Int,
    val newLevel: Int,
    val previousStats: com.incanatoapps.retofut.data.network.model.CardStats,
    val newStats: com.incanatoapps.retofut.data.network.model.CardStats,
    val levelUpAchieved: Boolean,
    val statBoosts: Map<String, Int>
)

/**
 * ProfileViewModel optimizado con carga bajo demanda (On-Demand Fetching):
 * - Sin peticiones HTTP en bloque init {}.
 * - Estado inicial en Idle.
 * - Desacopla la carga del perfil del usuario de la tarjeta secundaria para no bloquear la UI.
 * - Soporte de caché en memoria para evitar saturación de red en recomposiciones.
 * - Gamificación post-partido: XP, subida de nivel y actualización de carta FIFA.
 */
class ProfileViewModel : ViewModel() {

    var profileState by mutableStateOf<ProfileState>(ProfileState.Idle)
        private set

    var updateState by mutableStateOf<UpdateState>(UpdateState.Idle)
        private set

    var playerXp by mutableStateOf(1250)
        private set

    var playerLevel by mutableStateOf(5)
        private set

    var lastLevelUpOutcome by mutableStateOf<LevelUpOutcome?>(null)
        private set

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

        val currentState = profileState
        if (currentState is ProfileState.Success) {
            profileState = currentState.copy(card = updatedCard)
        }

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

    /**
     * Carga bajo demanda del perfil. Si ya está cargado y no se fuerza la recarga, reutiliza el estado.
     */
    fun loadProfile(token: String, forceReload: Boolean = false) {
        if (!forceReload && profileState is ProfileState.Success) {
            return
        }
        viewModelScope.launch {
            profileState = ProfileState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val profileResponse = RetrofitClient.authService.getMyProfile(bearerToken)
                
                val body = profileResponse.body()
                if (profileResponse.isSuccessful && body != null && body.success && body.data != null) {
                    val user = body.data.copy(
                        foto = getFullImageUrl(body.data.foto)
                    )
                    
                    // Publicar inmediatamente el perfil del usuario para respuesta visual instantánea
                    profileState = ProfileState.Success(user, null)
                    
                    // Cargar la tarjeta de jugador de forma asíncrona diferida sin bloquear el perfil
                    try {
                        val cardResponse = RetrofitClient.authService.getPlayerCard(bearerToken, user.id)
                        val card = if (cardResponse.isSuccessful) cardResponse.body()?.data?.let {
                            it.copy(foto = getFullImageUrl(it.foto))
                        } else null
                        
                        profileState = ProfileState.Success(user, card)
                    } catch (_: Exception) {
                        // Mantener el perfil exitoso aun si la tarjeta secundaria falla
                    }
                } else {
                    profileState = ProfileState.Error(body?.message ?: "Error al cargar perfil")
                }
            } catch (e: Exception) {
                profileState = ProfileState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun updateProfile(
        context: Context,
        token: String,
        nombre: String?,
        telefono: String?,
        posicion: String?,
        fotoUri: Uri?
    ) {
        viewModelScope.launch {
            updateState = UpdateState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

                val nombrePart = nombre?.let { MultipartBody.Part.createFormData("nombre", it) }
                val telefonoPart = telefono?.let { MultipartBody.Part.createFormData("telefono", it) }
                val posicionPart = posicion?.let { MultipartBody.Part.createFormData("posicion_principal", it) }

                val fotoPart = fotoUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("foto", file.name, requestFile)
                }

                val response = RetrofitClient.authService.updateProfile(
                    token = bearerToken,
                    nombre = nombrePart,
                    telefono = telefonoPart,
                    posicionPrincipal = posicionPart,
                    foto = fotoPart
                )
                
                val body = response.body()
                
                if (response.isSuccessful && body != null && body.success) {
                    updateState = UpdateState.Success(body.message)
                    loadProfile(token, forceReload = true)
                } else {
                    updateState = UpdateState.Error(body?.message ?: "Error al actualizar perfil")
                }
            } catch (e: Exception) {
                updateState = UpdateState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun resetUpdateState() {
        updateState = UpdateState.Idle
    }
}
