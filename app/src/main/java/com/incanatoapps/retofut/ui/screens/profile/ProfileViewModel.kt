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

class ProfileViewModel : ViewModel() {

    var profileState by mutableStateOf<ProfileState>(ProfileState.Loading)
        private set

    var updateState by mutableStateOf<UpdateState>(UpdateState.Idle)
        private set

    private fun getFullImageUrl(path: String?): String? {
        if (path.isNullOrEmpty()) return null
        if (path.startsWith("http")) return path.replace("http://", "https://")
        
        // Limpiamos el path: eliminamos espacios y cualquier barra inicial para evitar dobles barras en la URL
        val cleanPath = path.trim().removePrefix("/")
        // Concatenamos dinámicamente usando la BASE_URL de RetrofitClient
        return "${RetrofitClient.BASE_URL}media?path=$cleanPath"
    }

    fun loadProfile(token: String) {
        viewModelScope.launch {
            profileState = ProfileState.Loading
            try {
                val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
                val profileResponse = RetrofitClient.authService.getMyProfile(bearerToken)
                
                val body = profileResponse.body()
                if (profileResponse.isSuccessful && body != null && body.success) {
                    val user = body.data!!.copy(
                        foto = getFullImageUrl(body.data.foto)
                    )
                    
                    // Cargar la tarjeta FIFA y procesar su imagen
                    val cardResponse = RetrofitClient.authService.getPlayerCard(bearerToken, user.id)
                    val card = if (cardResponse.isSuccessful) cardResponse.body()?.data?.let {
                        it.copy(foto = getFullImageUrl(it.foto))
                    } else null
                    
                    profileState = ProfileState.Success(user, card)
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
                    loadProfile(token)
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
