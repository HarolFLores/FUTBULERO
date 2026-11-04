package com.incanatoapps.retofut.ui.screens.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun GuestRestrictionModal(
    onDismiss: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("guest_restriction_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFD1D5DB))
                )

                Spacer(Modifier.height(10.dp))

                // Badge y Botón Cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = LimeNeon,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "PASE DE AFICIONADO CHICLAYO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = DarkForestGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = TextSecondary, modifier = Modifier.size(15.dp))
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Título y Subtítulo
                Text(
                    text = "¡Crea tu Equipo y sé Capitán!",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "Para gestionar alineaciones y convocar a tus amigos de Chiclayo debes tener cuenta.",
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                // Hero Card: Carta de Jugador Preview
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = DarkForestGreen
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mini Carta FIFA
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LimeNeon,
                            modifier = Modifier
                                .width(56.dp)
                                .height(74.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("88", fontSize = 10.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                    Text("DEL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                                }
                                Icon(Icons.Outlined.SportsSoccer, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(22.dp))
                                Surface(
                                    color = DarkForestGreen,
                                    shape = RoundedCornerShape(3.dp)
                                ) {
                                    Text(
                                        text = "CHICLAYO",
                                        fontSize = 6.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = LimeNeon,
                                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✔", color = LimeNeon, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "TU CARTA DE JUGADOR",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LimeNeon
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Genera estadísticas OVR reales tras cada pichanga o torneo local con los votos de tus rivales.",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // 3 Puntos de Beneficios
                BenefitItemRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    title = "1) Reserva al instante con Yape / Plin",
                    subtitle = "Asegura tu cupo en la cancha sin lidiar con efectivo"
                )
                Spacer(Modifier.height(8.dp))
                BenefitItemRow(
                    icon = Icons.Outlined.Shield,
                    title = "2) Crea tu equipo y sé Capitán",
                    subtitle = "Inscribe tu escuadra a torneos nocturnos de fin de semana"
                )
                Spacer(Modifier.height(8.dp))
                BenefitItemRow(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "3) OVR & Carta FIFA Chiclayo",
                    subtitle = "Goles, asistencias y votos de MVP de tus rivales"
                )

                Spacer(Modifier.height(18.dp))

                // Botón verde CTA: Crear mi Cuenta en 3 pasos
                Button(
                    onClick = {
                        onDismiss()
                        onRegisterClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("create_account_from_guest_modal_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Crear mi Cuenta en 3 pasos",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }

                Spacer(Modifier.height(10.dp))

                // Link Iniciar Sesión
                Text(
                    text = "Ya tengo cuenta • Iniciar Sesión",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForestGreen,
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                            onLoginClick()
                        }
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun BenefitItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFDCFCE7),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(17.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary, lineHeight = 13.sp)
        }
    }
}
