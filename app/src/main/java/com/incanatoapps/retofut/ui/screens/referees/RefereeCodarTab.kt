package com.incanatoapps.retofut.ui.screens.referees

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun RefereeCodarTab(
    viewModel: RefereePortalViewModel,
    onVerifyQrClick: () -> Unit
) {
    val initials = remember(viewModel.refereeName) {
        val parts = viewModel.refereeName.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "JP"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. CARNET DIGITAL COLEGIADO (Dark Forest Green)
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("referee_credential_card"),
                shape = RoundedCornerShape(22.dp),
                color = DarkForestGreen,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header FPF + CODAR Lambayeque + Vigente 2025
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = LimeNeon,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("FPF", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("CODAR LAMBAYEQUE", fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text("Comisión Departamental de Árbitros", fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        Surface(
                            color = Color(0xFF0F3D28),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF166534))
                        ) {
                            Text(
                                text = "VIGENTE 2025",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = LimeNeon,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Perfil del Árbitro
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F4F6),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(initials, fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                            }

                            // Check verde verificado
                            Surface(
                                shape = CircleShape,
                                color = LimeNeon,
                                modifier = Modifier.size(16.dp).align(Alignment.BottomEnd)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("✔", fontSize = 9.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(viewModel.refereeCategory.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = LimeNeon)
                            Text(viewModel.refereeName, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("DNI ${viewModel.dniNumber} • REG ${viewModel.regNumber}", fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("Sede Chiclayo - Ferreñafe - Lambayeque", fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Cuadro QR de Validación
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkForestGreen,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.QrCode, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Validación QR CODAR FPF", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Escaneable por delegados de cancha", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Button(
                                onClick = onVerifyQrClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkForestGreen,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Verificar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. TRES CAJAS DE MÉTRICAS (Partidos, Rating, Ranking)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Caja 1: Partidos
                MetricBoxItem(
                    value = "${viewModel.matchesWhistled}",
                    label = "Partidos 2025",
                    modifier = Modifier.weight(1f)
                )

                // Caja 2: Capitanes Rating
                MetricBoxItem(
                    value = "${viewModel.rating} ★",
                    label = "Capitanes",
                    modifier = Modifier.weight(1f)
                )

                // Caja 3: Ranking
                MetricBoxItem(
                    value = "#${viewModel.rankingPosition}",
                    label = "Ranking Dep.",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ==========================================
        // 3. INFORMACIÓN FEDERATIVA CARD
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información Federativa",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForestGreen
                    )

                    Spacer(Modifier.height(12.dp))

                    FederativeInfoRow(label = "Colegio Arbitral:", value = "CODAR Lambayeque")
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

                    FederativeInfoRow(label = "Especialidad:", value = viewModel.specialty)
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

                    FederativeInfoRow(label = "Teléfono de Enlace:", value = viewModel.phoneContact)
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Capacitación VAR/Reglas:", fontSize = 11.5.sp, color = TextSecondary)
                        Text("Aprobado IFAB 2024/25", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBoxItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
            Spacer(Modifier.height(2.dp))
            Text(label, fontSize = 10.5.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun FederativeInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.5.sp, color = TextSecondary)
        Text(value, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
