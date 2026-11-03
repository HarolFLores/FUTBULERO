package com.incanatoapps.retofut.ui.screens.courts

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun NewReservationDialog(
    viewModel: CourtManagementViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var selectedCourtId by remember { mutableIntStateOf(1) }
    var timeSlot by remember { mutableStateOf("Hoy 20:00 - 21:00") }
    var paymentNote by remember { mutableStateOf("Seña S/ 50.00 Yape") }
    var isConfirmed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Nueva Reserva Directa",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
                Text(
                    text = "Registrar cliente o equipo de manera presencial",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre del Responsable") },
                    placeholder = { Text("Ej. Juan Pérez") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    label = { Text("WhatsApp (Chiclayo)") },
                    placeholder = { Text("974 123 456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventName,
                    onValueChange = { eventName = it },
                    label = { Text("Pichanga o Duelo") },
                    placeholder = { Text("Ej. Los Galácticos vs La Victoria") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(8.dp))

                // Selector de Cancha
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Pair(1, "Cancha 1 (F7)"), Pair(2, "Cancha 2 (F6)")).forEach { (id, label) ->
                        val isSel = selectedCourtId == id
                        Surface(
                            onClick = { selectedCourtId = id },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) DarkForestGreen else Color(0xFFF3F4F6),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) LimeNeon else TextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = paymentNote,
                    onValueChange = { paymentNote = it },
                    label = { Text("Detalle de Pago / Seña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(14.dp))

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
                            if (clientName.isBlank()) {
                                Toast.makeText(context, "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addManualReservation(
                                clientName = clientName.trim(),
                                phone = clientPhone.ifBlank { "974-123-456" },
                                courtId = selectedCourtId,
                                courtFormat = if (selectedCourtId == 1) "Cancha 1 (F7)" else "Cancha 2 (F6)",
                                dateTimeLabel = timeSlot,
                                eventName = eventName.ifBlank { "Pichanga Amigos" },
                                paymentNote = paymentNote.ifBlank { "100% Efectivo" },
                                isConfirmed = true
                            )
                            Toast.makeText(context, "Reserva creada con éxito", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar Reserva", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AssignShiftDialog(
    shift: CourtShiftUiModel,
    viewModel: CourtManagementViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var teamName by remember { mutableStateOf("") }
    var isMaintenance by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Asignar / Bloquear Turno",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
                Text(
                    text = "${shift.courtName} • ${shift.timeRange} (${shift.day})",
                    fontSize = 11.5.sp,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                // Selector Acción
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { isMaintenance = false },
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isMaintenance) DarkForestGreen else Color(0xFFF3F4F6),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Asignar Equipo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isMaintenance) LimeNeon else TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        onClick = { isMaintenance = true },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMaintenance) Color(0xFF991B1B) else Color(0xFFF3F4F6),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Mantenimiento",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMaintenance) Color.White else TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (!isMaintenance) {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Nombre del Equipo o Pichanga") },
                        placeholder = { Text("Ej. Amigos de Santa Victoria") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                } else {
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "El turno quedará bloqueado en la grilla pública de ChocoFulbo para cepillado o reposición de caucho.",
                            fontSize = 11.sp,
                            color = Color(0xFF991B1B),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
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
                            val idx = viewModel.shifts.indexOfFirst { it.id == shift.id }
                            if (idx != -1) {
                                if (isMaintenance) {
                                    viewModel.shifts[idx] = shift.copy(
                                        status = ShiftStatus.BLOQUEADO,
                                        clientOrTeam = "Bloqueo por Mantenimiento",
                                        paymentDetail = "Cepillado / Luces"
                                    )
                                } else {
                                    viewModel.shifts[idx] = shift.copy(
                                        status = ShiftStatus.RESERVADO,
                                        clientOrTeam = teamName.ifBlank { "Reserva Manual Mostrador" },
                                        paymentDetail = "✔ Pago Mostrador"
                                    )
                                }
                            }
                            Toast.makeText(context, "Turno actualizado", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTournamentDialog(
    viewModel: CourtManagementViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var tourneyTitle by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("Fútbol 7") }
    var prizePool by remember { mutableStateOf("S/ 1,200.00") }
    var feeText by remember { mutableStateOf("100.00") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Nuevo Torneo en la Sede",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
                Text(
                    text = "Organizado en ${viewModel.complexName}",
                    fontSize = 11.5.sp,
                    color = ForestGreen
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = tourneyTitle,
                    onValueChange = { tourneyTitle = it },
                    label = { Text("Nombre de la Copa o Torneo") },
                    placeholder = { Text("Ej. Copa Relámpago Verano Chiclayo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = prizePool,
                    onValueChange = { prizePool = it },
                    label = { Text("Pozo de Premios") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it },
                    label = { Text("Inscripción por Equipo (S/)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(14.dp))

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
                            if (tourneyTitle.isBlank()) {
                                Toast.makeText(context, "Ingresa el nombre del torneo", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addTournament(
                                title = tourneyTitle.trim(),
                                format = "Formato $format",
                                prize = prizePool.ifBlank { "S/ 1,000.00" },
                                maxTeams = 8,
                                fee = feeText.toDoubleOrNull() ?: 100.0,
                                date = "Próximo Sábado"
                            )
                            Toast.makeText(context, "Torneo creado y publicado", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkForestGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Publicar Torneo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
