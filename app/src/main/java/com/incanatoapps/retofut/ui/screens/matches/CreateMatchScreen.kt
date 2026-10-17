package com.incanatoapps.retofut.ui.screens.matches

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.ChocoFulboBottomBar
import com.incanatoapps.retofut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(navController: NavHostController) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var matchTitle by remember { mutableStateOf("Pichanga Nocturna Los Galácticos") }
    var selectedFormat by remember { mutableStateOf("Fútbol 7") }
    var matchDate by remember { mutableStateOf("Vie 28 Feb 2026") }
    var matchTime by remember { mutableStateOf("20:00 hrs") }
    var pricePerPlayer by remember { mutableStateOf(12) }
    var isPublic by remember { mutableStateOf(true) }
    var currentStep by remember { mutableStateOf(1) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val formatPlayers = when (selectedFormat) {
        "Fútbol 5" -> 10
        "Fútbol 7" -> 14
        "Fútbol 8" -> 16
        else -> 22
    }

    val totalCost = pricePerPlayer * formatPlayers

    Scaffold(
        containerColor = Color.White,
        bottomBar = { ChocoFulboBottomBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- TOP BAR ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextPrimary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Crear Partido",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "PASO $currentStep DE 3: DATOS DE CANCHA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            letterSpacing = 0.8.sp
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Define la sede y costo por jugador para tu pichanga", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = "Ayuda", tint = TextPrimary)
                    }
                }

                // Barra de progreso (Paso 1 de 3)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(BorderGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.33f)
                            .height(3.dp)
                            .background(LimeNeon)
                    )
                }
            }

            // --- CONTENIDO FORMULARIO ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner Hero Grassroots Chiclayo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color(0xFF143823),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, LimeNeon.copy(alpha = 0.7f))
                            ) {
                                Text(
                                    text = "GRASSROOTS CHICLAYO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LimeNeon,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Organiza tu Pichanga",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Define la sede, horario y divide el pozo sin dramas.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B4D31)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.SportsSoccer,
                                contentDescription = null,
                                tint = LimeNeon,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }

                // Título del Partido
                Column {
                    Text("Título del Partido", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = matchTitle,
                        onValueChange = { matchTitle = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("match_title_input"),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Icon(Icons.Outlined.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                // Tipo de Juego
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tipo de Juego", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(
                            "$formatPlayers JUGADORES",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreen
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Fútbol 5", "Fútbol 7", "Fútbol 8", "Fútbol 11").forEach { format ->
                            val isSelected = selectedFormat == format
                            Surface(
                                onClick = { selectedFormat = format },
                                color = if (isSelected) LimeNeon else Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = format,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) DarkForestGreen else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Fecha y Hora de Inicio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fecha", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            onClick = { Toast.makeText(context, "Fecha: Vie 28 Feb 2026 seleccionada", Toast.LENGTH_SHORT).show() },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.CalendarToday, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(matchDate, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora Inicio", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            onClick = { Toast.makeText(context, "Horario: 20:00 hrs seleccionado", Toast.LENGTH_SHORT).show() },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderGray),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Schedule, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(matchTime, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                        }
                    }
                }

                // Sede y Cancha
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sede y Cancha", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(
                            "CAMBIAR CANCHA",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Selección de canchas disponible en Chiclayo", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column {
                            // Banner visual simulación cancha sintética con iluminación LED
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0xFF0F3D24), Color(0xFF14532D))
                                        )
                                    )
                            ) {
                                // Etiquetas sobre la imagen
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Outlined.LocationOn, null, tint = Color.White, modifier = Modifier.size(11.dp))
                                            Spacer(Modifier.width(3.dp))
                                            Text("Chiclayo Central", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Surface(
                                        color = DarkForestGreen,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, LimeNeon.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            "Sintético Pro 50mm",
                                            color = LimeNeon,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            // Info de la sede
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Complejo Deportivo El Oasis", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                    Spacer(Modifier.height(2.dp))
                                    Text("⚽ Cancha 2 (Techada, Iluminación LED)", fontSize = 12.sp, color = TextSecondary)
                                    Text("Av. Los Tréboles 450, Urb. Santa Victoria", fontSize = 11.5.sp, color = TextSecondary)
                                }

                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Outlined.Verified, null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                                        Spacer(Modifier.width(3.dp))
                                        Text("Oficial", color = ForestGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Costo por Persona con Stepper
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Costo por Persona", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Text("Dividido equitativamente entre $formatPlayers cupos", fontSize = 11.5.sp, color = TextSecondary)
                            }

                            // Stepper de precio
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .clickable { if (pricePerPlayer > 5) pricePerPlayer-- },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("–", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }

                                    Text(
                                        text = "S/. ${pricePerPlayer}.00",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkForestGreen)
                                            .clickable { pricePerPlayer++ },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // Total Alquiler Cancha
                        Surface(
                            color = Color(0xFFF9FAFB),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Payments, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Alquiler Cancha (2 hrs):", fontSize = 12.5.sp, color = TextSecondary)
                                }
                                Text("Total S/. ${totalCost}.00", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }

                // Tipo de Acceso (Público vs Privado)
                Column {
                    Text("Tipo de Acceso", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Público
                        Card(
                            onClick = { isPublic = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPublic) Color(0xFFF0FDF4) else Color.White
                            ),
                            border = BorderStroke(1.5.dp, if (isPublic) ForestGreen else BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Outlined.Public, null, tint = if (isPublic) ForestGreen else TextSecondary)
                                    if (isPublic) {
                                        Icon(Icons.Outlined.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Público", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Spacer(Modifier.height(2.dp))
                                Text("Cualquiera en ChocoFulbo puede unirse al equipo.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        // Privado
                        Card(
                            onClick = { isPublic = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!isPublic) Color(0xFFF0FDF4) else Color.White
                            ),
                            border = BorderStroke(1.5.dp, if (!isPublic) ForestGreen else BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Outlined.Lock, null, tint = if (!isPublic) ForestGreen else TextSecondary)
                                    if (!isPublic) {
                                        Icon(Icons.Outlined.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Privado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Spacer(Modifier.height(2.dp))
                                Text("Solo con enlace de invitación o código PIN.", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Botón Siguiente: Armar Planteles
                Button(
                    onClick = { showSuccessDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_create_match"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Siguiente: Armar Planteles", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("¡Partido Creado con Éxito!", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tu pichanga '$matchTitle' ya está publicada en ChocoFulbo.", fontSize = 13.5.sp)
                    Text("• Formato: $selectedFormat ($formatPlayers jugadores)", fontSize = 13.sp)
                    Text("• Horario: $matchDate a las $matchTime", fontSize = 13.sp)
                    Text("• Sede: Complejo El Oasis - Cancha 2", fontSize = 13.sp)
                    Text("• Cuota: S/. $pricePerPlayer.00 por cupo", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("match_detail/1")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkForestGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver Partido Publicado")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text("Volver al Inicio")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
