package com.incanatoapps.retofut.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.components.FifaCard
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    LaunchedEffect(Unit) {
        val token = sessionManager.fetchAuthToken()
        if (token != null) {
            viewModel.loadProfile(token)
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val state = viewModel.profileState

    MainScaffold(
        navController = navController,
        title = "Mi Perfil"
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is ProfileState.Loading -> CircularProgressIndicator(color = BrandGreen)
                is ProfileState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = Color.Red)
                    Button(onClick = { 
                        val token = sessionManager.fetchAuthToken()
                        if (token != null) viewModel.loadProfile(token)
                    }) {
                        Text("Reintentar")
                    }
                }
                is ProfileState.Success -> {
                    val user = state.user
                    val card = state.card
                    val stats = user.playerStats
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // FIFA CARD
                        if (card != null) {
                            FifaCard(card = card)
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(380.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Gray.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Cargando Carta FIFA...", color = TextSecondary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { navController.navigate(Screen.EditProfile.route) }) {
                                Icon(Icons.Outlined.Edit, "Editar Perfil", tint = BrandGreen)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Estadísticas Detalladas de Temporada
                        Text(
                            "Estadísticas de Temporada",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatBox("Partidos", stats?.partidos?.toString() ?: "0", Modifier.weight(1f))
                            StatBox("Victorias", stats?.victorias?.toString() ?: "0", Modifier.weight(1f))
                            StatBox("Goles", stats?.goles?.toString() ?: "0", Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatBox("Asistencias", stats?.asistencias?.toString() ?: "0", Modifier.weight(1f))
                            StatBox("MVP", stats?.mvp?.toString() ?: "0", Modifier.weight(1f))
                            StatBox("Reputación", stats?.reputacion?.toString() ?: "0", Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(24.dp))

                        // Información de Usuario
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            color = CardWhite,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                ProfileInfoItem("Nombre", user.nombre)
                                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = BorderGray)
                                ProfileInfoItem("Email", user.email)
                                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = BorderGray)
                                ProfileInfoItem("Teléfono", user.telefono ?: "No registrado")
                                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = BorderGray)
                                ProfileInfoItem("Posición", user.playerProfile?.posicionPrincipal ?: "Sin asignar")
                            }
                        }
                        
                        Spacer(Modifier.height(100.dp)) // Espacio para el bottom bar flotante
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = TextPrimary
            )
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
