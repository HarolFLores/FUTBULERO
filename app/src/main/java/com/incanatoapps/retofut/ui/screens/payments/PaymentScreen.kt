package com.incanatoapps.retofut.ui.screens.payments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.ui.theme.*

data class PlayerPaymentStatus(
    val id: Int,
    val name: String,
    val roleDetail: String,
    val initials: String,
    val amount: String,
    val isPaid: Boolean,
    val paymentMethod: String? = null,
    val isCurrentUser: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    matchId: Int = 1
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var selectedPaymentMethod by remember { mutableStateOf("Yape") }
    var userPaymentConfirmed by remember { mutableStateOf(false) }

    val players = remember {
        mutableStateListOf(
            PlayerPaymentStatus(1, "Piero Mendoza", "Organizador", "PM", "S/. 10.00", isPaid = true, isCurrentUser = true),
            PlayerPaymentStatus(2, "Carlos Mendoza", "Delantero • Rayo Chiclayo", "CM", "S/. 10.00", isPaid = true, paymentMethod = "Yape"),
            PlayerPaymentStatus(3, "Roberto Silva", "Volante • Los Mochicas", "RS", "S/. 10.00", isPaid = true, paymentMethod = "Plin"),
            PlayerPaymentStatus(4, "Juan Pérez", "Defensa central", "JP", "S/. 10.00", isPaid = false),
            PlayerPaymentStatus(5, "David Paredes", "Arquero • Titular", "DP", "S/. 10.00", isPaid = false)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("payment_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextPrimary
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CHICLAYO CUP • F7",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Pago de Cancha y Cuotas",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Compartiendo datos de pago por WhatsApp...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = TextPrimary)
                    }

                    IconButton(onClick = {
                        Toast.makeText(context, "Soporte de pagos ChocoFulbo Chiclayo", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Outlined.HelpOutline, contentDescription = "Ayuda", tint = TextPrimary)
                    }
                }
                HorizontalDivider(color = BorderGray, thickness = 0.8.dp)
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            userPaymentConfirmed = true
                            Toast.makeText(context, "¡Comprobante subido! El tesorero verificará la transferencia en instantes.", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_payment_btn")
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = LimeNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (userPaymentConfirmed) "✓ Comprobante en Verificación" else "Confirmar mi pago (Subir comprobante)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Se validará la captura con la billetera digital seleccionada",
                        fontSize = 10.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. HERO MATCH & QUOTA CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkForestGreen)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF1B4D31),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, LimeNeon)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(LimeNeon))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "HOY 8:30 PM • F7",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LimeNeon
                                    )
                                }
                            }

                            Text(
                                text = "ID: #PCH-9482",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Pichanga Nocturna F7 - Complejo El Oasis",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = LimeNeon, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Av. Grau 1240, Chiclayo",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Fila de Montos (Total vs Tu Cuota)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "TOTAL DE ALQUILER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "S/. 120.00",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, LimeNeon),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "TU CUOTA FIJADA",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LimeNeon
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "S/. 10.00",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = LimeNeon
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Barra de Recaudación (75%)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recaudado: S/. 90.00 de S/. 120.00",
                                fontSize = 11.5.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = Color(0xFF1B4D31),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "9/12 pagaron",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LimeNeon,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LimeNeon)
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Faltan 3 cuotas para liquidar la cancha",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "75%",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LimeNeon
                            )
                        }
                    }
                }
            }

            // 2. MEDIOS DE PAGO AUTORIZADOS
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Payments, null, tint = ForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Medios de Pago Autorizados",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Sin comisión",
                            fontSize = 11.sp,
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Segmented Tabs: Yape, Plin, Efectivo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Yape", Color(0xFF7E22CE), "• Yape"),
                            Triple("Plin", Color(0xFF0EA5E9), "• Plin"),
                            Triple("Efectivo", Color(0xFF16A34A), "💵 Efectivo")
                        ).forEach { (method, dotColor, label) ->
                            val isSelected = selectedPaymentMethod == method
                            Surface(
                                onClick = { selectedPaymentMethod = method },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color.White else Color(0xFFF3F4F6),
                                border = BorderStroke(1.5.dp, if (isSelected) ForestGreen else BorderGray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (method != "Efectivo") {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(dotColor)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = if (method == "Efectivo") label else method,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // QR Card & Datos del Tesorero
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QR Visual Box
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF9FAFB),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.size(150.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    // Matriz de puntos simulada del QR
                                    Canvas(modifier = Modifier.size(120.dp)) {
                                        val step = size.width / 6
                                        for (i in 0..5) {
                                            for (j in 0..5) {
                                                if ((i + j) % 2 == 0 || (i == 0 && j == 0) || (i == 5 && j == 5)) {
                                                    drawRect(
                                                        color = Color(0xFF143823),
                                                        topLeft = Offset(i * step + 2, j * step + 2),
                                                        size = Size(step - 4, step - 4)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Logo Central (Yape Púrpura)
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF7E22CE))
                                            .border(2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (selectedPaymentMethod == "Plin") "P" else "Y",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "ESCANEAR CON ${selectedPaymentMethod.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(Modifier.height(14.dp))

                            // Badge Tesorero
                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.VerifiedUser,
                                        contentDescription = null,
                                        tint = ForestGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Tesorero Oficial del Encuentro",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text("Titular de cuenta:", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = "Piero Mendoza Torres",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )

                            Spacer(Modifier.height(10.dp))

                            // Número y Botón Copiar
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF9FAFB),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.PhoneAndroid,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "974 582 119",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Yape Phone", "974582119")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Número 974582119 copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkForestGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Outlined.ContentCopy, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Copiar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "*Indica en el mensaje de tu pago: \"Cuota F7 + Tu Apellido\".",
                                fontSize = 10.5.sp,
                                color = TextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // 3. TRANSPARENCIA DE CUOTA (LISTA DE JUGADORES)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Transparencia de Cuota",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(LimeNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "12",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForestGreen
                                )
                            }
                        }

                        Text(
                            text = "Actualizado hace 3 min",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        players.forEach { p ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, if (p.isCurrentUser) LimeNeon else BorderGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (p.isCurrentUser) DarkForestGreen else Color(0xFFF3F4F6)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = p.initials,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (p.isCurrentUser) LimeNeon else TextPrimary
                                        )
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = p.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = TextPrimary
                                            )
                                            if (p.isCurrentUser) {
                                                Spacer(Modifier.width(6.dp))
                                                Surface(
                                                    color = DarkForestGreen,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Tú",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = LimeNeon,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = p.roleDetail,
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    // Estado y Monto
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (p.isPaid) {
                                            Surface(
                                                color = when (p.paymentMethod) {
                                                    "Yape" -> Color(0xFFF3E8FF)
                                                    "Plin" -> Color(0xFFE0F2FE)
                                                    else -> Color(0xFFDCFCE7)
                                                },
                                                shape = RoundedCornerShape(50)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Outlined.Check,
                                                        contentDescription = null,
                                                        tint = when (p.paymentMethod) {
                                                            "Yape" -> Color(0xFF7E22CE)
                                                            "Plin" -> Color(0xFF0284C7)
                                                            else -> ForestGreen
                                                        },
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(Modifier.width(3.dp))
                                                    Text(
                                                        text = if (p.paymentMethod != null) "Pagado (${p.paymentMethod})" else "Pagado",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (p.paymentMethod) {
                                                            "Yape" -> Color(0xFF7E22CE)
                                                            "Plin" -> Color(0xFF0284C7)
                                                            else -> ForestGreen
                                                        }
                                                    )
                                                }
                                            }
                                        } else {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    color = Color(0xFFFEF3C7),
                                                    shape = RoundedCornerShape(50)
                                                ) {
                                                    Text(
                                                        text = "Pendiente",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFB45309),
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        Toast.makeText(context, "Recordatorio enviado por WhatsApp a ${p.name}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Outlined.NotificationsActive,
                                                        contentDescription = "Recordar",
                                                        tint = Color(0xFFB45309),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = p.amount,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Footer de jugadores confirmados
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "+ 7 jugadores restantes con estado confirmado ✓",
                                fontSize = 11.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. GARANTÍA DE PICHANGA SEGURA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Garantía de Pichanga Segura",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Las cuotas son administradas directamente por el tesorero asignado del equipo. Al subir tu captura, el saldo se descuenta automáticamente de la deuda grupal.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
