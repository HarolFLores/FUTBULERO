package com.incanatoapps.retofut.ui.screens.courts

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtSettingsTab(
    viewModel: CourtManagementViewModel
) {
    val context = LocalContext.current

    var dayPriceText by remember { mutableStateOf(viewModel.priceDayF7.toInt().toString()) }
    var nightPriceText by remember { mutableStateOf(viewModel.priceNightF7.toInt().toString()) }
    var hoursText by remember { mutableStateOf(viewModel.operatingHours) }
    var yapePhoneText by remember { mutableStateOf(viewModel.yapePhone) }
    var plinPhoneText by remember { mutableStateOf(viewModel.plinPhone) }
    var titularText by remember { mutableStateOf(viewModel.paymentTitular) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. HEADER TITLE
        // ==========================================
        item {
            Column {
                Text(
                    text = "Configuración de Sede",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = DarkForestGreen
                )
                Text(
                    text = "Parámetros operativos de ${viewModel.complexName}",
                    fontSize = 11.5.sp,
                    color = TextSecondary
                )
            }
        }

        // ==========================================
        // 2. CARD 1: PRECIOS POR HORA
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Precios por Hora", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Día
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Día (Hasta 18:00):", fontSize = 10.5.sp, color = TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("S/ ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkForestGreen)
                                    BasicAlertDialogTextField(
                                        value = dayPriceText,
                                        onValueChange = {
                                            dayPriceText = it
                                            it.toDoubleOrNull()?.let { p -> viewModel.priceDayF7 = p }
                                        },
                                        suffix = "/ h"
                                    )
                                }
                            }
                        }

                        // Noche LED
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9FAFB),
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Noche con Luz LED:", fontSize = 10.5.sp, color = TextSecondary)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("S/ ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                    BasicAlertDialogTextField(
                                        value = nightPriceText,
                                        onValueChange = {
                                            nightPriceText = it
                                            it.toDoubleOrNull()?.let { p -> viewModel.priceNightF7 = p }
                                        },
                                        suffix = "/ h"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. CARD 2: HORARIOS DE ATENCIÓN
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Horarios de Atención", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                        }

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Abierto Lun - Dom",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Franja Diaria:", fontSize = 11.5.sp, color = TextSecondary)
                            Text(hoursText, fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. CARD 3: MEDIOS DE PAGO VINCULADOS
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Medios de Pago Vinculados", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Yape
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFAF5FF),
                        border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF7C3AED),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("Y", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Yape Negocios: $yapePhoneText", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Titular: $titularText", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Text("Activo ✔", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Plin
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFEFF),
                        border = BorderStroke(1.dp, Color(0xFFCFFAFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0891B2),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("P", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Plin Interbank: $plinPhoneText", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Titular: $titularText", fontSize = 10.sp, color = TextSecondary)
                                }
                            }

                            Text("Activo ✔", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 5. CARD 4: MANTENIMIENTO DE CÉSPED SINTÉTICO
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Grass, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Mantenimiento de Césped Sintético", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Fibra Monofilamento 50mm con caucho SBR criogénico. Último cepillado: hace 3 días.",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Solicitud enviada al proveedor de caucho sintético en Chiclayo", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Text("Solicitar Reabastecimiento de Caucho", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }

        // ==========================================
        // 6. CARD 5: UBICACIÓN Y MAPS REGISTRADOS
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = DarkForestGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ubicación Satelital Registrada", fontSize = 14.sp, fontWeight = FontWeight.Black, color = DarkForestGreen)
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(viewModel.complexAddress, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Distrito: ${viewModel.complexDistrict}", fontSize = 11.sp, color = TextSecondary)
                    Text("RUC: ${viewModel.complexRuc}", fontSize = 11.sp, color = TextSecondary)

                    if (viewModel.mapsUrl.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.mapsUrl))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Abriendo Maps: ${viewModel.mapsUrl}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Icon(Icons.Outlined.Map, contentDescription = null, modifier = Modifier.size(14.dp), tint = ForestGreen)
                            Spacer(Modifier.width(6.dp))
                            Text("Ver Coordenadas en Google Maps", fontSize = 10.5.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 7. BOTÓN GUARDAR CONFIGURACIÓN
        // ==========================================
        item {
            Button(
                onClick = {
                    viewModel.operatingHours = hoursText
                    viewModel.yapePhone = yapePhoneText
                    viewModel.plinPhone = plinPhoneText
                    viewModel.paymentTitular = titularText
                    viewModel.saveSettings(context) {
                        Toast.makeText(context, "Configuración guardada y sincronizada con éxito", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_settings_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkForestGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Guardar Configuración",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun BasicAlertDialogTextField(
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = DarkForestGreen
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(42.dp)
        )
        Text(suffix, fontSize = 11.sp, color = TextSecondary)
    }
}
