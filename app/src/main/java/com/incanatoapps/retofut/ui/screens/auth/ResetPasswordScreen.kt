package com.incanatoapps.retofut.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.incanatoapps.retofut.R
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.authState
    val isSuccess = authState is AuthState.Success

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            IconButton(
                onClick = { 
                    viewModel.resetState()
                    navController.popBackStack() 
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isSuccess) {
                    Text(
                        text = "Nueva Contraseña",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = BrandGreen,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ingresa el código de 6 dígitos enviado a tu correo.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (authState is AuthState.Error) {
                                Text(
                                    text = authState.message, 
                                    color = Color.Red, 
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo electrónico") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                leadingIcon = { Icon(Icons.Outlined.Email, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                enabled = authState !is AuthState.Loading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = code,
                                onValueChange = { if (it.length <= 6) code = it },
                                label = { Text("Código de verificación") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                leadingIcon = { Icon(Icons.Outlined.Message, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = authState !is AuthState.Loading,
                                placeholder = { Text("123456") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                enabled = authState !is AuthState.Loading,
                                leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                enabled = authState !is AuthState.Loading,
                                leadingIcon = { Icon(Icons.Outlined.LockReset, null) },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = { 
                                    viewModel.resetPassword(email, code, password, confirmPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = authState !is AuthState.Loading && email.isNotBlank() && code.length == 6 && password.isNotBlank(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                            ) {
                                if (authState is AuthState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TextPrimary)
                                } else {
                                    Text("ACTUALIZAR CONTRASEÑA", fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                } else {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        null,
                        modifier = Modifier.size(120.dp),
                        tint = BrandGreen
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "¡Todo listo!",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = BrandGreen
                    )
                    Text(
                        text = (authState as AuthState.Success).message ?: "Tu contraseña ha sido actualizada con éxito.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = { 
                            viewModel.resetState()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text("VOLVER AL INICIO DE SESIÓN", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}
