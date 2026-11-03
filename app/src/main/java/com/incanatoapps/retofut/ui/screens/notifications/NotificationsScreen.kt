package com.incanatoapps.retofut.ui.screens.notifications

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.R
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.screens.dashboard.FloatingPremiumBottomNav
import com.incanatoapps.retofut.ui.theme.*

private enum class NotificationFilter(val label: String) {
    TODAS("Todas (4)"),
    PARTIDOS("Partidos"),
    INVITACIONES("Invitaciones"),
    PAGOS("Pagos")
}

@Composable
fun NotificationsScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    BackHandler { navController.popBackStack() }

    var selectedFilter by remember { mutableStateOf(NotificationFilter.TODAS) }
    var allMarkedAsRead by remember { mutableStateOf(false) }

    // Estados para interacciones
    var inviteAccepted by remember { mutableStateOf(false) }
    var inviteRejected by remember { mutableStateOf(false) }

    val activeCount = if (allMarkedAsRead) 0 else 3

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                // Header principal: Botón Volver + Título + Marcar todo como leído
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827)
                    )

                    Spacer(Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            allMarkedAsRead = true
                            Toast.makeText(context, "Todas las notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show()
                        },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Marcar todo como leído",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }

                // Selector de pestañas / Filtros de categorías
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NotificationFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) Color(0xFF143826) else Color.White,
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF4B5563)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            }
        },
        bottomBar = {
            FloatingPremiumBottomNav(navController = navController)
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==========================================
            // SECCIÓN: HOY
            // ==========================================
            val showTodaySection = selectedFilter in listOf(
                NotificationFilter.TODAS,
                NotificationFilter.PARTIDOS,
                NotificationFilter.INVITACIONES
            )

            if (showTodaySection) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOY",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4B5563),
                            letterSpacing = 0.5.sp
                        )

                        if (activeCount > 0) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF143826))
                                    .padding(horizontal = 9.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4ADE80))
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "• $activeCount activas",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                        }
                    }
                }
            }

            // --- TARJETA 1: Tu partido empieza en 2 horas (FÚTBOL 7) ---
            if (selectedFilter in listOf(NotificationFilter.TODAS, NotificationFilter.PARTIDOS)) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("match_detail/1") }
                            .testTag("notification_match_reminder"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.2.dp, Color(0xFFA7F3D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Ícono Verde Neón con Balón
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF4ADE80)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SportsSoccer,
                                    contentDescription = null,
                                    tint = Color(0xFF0F3822),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Tag FÚTBOL 7 con punto verde
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(Color(0xFF143826))
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "FÚTBOL 7",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF86EFAC)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF4ADE80))
                                        )
                                    }

                                    Text(
                                        text = "Hace 15 min",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    text = "Tu partido empieza en 2 horas",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = "Pichanga Nocturna F7 en Complejo El Oasis • Cancha 2. No olvides llevar camiseta blanca y oscura.",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF374151)
                                )

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFF4B5563),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(
                                            text = "Chiclayo Oeste",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF374151)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                                    ) {
                                        Text(
                                            text = "20:30 hrs",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1F2937),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- TARJETA 2: Invitación de Equipo (FICHAJE) ---
            if (selectedFilter in listOf(NotificationFilter.TODAS, NotificationFilter.INVITACIONES)) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notification_team_invitation"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Avatar con insignia de escudo
                            Box(
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7))
                                        .border(1.dp, Color(0xFFFDE68A), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF92400E),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF143826))
                                        .border(1.2.dp, Color.White, CircleShape)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "FICHAJE",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF166534),
                                        letterSpacing = 0.5.sp
                                    )

                                    Text(
                                        text = "Hace 1 hora",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Invitación de Equipo",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = "Carlos Farfán te ha convocado para unirte al plantel oficial de Los Galácticos (Fútbol 7).",
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF4B5563)
                                )

                                Spacer(Modifier.height(12.dp))

                                // Botones de Aceptar o Rechazar
                                if (!inviteAccepted && !inviteRejected) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                inviteAccepted = true
                                                Toast.makeText(context, "¡Convocatoria aceptada! Ya estás en Los Galácticos", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF143826),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .height(42.dp)
                                                .testTag("accept_invitation_button"),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = "Aceptar convocatoria",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                inviteRejected = true
                                                Toast.makeText(context, "Convocatoria rechazada", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("reject_invitation_button"),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Close,
                                                contentDescription = null,
                                                tint = Color(0xFF4B5563),
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "Rechazar",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF4B5563)
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (inviteAccepted) Color(0xFFECFDF5) else Color(0xFFF3F4F6),
                                        border = BorderStroke(1.dp, if (inviteAccepted) Color(0xFFA7F3D0) else Color(0xFFE5E7EB)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (inviteAccepted) "✓ Convocatoria aceptada exitosamente" else "Convocatoria rechazada",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (inviteAccepted) Color(0xFF166534) else Color(0xFF6B7280),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- TARJETA 3: Gran victoria de Atlético Chiclayo (TORNEO CLAUSURA) ---
            if (selectedFilter in listOf(NotificationFilter.TODAS, NotificationFilter.PARTIDOS)) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notification_match_result"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Ícono Trofeo en caja verde suave
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0FDF4))
                                    .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TORNEO CLAUSURA",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF111827),
                                        letterSpacing = 0.5.sp
                                    )

                                    Text(
                                        text = "Hace 3 horas",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "¡Gran victoria de Atlético Chiclayo!",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(Modifier.height(8.dp))

                                // Marcador Estilizado 4 - 2
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF9FAFB),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Atlético Chiclayo",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827),
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.End
                                        )

                                        Spacer(Modifier.width(10.dp))

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text(
                                                text = "4 - 2",
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF111827),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(Modifier.width(10.dp))

                                        Text(
                                            text = "Los Caimanes FC",
                                            fontSize = 11.5.sp,
                                            color = Color(0xFF4B5563),
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        append("Vencieron 4 - 2 a Los Caimanes FC. ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF166534))) {
                                            append("Fuiste elegido MVP")
                                        }
                                        append(" del encuentro con 72% de votos.")
                                    },
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF374151)
                                )

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .clickable { navController.navigate(Screen.PostMatchReview.createRoute(1)) }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Ver acta y estadísticas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF143826)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF143826),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // SECCIÓN: AYER
            // ==========================================
            val showYesterdaySection = selectedFilter in listOf(
                NotificationFilter.TODAS,
                NotificationFilter.PAGOS
            )

            if (showYesterdaySection) {
                item {
                    Text(
                        text = "AYER",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4B5563),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                // --- TARJETA 4: Pago confirmado (CUOTA CANCHA) ---
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notification_payment_confirmed"),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Ícono Billetera en caja suave
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0FDF4))
                                    .border(1.dp, Color(0xFFDCFCE7), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "CUOTA CANCHA",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF111827),
                                        letterSpacing = 0.5.sp
                                    )

                                    Text(
                                        text = "Ayer 19:30",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Pago confirmado",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        append("Carlos M. confirmó el pago de su cuota de ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF111827))) {
                                            append("S/. 10.00")
                                        }
                                        append(" para la pichanga de hoy.")
                                    },
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF4B5563)
                                )

                                Spacer(Modifier.height(10.dp))

                                // Badge Verificado por Yape
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFECFDF5),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Verificado por Yape",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
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
