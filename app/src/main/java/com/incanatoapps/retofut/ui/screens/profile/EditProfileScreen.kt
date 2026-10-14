package com.incanatoapps.retofut.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.components.MainScaffold
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = remember { sessionManager.fetchAuthToken() }

    val profileState = viewModel.profileState
    val currentUser = if (profileState is ProfileState.Success) profileState.user else null

    // Estados de los inputs
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var posicion by remember { mutableStateOf("POR") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    // Cargar perfil al iniciar si no está cargado en el ViewModel actual
    LaunchedEffect(Unit) {
        if (token != null && profileState !is ProfileState.Success) {
            viewModel.loadProfile(token)
        }
    }

    // Sincronizar datos del usuario con los inputs una vez que el perfil cargue
    LaunchedEffect(currentUser) {
        if (currentUser != null && !isInitialized) {
            nombre = currentUser.nombre
            telefono = currentUser.telefono ?: ""
            posicion = currentUser.playerProfile?.posicionPrincipal ?: "POR"
            isInitialized = true
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val updateState = viewModel.updateState

    LaunchedEffect(updateState) {
        if (updateState is UpdateState.Success) {
            viewModel.resetUpdateState()
            navController.popBackStack()
        }
    }

    MainScaffold(
        navController = navController,
        title = "Editar Perfil"
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (profileState is ProfileState.Loading && !isInitialized) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandGreen
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (updateState is UpdateState.Error) {
                        Text(updateState.message, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    // Selector de Imagen
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(CardWhite)
                            .border(2.dp, BrandGreen, CircleShape)
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
                        } else if (!currentUser?.foto.isNullOrEmpty()) {
                            AsyncImage(
                                model = currentUser?.foto,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Outlined.AddAPhoto, null, modifier = Modifier.size(40.dp), tint = BrandGreen)
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    Text("Cambiar Foto", style = MaterialTheme.typography.labelMedium, color = BrandGreen)

                    Spacer(Modifier.height(32.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = CardWhite,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Información Básica", fontWeight = FontWeight.Bold, color = BrandGreenDeep)
                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre Completo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Outlined.Person, null) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = telefono,
                                onValueChange = { telefono = it },
                                label = { Text("Teléfono") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Outlined.Phone, null) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandGreen,
                                    focusedLabelColor = BrandGreen
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = CardWhite,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Configuración de Jugador", fontWeight = FontWeight.Bold, color = BrandGreenDeep)
                            Spacer(Modifier.height(16.dp))

                            Text("Posición Principal", style = MaterialTheme.typography.labelMedium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("POR", "DEF", "MED", "DEL").forEach { pos ->
                                    FilterChip(
                                        selected = posicion == pos,
                                        onClick = { posicion = pos },
                                        label = { Text(pos) },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrandGreen,
                                            selectedLabelColor = TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    Button(
                        onClick = { 
                            if (token != null) {
                                viewModel.updateProfile(context, token, nombre, telefono, posicion, selectedImageUri)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = updateState !is UpdateState.Loading && token != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        if (updateState is UpdateState.Loading) {
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
}
