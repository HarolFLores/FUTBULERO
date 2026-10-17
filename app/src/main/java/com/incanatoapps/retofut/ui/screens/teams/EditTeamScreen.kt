package com.incanatoapps.retofut.ui.screens.teams

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun EditTeamScreen(
    navController: NavHostController,
    teamId: Int,
    viewModel: TeamViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()

    BackHandler { navController.popBackStack() }

    var nombre by remember { mutableStateOf(if (teamId == 1) "Los Galácticos FC" else "Rayo Chiclayano") }
    var ciudad by remember { mutableStateOf(if (teamId == 1) "Pimentel, Chiclayo" else "La Victoria, Chiclayo") }
    var descripcion by remember { mutableStateOf("Club oficial de pichangas y torneos clausura 2026.") }
    var formato by remember { mutableStateOf("Fútbol 7 (F7)") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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
                    "EDITAR EQUIPO",
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
            // Escudo
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
                            .size(80.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(DarkForestGreen)
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
                        Icon(Icons.Outlined.AddPhotoAlternate, null, modifier = Modifier.size(16.dp), tint = DarkForestGreen)
                        Spacer(Modifier.width(6.dp))
                        Text("Cambiar Escudo del Equipo", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                    }
                }
            }

            // Nombre
            Column {
                Text(
                    "NOMBRE DEL EQUIPO",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_team_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            // Formato
            Column {
                Text(
                    "FORMATO PREFERIDO",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Fútbol 7 (F7)", "Fútbol 6 (F6)", "Fútbol 11 (F11)").forEach { fmt ->
                        val isSelected = formato == fmt
                        Surface(
                            onClick = { formato = fmt },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DarkForestGreen else CardWhite,
                            border = BorderStroke(1.dp, if (isSelected) DarkForestGreen else BorderGray),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    fmt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) LimeNeon else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Ciudad
            Column {
                Text(
                    "CIUDAD / ZONA DE JUEGO",
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

            // Descripción
            Column {
                Text(
                    "DESCRIPCIÓN DEL EQUIPO",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            // Botón Guardar
            Button(
                onClick = {
                    Toast.makeText(context, "¡Cambios guardados en '$nombre'!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_edit_team_button")
            ) {
                Text("Guardar Cambios del Equipo", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}
