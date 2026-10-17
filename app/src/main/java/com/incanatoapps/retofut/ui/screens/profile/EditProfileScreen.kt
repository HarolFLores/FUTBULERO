package com.incanatoapps.retofut.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()

    BackHandler { navController.popBackStack() }

    var nombre by remember { mutableStateOf(sessionManager.fetchUserName() ?: "Piero Flores") }
    var telefono by remember { mutableStateOf("974829102") }
    var posicion by remember { mutableStateOf(sessionManager.fetchPreferredPosition()) }
    var dorsal by remember { mutableStateOf("10") }
    var ciudad by remember { mutableStateOf("Pimentel, Chiclayo") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val positionOptions = listOf("Delantero (DC)", "Mediocampista (MC)", "Defensa (DFC)", "Portero (GK)")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardWhite)
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "EDITAR PERFIL",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Avatar picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CardWhite)
                    .border(1.dp, BorderGray, RoundedCornerShape(22.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen)
                            .border(3.dp, LimeNeon, CircleShape)
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                nombre.take(2).uppercase(),
                                color = LimeNeon,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { photoLauncher.launch("image/*") }) {
                        Icon(Icons.Outlined.CameraAlt, null, modifier = Modifier.size(16.dp), tint = DarkForestGreen)
                        Spacer(Modifier.width(6.dp))
                        Text("Cambiar Foto de Perfil", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                    }
                }
            }

            // Nombre completo
            Column {
                Text(
                    "NOMBRE COMPLETO",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            // Posición preferida
            Column {
                Text(
                    "POSICIÓN PRINCIPAL EN CANCHA",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    positionOptions.forEach { pos ->
                        val isSelected = posicion.contains(pos.substringBefore(" ")) || posicion == pos
                        Surface(
                            onClick = { posicion = pos.substringBefore(" ") },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDF4) else CardWhite,
                            border = BorderStroke(1.5.dp, if (isSelected) DarkForestGreen else BorderGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { posicion = pos.substringBefore(" ") },
                                    colors = RadioButtonDefaults.colors(selectedColor = DarkForestGreen)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(pos, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            // Dorsal preferido & Teléfono
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "DORSAL (#)",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = dorsal,
                        onValueChange = { dorsal = it },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite
                        )
                    )
                }

                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        "TELÉFONO DE CONTACTO",
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite
                        )
                    )
                }
            }

            // Ubicación / Zona
            Column {
                Text(
                    "ZONA O CIUDAD PREDILECTA",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Place, null, tint = DarkForestGreen) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // Botón Guardar Cambios
            Button(
                onClick = {
                    sessionManager.saveUserName(nombre)
                    sessionManager.savePreferredPosition(posicion)
                    Toast.makeText(context, "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_profile_button")
            ) {
                Text("Guardar Cambios", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}
