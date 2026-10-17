package com.incanatoapps.retofut.ui.screens.teams

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(navController: NavController, viewModel: TeamViewModel = viewModel()) {
    var nombre by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("Pimentel, Chiclayo") }
    var descripcion by remember { mutableStateOf("") }
    var formato by remember { mutableStateOf("Fútbol 7 (F7)") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.fetchAuthToken()

    val colorOptions = listOf(
        listOf(DarkForestGreen, LimeNeon),
        listOf(Color(0xFF1E3A8A), Color(0xFF60A5FA)),
        listOf(Color(0xFF991B1B), Color(0xFFF87171)),
        listOf(Color(0xFF1F2937), Color(0xFF9CA3AF))
    )

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
                    "CREAR NUEVO EQUIPO",
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
            // Escudo & Logo Picker
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
                            .background(Brush.linearGradient(colorOptions[selectedColorIndex]))
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
                                if (nombre.isNotBlank()) nombre.take(2).uppercase() else "FC",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { photoLauncher.launch("image/*") }) {
                        Icon(Icons.Outlined.AddPhotoAlternate, null, modifier = Modifier.size(16.dp), tint = DarkForestGreen)
                        Spacer(Modifier.width(6.dp))
                        Text("Subir Escudo o Logo", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                    }
                }
            }

            // Selección de colores del club
            Column {
                Text(
                    "COLORES DISTINTIVOS",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colorOptions.forEachIndexed { index, colors ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(colors))
                                .border(
                                    if (selectedColorIndex == index) 3.dp else 1.dp,
                                    if (selectedColorIndex == index) DarkForestGreen else BorderGray,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }
            }

            // Nombre del Equipo
            Column {
                Text(
                    "NOMBRE DEL EQUIPO / CLUB",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Ej: Los Galácticos FC") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("team_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkForestGreen,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    )
                )
            }

            // Formato de juego
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

            // Ciudad / Ubicación
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
                    placeholder = { Text("Ej: Pimentel, Chiclayo") },
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

            // Descripción / Lema
            Column {
                Text(
                    "DESCRIPCIÓN O LEMA DEL CLUB",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = { Text("Ej: Equipo competitivo de amigos los fines de semana en Pimentel.") },
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

            // Botón Crear
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        Toast.makeText(context, "Por favor ingresa un nombre para el equipo", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "¡Equipo '$nombre' creado exitosamente!", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeNeon, contentColor = DarkForestGreen),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_create_team_button")
            ) {
                Text("Crear y Registrar Equipo", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}
