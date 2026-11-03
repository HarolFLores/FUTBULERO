package com.incanatoapps.retofut.ui.screens.teams

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Int,
    val senderName: String,
    val senderRole: String? = null,
    val senderInitials: String,
    val text: String,
    val time: String,
    val isOutgoing: Boolean = false,
    val isAnnouncement: Boolean = false,
    val isAudioNote: Boolean = false,
    val audioDuration: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamChatScreen(
    navController: NavHostController,
    teamId: Int = 1
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    BackHandler { navController.popBackStack() }

    var inputText by remember { mutableStateOf("") }
    var isPlayingAudio by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = 1,
                senderName = "Carlos M.",
                senderRole = "DEFENSA",
                senderInitials = "CM",
                text = "Muchachos, ¿quién lleva el balón hoy para la pichanga? ⚽",
                time = "17:42",
                isOutgoing = false
            ),
            ChatMessage(
                id = 2,
                senderName = "Piero Mendoza",
                senderRole = "TÚ",
                senderInitials = "PM",
                text = "Yo lo llevo, nos vemos a las 8 en punto en Cancha 2 💪🔥",
                time = "17:45",
                isOutgoing = true
            ),
            ChatMessage(
                id = 3,
                senderName = "CONVOCATORIA OFICIAL",
                senderInitials = "CO",
                text = "Uniforme alterno: Camiseta Negra y medias verdes. Traer DNI para la planilla arbitral del torneo ChocoFulbo.",
                time = "17:46",
                isAnnouncement = true
            ),
            ChatMessage(
                id = 4,
                senderName = "Roberto \"Beto\" S.",
                senderRole = "CAPITÁN",
                senderInitials = "RS",
                text = "No se olviden de la cuota de la cancha (S/. 10 c/u por Yape). ¡Lleguen con canilleras! 🟨",
                time = "17:48",
                isOutgoing = false
            ),
            ChatMessage(
                id = 5,
                senderName = "Juan Pablo",
                senderRole = "DELANTERO",
                senderInitials = "JP",
                text = "Nota de voz",
                time = "17:50",
                isOutgoing = false,
                isAudioNote = true,
                audioDuration = "0:14"
            )
        )
    }

    val quickChips = listOf(
        "⚽ ¡Vamos Chiclayo!",
        "✅ Ya pagué por Yape",
        "⏱️ Voy 10m tarde",
        "🧤 Llevo guantes de repuesto"
    )

    Scaffold(
        containerColor = Color(0xFFF4F6F4),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkForestGreen)
                    .statusBarsPadding()
            ) {
                // Top App Bar Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("chat_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }

                    // Avatar con indicador en línea
                    Box(modifier = Modifier.size(42.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, LimeNeon, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AC",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(LimeNeon)
                                .border(1.5.dp, DarkForestGreen, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Atlético Chiclayo",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = "Oficial",
                                tint = LimeNeon,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(LimeNeon)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "12 futbolistas • En línea",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    IconButton(onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:974582119")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Llamando al Capitán Roberto Silva...", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            Icons.Outlined.Call,
                            contentDescription = "Llamar al capitán",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Plantel oficial: 12 jugadores registrados", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = "Opciones",
                            tint = Color.White
                        )
                    }
                }

                // Banner de Partido Fijado
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("match_detail/1") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkForestGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.SportsSoccer,
                                contentDescription = null,
                                tint = LimeNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "FIJADO",
                                        color = DarkForestGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Clásico vs Los Galácticos",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.5.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "Hoy 20:30 hrs • Complejo El Oasis (Cancha 2)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ver Partido",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
            ) {
                // Quick Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickChips.forEach { chip ->
                        Surface(
                            onClick = {
                                messages.add(
                                    ChatMessage(
                                        id = messages.size + 1,
                                        senderName = "Piero Mendoza",
                                        senderRole = "TÚ",
                                        senderInitials = "PM",
                                        text = chip,
                                        time = "17:52",
                                        isOutgoing = true
                                    )
                                )
                                coroutineScope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            },
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = BorderGray, thickness = 0.8.dp)

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Adjuntar (+)
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Adjuntar foto de la cancha, comprobante de pago o audio", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Adjuntar",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    // TextField
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Escribe un mensaje al plantel...",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderGray
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                Toast.makeText(context, "Cámara lista para capturar alineación", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    Icons.Outlined.PhotoCamera,
                                    contentDescription = "Cámara",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        singleLine = true
                    )

                    Spacer(Modifier.width(8.dp))

                    // Botón Enviar con texto/ícono blanco sobre fondo verde
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkForestGreen)
                            .clickable {
                                if (inputText.isNotBlank()) {
                                    messages.add(
                                        ChatMessage(
                                            id = messages.size + 1,
                                            senderName = "Piero Mendoza",
                                            senderRole = "TÚ",
                                            senderInitials = "PM",
                                            text = inputText.trim(),
                                            time = "17:53",
                                            isOutgoing = true
                                        )
                                    )
                                    inputText = ""
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            }
                            .testTag("chat_send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fecha separador
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "HOY, 28 DE FEBRERO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                when {
                    msg.isAnnouncement -> {
                        // Tarjeta de Convocatoria Oficial
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.2.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkForestGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Campaign,
                                            contentDescription = null,
                                            tint = LimeNeon,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "CONVOCATORIA OFICIAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.5.sp,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    msg.isOutgoing -> {
                        // Burbuja de Usuario (Saliente - Verde Lima)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                color = Color(0xFFA3E635),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                modifier = Modifier.widthIn(max = 290.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF0F3D24),
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = msg.time,
                                            fontSize = 10.sp,
                                            color = Color(0xFF0F3D24).copy(alpha = 0.7f)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Leído",
                                            tint = Color(0xFF0F3D24),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    msg.isAudioNote -> {
                        // Nota de Voz Entrante
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5E7EB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = msg.senderInitials,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${msg.senderName} (${msg.senderRole ?: ""})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.widthIn(min = 220.dp, max = 280.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(DarkForestGreen)
                                                .clickable {
                                                    isPlayingAudio = !isPlayingAudio
                                                    Toast.makeText(context, if (isPlayingAudio) "Reproduciendo audio del delantero..." else "Audio pausado", Toast.LENGTH_SHORT).show()
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                if (isPlayingAudio) Icons.Outlined.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Reproducir",
                                                tint = LimeNeon,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        // Formas de onda de audio simuladas
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            listOf(8, 16, 22, 12, 20, 14, 24, 18, 10, 16, 22, 14, 8).forEach { h ->
                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .height(h.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(if (isPlayingAudio) ForestGreen else Color(0xFFD1D5DB))
                                                )
                                            }
                                        }

                                        Spacer(Modifier.width(8.dp))
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = msg.audioDuration ?: "0:14",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = msg.time,
                                                fontSize = 9.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Mensaje Normal Entrante (Blanco)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DarkForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = msg.senderInitials,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LimeNeon
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                    if (msg.senderRole != null) {
                                        Spacer(Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFF3F4F6),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = msg.senderRole,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Black,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(3.dp))
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                                        Text(
                                            text = msg.text,
                                            fontSize = 13.5.sp,
                                            color = TextPrimary,
                                            lineHeight = 18.sp
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = msg.time,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
