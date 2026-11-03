package com.incanatoapps.retofut.ui.screens.courts

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun CourtManagementScreen(
    navController: NavHostController,
    viewModel: CourtManagementViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Sincronizar datos iniciales desde SessionManager
    LaunchedEffect(Unit) {
        viewModel.syncWithSession(sessionManager)
    }

    // Manejo de botón atrás
    BackHandler {
        if (viewModel.selectedBottomTab != 0) {
            viewModel.selectedBottomTab = 0
        } else {
            navController.popBackStack()
        }
    }

    val currentTabTitle = when (viewModel.selectedBottomTab) {
        0 -> "Mi Sede Turnos"
        1 -> "Gestión de Reservas"
        2 -> "Torneos y Ligas"
        3 -> "Ajustes del Complejo"
        else -> "Mi Sede Turnos"
    }

    // Iniciales para el avatar del complejo (ej. "EO" para El Oasis)
    val complexInitials = remember(viewModel.complexName) {
        val words = viewModel.complexName.trim().split(" ").filter { it.isNotBlank() }
        when {
            words.size >= 2 -> "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            words.isNotEmpty() -> words[0].take(2).uppercase()
            else -> "EO"
        }
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
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Icono circular de balón verde
                        Surface(
                            shape = CircleShape,
                            color = DarkForestGreen,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.SportsSoccer,
                                    contentDescription = null,
                                    tint = LimeNeon,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "DUEÑO CANCHA",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = ForestGreen,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = currentTabTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                    }

                    // Acciones derechas: Notificaciones y Perfil
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Campana con indicador verde
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clickable {
                                    navController.navigate(Screen.Notifications.route)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreen)
                                    .align(Alignment.TopEnd)
                            )
                        }

                        // Avatar iniciales EO
                        Surface(
                            onClick = {
                                navController.navigate(Screen.Profile.route)
                            },
                            shape = CircleShape,
                            color = DarkForestGreen,
                            modifier = Modifier.size(32.dp).testTag("owner_avatar")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = complexInitials,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LimeNeon
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
            }
        },
        bottomBar = {
            CourtOwnerBottomBar(
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
                0 -> CourtShiftsTab(
                    viewModel = viewModel,
                    onCreateTournamentClick = { viewModel.showCreateTournamentDialog = true },
                    onReviewDepositsClick = { viewModel.selectedBottomTab = 1 },
                    onAssignShiftClick = { shift ->
                        viewModel.selectedShiftForAssign = shift
                        viewModel.showAssignShiftDialog = true
                    }
                )
                1 -> CourtReservationsTab(
                    viewModel = viewModel,
                    onNewReservationClick = { viewModel.showNewReservationDialog = true }
                )
                2 -> CourtTournamentsTab(
                    navController = navController,
                    viewModel = viewModel,
                    onCreateTournamentClick = { viewModel.showCreateTournamentDialog = true }
                )
                3 -> CourtSettingsTab(
                    viewModel = viewModel
                )
            }

            // Diálogos modales
            if (viewModel.showNewReservationDialog) {
                NewReservationDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showNewReservationDialog = false }
                )
            }

            val shiftToAssign = viewModel.selectedShiftForAssign
            if (viewModel.showAssignShiftDialog && shiftToAssign != null) {
                AssignShiftDialog(
                    shift = shiftToAssign,
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.showAssignShiftDialog = false
                        viewModel.selectedShiftForAssign = null
                    }
                )
            }

            if (viewModel.showCreateTournamentDialog) {
                CreateTournamentDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showCreateTournamentDialog = false }
                )
            }
        }
    }
}

@Composable
fun CourtOwnerBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .testTag("court_owner_bottom_bar"),
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
            CourtBottomBarItem(
                title = "Mi Sede",
                icon = Icons.Outlined.Stadium,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            CourtBottomBarItem(
                title = "Reservas",
                icon = Icons.Outlined.CalendarMonth,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            CourtBottomBarItem(
                title = "Torneos",
                icon = Icons.Outlined.EmojiEvents,
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            CourtBottomBarItem(
                title = "Ajustes",
                icon = Icons.Outlined.Settings,
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun CourtBottomBarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            // Cápsula activa en LimeNeon exactamente como en los mockups de Stitch
            Surface(
                color = LimeNeon,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = DarkForestGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                color = DarkForestGreen
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6B7280)
            )
        }
    }
}
