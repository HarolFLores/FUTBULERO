package com.incanatoapps.retofut.ui.screens.referees

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun RefereePortalScreen(
    navController: NavHostController,
    viewModel: RefereePortalViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        viewModel.syncWithSession(sessionManager)
    }

    BackHandler {
        if (viewModel.selectedBottomTab != 0) {
            viewModel.selectedBottomTab = 0
        } else {
            navController.popBackStack()
        }
    }

    val screenTitle = when (viewModel.selectedBottomTab) {
        0 -> "Registro Incidencias Arbitro"
        1 -> "Actas Oficiales CODAR"
        2 -> "Bolsa de Honorarios y Pagos"
        3 -> "Carnet Digital Colegiado"
        else -> "Registro Incidencias Arbitro"
    }

    Scaffold(
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
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = {
                                if (viewModel.selectedBottomTab != 0) {
                                    viewModel.selectedBottomTab = 0
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Atrás",
                                tint = TextPrimary
                            )
                        }

                        Column {
                            Text(
                                text = screenTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "ChocoFulbo • Chiclayo Oficial",
                                fontSize = 10.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Botón derecho: Credencial CODAR rápida
                    Surface(
                        onClick = { viewModel.selectedBottomTab = 3 },
                        shape = RoundedCornerShape(10.dp),
                        color = DarkForestGreen,
                        modifier = Modifier.size(36.dp).testTag("referee_badge_header")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.Badge,
                                contentDescription = "Carnet CODAR",
                                tint = LimeNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            }
        },
        bottomBar = {
            RefereeBottomBar(
                selectedTab = viewModel.selectedBottomTab,
                onTabSelected = { viewModel.selectedBottomTab = it }
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (viewModel.selectedBottomTab) {
                0 -> RefereeAgendaTab(
                    viewModel = viewModel,
                    onOpenMatchSheetClick = { des ->
                        viewModel.activeDesignationForSheet = des
                        viewModel.showMatchSheetDialog = true
                    },
                    onQuickIncidentClick = { type ->
                        viewModel.activeIncidentType = type
                        viewModel.showIncidentDialog = true
                    },
                    onWithdrawClick = { viewModel.showWithdrawalDialog = true },
                    onSwitchRoleClick = {
                        viewModel.switchToPlayerMode(context) {
                            Toast.makeText(context, "¡Modo Jugador activado! Tu carnet CODAR se mantiene seguro.", Toast.LENGTH_LONG).show()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
                1 -> RefereeActasTab(
                    viewModel = viewModel,
                    onFillActClick = { act ->
                        viewModel.activeActForPdf = act
                        viewModel.showMatchSheetDialog = true
                    },
                    onViewPdfClick = { act ->
                        viewModel.activeActForPdf = act
                        viewModel.showPdfViewerDialog = true
                    }
                )
                2 -> RefereeHonorariosTab(
                    viewModel = viewModel,
                    onWithdrawClick = { viewModel.showWithdrawalDialog = true }
                )
                3 -> RefereeCodarTab(
                    viewModel = viewModel,
                    onVerifyQrClick = { viewModel.showQrVerificationDialog = true }
                )
            }

            // Diálogos modales
            if (viewModel.showIncidentDialog) {
                QuickIncidentDialog(
                    type = viewModel.activeIncidentType,
                    viewModel = viewModel,
                    onDismiss = { viewModel.showIncidentDialog = false }
                )
            }

            if (viewModel.showMatchSheetDialog) {
                DigitalMatchSheetDialog(
                    act = viewModel.activeActForPdf,
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.showMatchSheetDialog = false
                        viewModel.activeActForPdf = null
                    }
                )
            }

            if (viewModel.showPdfViewerDialog) {
                PdfViewerDialog(
                    act = viewModel.activeActForPdf,
                    onDismiss = {
                        viewModel.showPdfViewerDialog = false
                        viewModel.activeActForPdf = null
                    }
                )
            }

            if (viewModel.showWithdrawalDialog) {
                RefereeWithdrawalDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showWithdrawalDialog = false }
                )
            }

            if (viewModel.showQrVerificationDialog) {
                QrVerificationDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showQrVerificationDialog = false }
                )
            }
        }
    }
}

@Composable
fun RefereeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .testTag("referee_bottom_bar"),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RefereeBottomBarItem(
                title = "Agenda",
                icon = Icons.Outlined.CalendarMonth,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            RefereeBottomBarItem(
                title = "Actas",
                icon = Icons.Outlined.Assignment,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            RefereeBottomBarItem(
                title = "Honorarios",
                icon = Icons.Outlined.Payments,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            RefereeBottomBarItem(
                title = "CODAR",
                icon = Icons.Outlined.Badge,
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun RefereeBottomBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) ForestGreen else Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) ForestGreen else Color(0xFF6B7280)
        )
        if (isSelected) {
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(ForestGreen)
            )
        }
    }
}
