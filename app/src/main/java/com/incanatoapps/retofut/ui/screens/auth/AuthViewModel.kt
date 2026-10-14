package com.incanatoapps.retofut.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.incanatoapps.retofut.data.network.RetrofitClient
import com.incanatoapps.retofut.data.network.model.ApiResponse
import com.incanatoapps.retofut.data.network.model.ForgotPasswordRequest
import com.incanatoapps.retofut.data.network.model.LoginRequest
import com.incanatoapps.retofut.data.network.model.RegisterRequest
import com.incanatoapps.retofut.data.network.model.ResetPasswordRequest
import com.incanatoapps.retofut.data.network.model.AuthData
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String? = null, val userId: Int? = null, val message: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.authService.login(LoginRequest(email, password))
                handleAuthResponse(response)
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(nombre: String, email: String, telefono: String, password: String, passwordConfirmation: String) {
        val trimmedName = nombre.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = telefono.trim()

        if (trimmedName.isBlank()) {
            authState = AuthState.Error("Por favor ingresa tu nombre completo")
            return
        }
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            authState = AuthState.Error("Ingresa un correo electrónico válido")
            return
        }
        if (password.isBlank()) {
            authState = AuthState.Error("Ingresa una contraseña")
            return
        }
        if (password.length < 6) {
            authState = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (password != passwordConfirmation) {
            authState = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = RegisterRequest(
                    nombre = trimmedName,
                    email = trimmedEmail,
                    telefono = trimmedPhone.ifEmpty { null },
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
                val response = RetrofitClient.authService.register(request)
                handleAuthResponse(response)
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun forgotPassword(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            authState = AuthState.Error("Ingresa un correo electrónico válido")
            return
        }

        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = RetrofitClient.authService.forgotPassword(ForgotPasswordRequest(trimmedEmail))
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    authState = AuthState.Success(message = body.message)
                } else {
                    val errorMsg = parseErrorMessage(response) ?: "Error al enviar correo"
                    authState = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    fun resetPassword(email: String, code: String, password: String, passwordConfirmation: String) {
        val trimmedEmail = email.trim()
        if (password != passwordConfirmation) {
            authState = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val request = ResetPasswordRequest(
                    email = trimmedEmail,
                    code = code,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
                val response = RetrofitClient.authService.resetPassword(request)
                val body = response.body()
                if (response.isSuccessful && body != null && body.success) {
                    authState = AuthState.Success(message = body.message)
                } else {
                    val errorMsg = parseErrorMessage(response) ?: "Error al restablecer contraseña"
                    authState = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    private fun <T> handleAuthResponse(response: Response<ApiResponse<T>>) {
        val body = response.body()
        if (response.isSuccessful && body != null && body.success) {
            val data = body.data
            var token: String? = null
            var userId: Int? = null
            if (data is AuthData) {
                token = data.accessToken
                userId = data.user?.id
            }
            authState = AuthState.Success(token = token, userId = userId, message = body.message)
        } else {
            val errorMsg = parseErrorMessage(response) ?: "Error en la operación (${response.code()})"
            authState = AuthState.Error(errorMsg)
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String? {
        return try {
            val errorBody = response.errorBody()?.string() ?: return response.message().ifBlank { null }
            if (errorBody.isBlank()) return response.message().ifBlank { null }

            val jsonObject = Gson().fromJson(errorBody, com.google.gson.JsonObject::class.java)
                ?: return response.message().ifBlank { null }

            // 1. Extraer errores de validación de campos ("errors": { "email": ["..."], ... })
            if (jsonObject.has("errors") && jsonObject.get("errors").isJsonObject) {
                val errorsObj = jsonObject.getAsJsonObject("errors")
                val messages = mutableListOf<String>()
                for ((_, value) in errorsObj.entrySet()) {
                    if (value.isJsonArray) {
                        for (elem in value.asJsonArray) {
                            messages.add(elem.asString)
                        }
                    } else if (value.isJsonPrimitive) {
                        messages.add(value.asString)
                    }
                }
                if (messages.isNotEmpty()) {
                    return messages.joinToString("\n")
                }
            }

            // 2. Extraer campo "message"
            if (jsonObject.has("message") && jsonObject.get("message").isJsonPrimitive) {
                val msg = jsonObject.get("message").asString
                if (msg.isNotBlank()) return msg
            }

            // 3. Extraer campo "error"
            if (jsonObject.has("error") && jsonObject.get("error").isJsonPrimitive) {
                val err = jsonObject.get("error").asString
                if (err.isNotBlank()) return err
            }

            response.message().ifBlank { null }
        } catch (e: Exception) {
            null
        }
    }

    fun resetState() {
        authState = AuthState.Idle
    }
}
