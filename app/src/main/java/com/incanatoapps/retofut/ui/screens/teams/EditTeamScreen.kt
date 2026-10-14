package com.incanatoapps.retofut.ui.screens.teams

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTeamScreen(
    navController: NavHostController,
    teamId: Int,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }
    
    val state = viewModel.teamState
    val detailState = viewModel.teamDetailState
    val currentTeam = if (detailState is TeamState.Success) detailState.team else null

    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var formato by remember { mutableStateOf("futbol11") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(teamId) {
        if (token != null && (currentTeam == null || currentTeam.id != teamId)) {
            viewModel.loadTeamDetails(token, teamId)
        }
    }

    LaunchedEffect(currentTeam) {
        if (currentTeam != null && !isInitialized) {
            nombre = currentTeam.nombre
            ciudad = currentTeam.ciudad ?: ""
            descripcion = currentTeam.descripcion ?: ""
            formato = currentTeam.formatoPreferido ?: "futbol11"
            isInitialized = true
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(state) {
        if (state is TeamState.Success) {
            Toast.makeText(context, "¡Equipo actualizado con éxito!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.resetState()
        }
    }

    MainScaffold(
        navController = navController,
        title = "Editar Equipo"
    ) { padding ->
        if (detailState is TeamState.Loading && !isInitialized) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector de Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardWhite)
                        .border(2.dp, BrandGreen, RoundedCornerShape(24.dp))
                        .clickable { photoLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!currentTeam?.logo.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentTeam?.logo,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.AddPhotoAlternate, null, tint = BrandGreen)
                            Text("Logo", style = MaterialTheme.typography.labelSmall, color = BrandGreen)
                        }
                    }
                }

                Text(
                    "Información del Equipo",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.Groups, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        focusedLabelColor = BrandGreen
                    )
                )

                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        focusedLabelColor = BrandGreen
                    )
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Outlined.Description, null) },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        focusedLabelColor = BrandGreen
                    )
                )

                Text(
                    "Formato Preferido",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("futbol5", "futbol6", "futbol7", "futbol11").forEach { item ->
                        FilterChip(
                            selected = formato == item,
                            onClick = { formato = item },
                            label = { 
                                val displayText = when(item) {
                                    "futbol5" -> "Futbol 5"
                                    "futbol6" -> "Futbol 6"
                                    "futbol7" -> "Futbol 7"
                                    "futbol11" -> "Futbol 11"
                                    else -> item
                                }
                                Text(displayText)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandGreen,
                                selectedLabelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (state is TeamState.Error) {
                    Text(state.message, color = Color.Red, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        if (token != null) {
                            viewModel.updateTeam(context, token, teamId, nombre, selectedImageUri, ciudad, descripcion, formato)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    enabled = state !is TeamState.Loading && nombre.isNotBlank() && ciudad.isNotBlank()
                ) {
                    if (state is TeamState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TextPrimary)
                    } else {
                        Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}
