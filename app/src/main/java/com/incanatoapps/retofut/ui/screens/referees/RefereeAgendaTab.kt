package com.incanatoapps.retofut.ui.screens.referees

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun RefereeAgendaTab(
    viewModel: RefereePortalViewModel,
    onOpenMatchSheetClick: (RefereeDesignationUiModel) -> Unit,
    onQuickIncidentClick: (String) -> Unit,
    onWithdrawClick: () -> Unit,
    onSwitchRoleClick: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HERO CARD (Dark Forest Green)
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("referee_hero_card"),
                shape = RoundedCornerShape(22.dp),
                color = DarkForestGreen,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF0F3D28),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF166534))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(LimeNeon)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "CODAR LAMBAYEQUE • FPF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F3D28),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Sports, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "CUERPO ARBITRAL OFICIAL",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF86EFAC),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = viewModel.refereeName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = viewModel.refereeLicense,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Switch Disponibilidad
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F3D28),
                        border = BorderStroke(1.dp, Color(0xFF166534)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (viewModel.isAvailableFridayNight) LimeNeon else Color.Gray)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (viewModel.isAvailableFridayNight) "Disponible viernes noche" else "No disponible hoy",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Visible para complejos de Chiclayo",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Switch(
                                checked = viewModel.isAvailableFridayNight,
                                onCheckedChange = {
                                    viewModel.isAvailableFridayNight = it
                                    Toast.makeText(
                                        context,
                                        if (it) "Disponibilidad activada en Chiclayo" else "Disponibilidad pausada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = ForestGreen
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Stats: Rating y Puntualidad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F3D28)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("★", fontSize = 14.sp, color = LimeNeon)
                                Spacer(Modifier.width(6.dp))
                                Column {
                                    Text("${viewModel.rating} ★", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("${viewModel.matchesWhistled} partidos pitados", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F3D28)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Timer, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Column {
                                    Text("${viewModel.punctualityPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("Puntualidad en campo", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. BOLSA DE HONORARIOS (MES) CARD
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("referee_wallet_card"),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Payments, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Bolsa de Honorarios (Mes)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "12 Encuentros",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "S/ ${"%.2f".format(viewModel.walletBalance)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )

                    Spacer(Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Tarifa promedio Chiclayo:", fontSize = 10.5.sp, color = TextSecondary)
                            }
                            Text("S/ ${"%.2f".format(viewModel.personalFee)} / partido", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onWithdrawClick,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Retirar a mi cuenta / Yape ➔",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForestGreen
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. MESA DE CONTROL EN VIVO (ACCESO RÁPIDO)
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("referee_live_control"),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.SportsSoccer, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Mesa de Control en Vivo",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen
                            )
                        }
                        Text(
                            text = "ACCESO RÁPIDO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = ForestGreen,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // 4 botones circulares de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickIncidentButtonItem(
                            icon = "⚽",
                            label = "Gol /\nAsist.",
                            bgTint = Color(0xFFDCFCE7),
                            onClick = { onQuickIncidentClick("GOL") }
                        )
                        QuickIncidentButtonItem(
                            icon = "🟨",
                            label = "Tarjeta\nAmarilla",
                            bgTint = Color(0xFFFEF3C7),
                            onClick = { onQuickIncidentClick("AMARILLA") }
                        )
                        QuickIncidentButtonItem(
                            icon = "🟥",
                            label = "Tarjeta\nRoja",
                            bgTint = Color(0xFFFEE2E2),
                            onClick = { onQuickIncidentClick("ROJA") }
                        )
                        QuickIncidentButtonItem(
                            icon = "✍️",
                            label = "Firma\nDigital",
                            bgTint = Color(0xFFE0F2FE),
                            onClick = { onQuickIncidentClick("FIRMA") }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Último suceso registrado
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF9FAFB)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Último suceso registrado:", fontSize = 10.5.sp, color = TextSecondary)
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = viewModel.lastIncidentRegistered,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkForestGreen,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. DESIGNACIONES DE HOY (2 Partidos)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Designaciones de Hoy",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = DarkForestGreen,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${viewModel.designations.size} Partidos",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = LimeNeon,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Viernes, Chiclayo",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        items(viewModel.designations, key = { it.id }) { des ->
            DesignationCard(
                designation = des,
                onOpenActClick = { onOpenMatchSheetClick(des) }
            )
        }

        // ==========================================
        // 5. BANNER: ¿QUIERES JUGAR UNA PICHANGA HOY?
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚽", fontSize = 18.sp)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "¿Quieres jugar una pichanga hoy?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkForestGreen
                            )
                            Text(
                                text = "Cambia a Modo Jugador para unirte a partidos con amigos en Chiclayo sin perder tu registro ni carnet CODAR.",
                                fontSize = 10.5.sp,
                                color = ForestGreen,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = onSwitchRoleClick,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Outlined.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("⇄  Cambiar a Modo Jugador", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickIncidentButtonItem(
    icon: String,
    label: String,
    bgTint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = bgTint,
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Composable
private fun DesignationCard(
    designation: RefereeDesignationUiModel,
    onOpenActClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(if (designation.isReadyToStart) 1.5.dp else 1.dp, if (designation.isReadyToStart) ForestGreen else Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Status chip + Format
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (designation.isReadyToStart) DarkForestGreen else Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${designation.statusChip} ${designation.timeLabel}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (designation.isReadyToStart) LimeNeon else TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = designation.formatBadge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF374151),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(designation.venueLabel, fontSize = 10.5.sp, color = TextSecondary)
            Text(designation.tournamentName, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            Spacer(Modifier.height(2.dp))
            Text(designation.teamsMatch, fontSize = 13.5.sp, fontWeight = FontWeight.Black, color = TextPrimary)

            Spacer(Modifier.height(8.dp))

            // Honorario y Custodia
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Honorario: ", fontSize = 10.5.sp, color = TextSecondary)
                    Text("S/ ${"%.2f".format(designation.fee)}", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                }

                Text(designation.custodyStatus, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
            }

            if (designation.isReadyToStart) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onOpenActClick,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Outlined.Assignment, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Abrir Acta Digital del Partido", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}
