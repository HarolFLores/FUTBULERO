package com.incanatoapps.retofut.ui.screens.referees

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun RefereeHonorariosTab(
    viewModel: RefereePortalViewModel,
    onWithdrawClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HERO BALANCE CARD (Dark Forest Green)
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().testTag("referee_honorary_balance_card"),
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
                        Text(
                            text = "SALDO ACUMULADO EN BILLETERA",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF86EFAC),
                            letterSpacing = 0.5.sp
                        )

                        Surface(
                            color = LimeNeon,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Mayo 2025",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkForestGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "S/ ${"%.2f".format(viewModel.walletBalance)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Listo para desembolso",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LimeNeon,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Stats: En custodia y Retirado
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F3D28)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("En custodia ChocoFulbo:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("S/ ${"%.2f".format(viewModel.inCustodyToday)} (Hoy)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Retirado este mes:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("S/ ${"%.2f".format(viewModel.retiredThisMonth)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LimeNeon)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Botón Retirar
                    Button(
                        onClick = onWithdrawClick,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Outlined.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "RETIRAR VÍA YAPE / TRANSFERENCIA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. HISTORIAL DE HONORARIOS POR PARTIDO
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial de Honorarios por Partido",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )

                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "12 Encuentros",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        items(viewModel.honoraryHistory, key = { it.id }) { item ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.teamsMatch, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(Modifier.height(2.dp))
                        Text(item.dateAndVenue, fontSize = 10.5.sp, color = TextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "S/ ${"%.2f".format(item.amount)}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )
                        Spacer(Modifier.height(2.dp))

                        Surface(
                            color = if (item.isCustody) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (item.isCustody) "En Custodia" else "Liberado ✔",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (item.isCustody) Color(0xFFB45309) else ForestGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. TARIFA PERSONAL ÁRBITRO CODAR
        // ==========================================
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarifa Personal Árbitro CODAR",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen
                        )
                        Text(
                            text = "Establecida para torneos F7 nocturnos",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "S/ ${"%.2f".format(viewModel.personalFee)} / h",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
