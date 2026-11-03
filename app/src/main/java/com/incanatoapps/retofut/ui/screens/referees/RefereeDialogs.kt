package com.incanatoapps.retofut.ui.screens.referees

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun QuickIncidentDialog(
    type: String,
    viewModel: RefereePortalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var minuteText by remember { mutableStateOf("15") }
    var playerNumberText by remember { mutableStateOf("9") }
    var teamName by remember { mutableStateOf("Atlético Chiclayo") }

    val typeLabel = when (type) {
        "GOL" -> "Registrar Gol / Asistencia"
        "AMARILLA" -> "Amonestación: Tarjeta Amarilla"
        "ROJA" -> "Expulsión: Tarjeta Roja Directa"
        "FIRMA" -> "Firma Digital de Acta"
        else -> "Registro de Incidencia"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = typeLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
                Text(
                    text = "Mesa de Control en Vivo • Torneo Apertura F7",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(Modifier.height(14.dp))

                if (type == "FIRMA") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Sello Digital CODAR:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                            Text("Juan Carlos Pérez Delgado • REG #4829", fontSize = 12.sp, color = ForestGreen)
                            Text("Certificado FPF Chiclayo válido para la tabla oficial.", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { minuteText = it },
                            label = { Text("Minuto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = playerNumberText,
                            onValueChange = { playerNumberText = it },
                            label = { Text("Dorsal #") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Equipo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val min = minuteText.toIntOrNull() ?: 15
                            viewModel.registerIncident(type, min, playerNumberText, teamName)
                            Toast.makeText(context, "Incidencia registrada en el sistema CODAR", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalMatchSheetDialog(
    act: RefereeMatchActUiModel?,
    viewModel: RefereePortalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var scoreTeam1 by remember { mutableStateOf("3") }
    var scoreTeam2 by remember { mutableStateOf("2") }
    var observations by remember { mutableStateOf("Partido disputado con total normalidad. Cancha sintética en óptimas condiciones.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Acta Digital CODAR", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                        Text(act?.actNumber ?: "Acta #2025-0819", fontSize = 11.sp, color = TextSecondary)
                    }

                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                        Text("EN VIVO", fontSize = 8.5.sp, fontWeight = FontWeight.Black, color = ForestGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(act?.teamsMatch ?: "Atlético Chiclayo vs Los Galácticos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                Spacer(Modifier.height(10.dp))

                // Marcador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = scoreTeam1,
                        onValueChange = { scoreTeam1 = it },
                        modifier = Modifier.width(55.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Text("  -  ", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    OutlinedTextField(
                        value = scoreTeam2,
                        onValueChange = { scoreTeam2 = it },
                        modifier = Modifier.width(55.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observaciones del Árbitro") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        val actId = act?.id ?: "act_1"
                        viewModel.signAndValidateAct(actId)
                        Toast.makeText(context, "Acta firmada con éxito. Marcador $scoreTeam1 - $scoreTeam2 guardado.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeNeon,
                        contentColor = DarkForestGreen
                    )
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Firmar y Validar Acta CODAR", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }

                Spacer(Modifier.height(6.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cerrar", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun PdfViewerDialog(
    act: RefereeMatchActUiModel?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("Acta Oficial Digitalizada", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                Text(act?.actNumber ?: "Acta CODAR #2025-0812", fontSize = 11.sp, color = TextSecondary)

                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9FAFB),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Encuentro:", fontSize = 10.5.sp, color = TextSecondary)
                        Text(act?.teamsMatch ?: "", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Text("Sello Notarial CODAR Chiclayo:", fontSize = 10.5.sp, color = TextSecondary)
                        Text("Documento Criptográfico FPF #9281-2025", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(Modifier.height(6.dp))
                        Text(act?.statsSummary ?: "", fontSize = 10.5.sp, color = TextSecondary)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cerrar Documento", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RefereeWithdrawalDialog(
    viewModel: RefereePortalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("100.00") }
    var phoneText by remember { mutableStateOf("974 812 390") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Desembolso de Honorarios", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                Text("Disponible: S/ ${"%.2f".format(viewModel.walletBalance)}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = ForestGreen)

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto a retirar (S/)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text("Número de Yape / Plin") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt <= 0 || amt > viewModel.walletBalance) {
                                Toast.makeText(context, "Monto inválido o excede tu saldo", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.withdrawFunds(amt, phoneText) {
                                Toast.makeText(context, "Retiro de S/ $amt enviado a tu Yape", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirmar Retiro", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QrVerificationDialog(
    viewModel: RefereePortalViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkForestGreen,
                    modifier = Modifier.size(150.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = LimeNeon, modifier = Modifier.size(100.dp))
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text("Validación QR Oficial CODAR", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                Text("DNI ${viewModel.dniNumber} • REG ${viewModel.regNumber}", fontSize = 11.5.sp, color = TextSecondary)
                Text("Árbitro Acreditado FPF Lambayeque 2025", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestGreen)

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text("Listo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
