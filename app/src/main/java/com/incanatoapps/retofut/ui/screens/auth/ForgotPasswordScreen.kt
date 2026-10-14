package com.incanatoapps.retofut.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.incanatoapps.retofut.R
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    
    // Reiniciar el estado al entrar a la pantalla para que siempre esté limpia
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    val authState = viewModel.authState
    val isSent = authState is AuthState.Success && authState.message != null

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
                    // No reseteamos aquí para evitar parpadeo
                    navController.popBackStack() 
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isSent) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = BrandGreen,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ingresa tu correo y te enviaremos las instrucciones para restablecerla.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 24.dp)
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
                                enabled = authState !is AuthState.Loading,
                                leadingIcon = { Icon(Icons.Outlined.Email, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = { 
                                    if (email.isNotBlank()) {
                                        viewModel.forgotPassword(email) 
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = authState !is AuthState.Loading && email.isNotBlank(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                            ) {
                                if (authState is AuthState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TextPrimary)
                                } else {
                                    Text("ENVIAR INSTRUCCIONES", fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                } else {
                    Icon(
                        Icons.Outlined.MarkEmailRead,
                        null,
                        modifier = Modifier.size(100.dp),
                        tint = BrandGreen
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "¡Correo Enviado!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = BrandGreen
                    )
                    Text(
                        text = (authState as AuthState.Success).message ?: "Hemos enviado un enlace de recuperación.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = { 
                            navController.navigate(Screen.ResetPassword.route) {
                                popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text("CONTINUAR A RESTABLECER", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = { 
                        // Simplemente volvemos atrás sin resetear el estado aquí
                        navController.popBackStack() 
                    }) {
                        Text("VOLVER AL LOGIN", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
