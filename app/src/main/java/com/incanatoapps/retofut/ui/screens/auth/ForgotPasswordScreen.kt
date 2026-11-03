package com.incanatoapps.retofut.ui.screens.auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

private enum class RecoveryMethod {
    WHATSAPP,
    EMAIL
}

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedMethod by remember { mutableStateOf(RecoveryMethod.WHATSAPP) }
    var phoneNumber by remember { mutableStateOf("979 845 210") }
    var dniOrEmail by remember { mutableStateOf("piero.mendoza@gmail.com") }

    var isSubmitting by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showCodeSentDialog by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf("482910") }

    // Reiniciar el estado de autenticación al entrar
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    val authState = viewModel.authState

    // Escuchar respuestas exitosas del backend si se usa recuperación por correo
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            isSubmitting = false
            showCodeSentDialog = true
        } else if (authState is AuthState.Error) {
            isSubmitting = false
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ==========================================
            // 1. CABECERA VERDE CON ARCOS Y DATOS (HEADER)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F3822),
                                Color(0xFF134228),
                                Color(0xFF184E30)
                            )
                        )
                    )
                    .drawBehind {
                        // Arcos decorativos concéntricos simulando radar/cancha
                        val centerOffset = Offset(size.width * 0.5f, size.height * 0.42f)
                        drawCircle(
                            color = Color(0x144ADE80),
                            radius = size.width * 0.35f,
                            center = centerOffset,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0x0F4ADE80),
                            radius = size.width * 0.55f,
                            center = centerOffset,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = Color(0x0A4ADE80),
                            radius = size.width * 0.78f,
                            center = centerOffset,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                    .statusBarsPadding()
                    .padding(bottom = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra superior: Botón Atrás + Badge Central + Botón Ayuda
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Volver
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x33000000))
                                .border(BorderStroke(1.dp, Color(0x26FFFFFF)), CircleShape)
                                .clickable { navController.popBackStack() }
                                .testTag("forgot_back_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Badge Central: • SEGURIDAD CHOCOFULBO
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0x40000000))
                                .border(BorderStroke(1.dp, Color(0x334ADE80)), RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4ADE80))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "SEGURIDAD CHOCOFULBO",
                                color = Color(0xFF86EFAC),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Botón Ayuda / Soporte
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x33000000))
                                .border(BorderStroke(1.dp, Color(0x26FFFFFF)), CircleShape)
                                .clickable { showHelpDialog = true }
                                .testTag("forgot_help_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.HelpOutline,
                                contentDescription = "Ayuda",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Ícono Central redondeado con candado y flecha circular
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x330A2917))
                            .border(BorderStroke(1.2.dp, Color(0x404ADE80)), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LockReset,
                            contentDescription = null,
                            tint = Color(0xFF86EFAC),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // Título
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    // Subtítulo
                    Text(
                        text = "No te quedes fuera de la pichanga. Ingresa tus datos\npara recuperar el acceso a tu cuenta.",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // ==========================================
            // 2. CUERPO DEL FORMULARIO (BODY)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Selector de método: WhatsApp (+51) vs Correo Electrónico
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Píldora WhatsApp
                    val isWhatsApp = selectedMethod == RecoveryMethod.WHATSAPP
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("method_whatsapp_tab"),
                        shape = RoundedCornerShape(11.dp),
                        color = if (isWhatsApp) Color(0xFF143826) else Color.Transparent,
                        onClick = { selectedMethod = RecoveryMethod.WHATSAPP }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = null,
                                tint = if (isWhatsApp) Color.White else Color(0xFF4B5563),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = "WhatsApp (+51)",
                                fontSize = 12.5.sp,
                                fontWeight = if (isWhatsApp) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isWhatsApp) Color.White else Color(0xFF4B5563)
                            )
                        }
                    }

                    // Píldora Correo Electrónico
                    val isEmail = selectedMethod == RecoveryMethod.EMAIL
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("method_email_tab"),
                        shape = RoundedCornerShape(11.dp),
                        color = if (isEmail) Color(0xFF143826) else Color.Transparent,
                        onClick = { selectedMethod = RecoveryMethod.EMAIL }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = if (isEmail) Color.White else Color(0xFF4B5563),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = "Correo Electrónico",
                                fontSize = 12.5.sp,
                                fontWeight = if (isEmail) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isEmail) Color.White else Color(0xFF4B5563)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Banner de información verde menta con escudo
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier
                                .size(22.dp)
                                .padding(top = 1.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (selectedMethod == RecoveryMethod.WHATSAPP) {
                                    "Recuperación inmediata por WhatsApp"
                                } else {
                                    "Recuperación por Correo Electrónico"
                                },
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF14532D)
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = if (selectedMethod == RecoveryMethod.WHATSAPP) {
                                    "Te enviaremos un código de 6 dígitos al número asociado a tus equipos y pichangas en Chiclayo."
                                } else {
                                    "Te enviaremos un código de 6 dígitos e instrucciones a tu bandeja de entrada o spam."
                                },
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Input 1: NÚMERO DE CELULAR REGISTRADO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedMethod == RecoveryMethod.WHATSAPP) "NÚMERO DE CELULAR REGISTRADO" else "CELULAR O CORREO REGISTRADO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1F2937),
                        letterSpacing = 0.3.sp
                    )
                    Text(
                        text = "recomendado en perú",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF16A34A)
                    )
                }

                Spacer(Modifier.height(7.dp))

                // Caja de texto del celular con bandera de Perú
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bandera Perú + prefijo +51
                        Text(text = "🇵🇪", fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "+51",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(Color(0xFFE5E7EB))
                        )
                        Spacer(Modifier.width(10.dp))

                        // Campo editable
                        BasicTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("phone_input_field"),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            ),
                            cursorBrush = SolidColor(DarkForestGreen),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            )
                        )

                        Icon(
                            imageVector = Icons.Outlined.Smartphone,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(5.dp))

                // Texto informativo inferior: Cuenta de Piero Mendoza (Atlético Chiclayo)
                Text(
                    text = buildAnnotatedString {
                        append("Cuenta de Piero Mendoza (")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF111827))) {
                            append("Atlético Chiclayo")
                        }
                        append(")")
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(start = 2.dp)
                )

                Spacer(Modifier.height(18.dp))

                // Input 2: DNI O CORREO DE CONFIRMACIÓN
                Text(
                    text = "DNI O CORREO DE CONFIRMACIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1F2937),
                    letterSpacing = 0.3.sp
                )

                Spacer(Modifier.height(7.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))

                        BasicTextField(
                            value = dniOrEmail,
                            onValueChange = { dniOrEmail = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dni_email_input_field"),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF111827)
                            ),
                            cursorBrush = SolidColor(DarkForestGreen),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            decorationBox = { innerTextField ->
                                if (dniOrEmail.isEmpty()) {
                                    Text(
                                        text = "piero.mendoza@gmail.com",
                                        fontSize = 13.5.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Tarjeta informativa: "¿Qué pasará después?"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF4B5563),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "¿Qué pasará después?",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = buildAnnotatedString {
                                append("1. Recibirás un código seguro de ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF111827))) {
                                    append("6 dígitos")
                                }
                                append(" al instante.")
                            },
                            fontSize = 11.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 16.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = "2. Ingresarás el código y crearás una nueva contraseña para ingresar de inmediato.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Botón principal: "Enviar código de recuperación"
                Button(
                    onClick = {
                        val trimmedDniOrEmail = dniOrEmail.trim()
                        val trimmedPhone = phoneNumber.trim()

                        if (trimmedPhone.isBlank() && trimmedDniOrEmail.isBlank()) {
                            Toast.makeText(context, "Por favor completa tu número o correo de confirmación", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true
                        // Generar código aleatorio de 6 dígitos para la demo inmediata
                        generatedCode = (100000..999999).random().toString()

                        if (selectedMethod == RecoveryMethod.EMAIL && trimmedDniOrEmail.contains("@")) {
                            viewModel.forgotPassword(trimmedDniOrEmail)
                        } else {
                            // Flujo WhatsApp o DNI: respuesta instantánea
                            isSubmitting = false
                            showCodeSentDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("send_recovery_code_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF143826), contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color(0xFF4ADE80),
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Enviar código de recuperación",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Send,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Enlace de soporte: "¿Cambiaste de número o perdiste acceso?"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Cambiaste de número o perdiste acceso?",
                        fontSize = 11.5.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .clickable { showSupportDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SupportAgent,
                            contentDescription = null,
                            tint = Color(0xFF143826),
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Contactar al soporte de torneos en Chiclayo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF143826),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Footer inferior: ¿Recordaste tu contraseña? Iniciar sesión ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Recordaste tu contraseña? ",
                        fontSize = 12.5.sp,
                        color = Color(0xFF4B5563)
                    )
                    Row(
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // DIÁLOGOS DE CONFIRMACIÓN Y SOPORTE
    // ==========================================

    // Diálogo 1: Código enviado con éxito (WhatsApp / Email)
    if (showCodeSentDialog) {
        Dialog(onDismissRequest = { showCodeSentDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "¡Código de 6 dígitos enviado!",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = if (selectedMethod == RecoveryMethod.WHATSAPP) {
                            "Hemos enviado tu código OTP a tu WhatsApp al número +51 $phoneNumber."
                        } else {
                            "Hemos enviado tu código a tu correo $dniOrEmail."
                        },
                        fontSize = 12.5.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // Tarjeta con el código generado para la simulación
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F4F6),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TU CÓDIGO DE RECUPERACIÓN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280),
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = generatedCode,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen,
                                letterSpacing = 6.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Válido por 10 minutos",
                                fontSize = 10.5.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Botón para continuar a restablecer la contraseña
                    Button(
                        onClick = {
                            showCodeSentDialog = false
                            navController.navigate(Screen.ResetPassword.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("go_to_reset_password_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF143826), contentColor = Color.White)
                    ) {
                        Text(
                            text = "Ingresar código y restablecer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Diálogo 2: Soporte de Torneos en Chiclayo
    if (showSupportDialog) {
        Dialog(onDismissRequest = { showSupportDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SupportAgent,
                            contentDescription = null,
                            tint = Color(0xFF3730A3),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Soporte ChocoFulbo Chiclayo",
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "¿Tienes inconvenientes con tu número o tu equipo de pichanga? Nuestro equipo de mesa de control en Chiclayo puede ayudarte a revalidar tu identidad.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF4B5563),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showSupportDialog = false
                            try {
                                val url = "https://wa.me/51979845210?text=Hola%20ChocoFulbo%20Chiclayo,%20necesito%20ayuda%20para%20recuperar%20el%20acceso%20a%20mi%20cuenta"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Contactar al +51 979 845 210", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir chat de WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(
                        onClick = { showSupportDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar", color = Color(0xFF6B7280))
                    }
                }
            }
        }
    }

    // Diálogo 3: Ayuda general de seguridad
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "Seguridad ChocoFulbo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Por seguridad de tus equipos, pichangas y apuestas, validamos que el número o correo coincida con los datos registrados en la liga de Chiclayo. El código OTP de 6 dígitos caduca a los 10 minutos.",
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = DarkForestGreen)
                }
            }
        )
    }
}
