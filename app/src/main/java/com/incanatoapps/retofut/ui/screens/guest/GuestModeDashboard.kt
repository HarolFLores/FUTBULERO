package com.incanatoapps.retofut.ui.screens.guest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.incanatoapps.retofut.navigation.Screen
import com.incanatoapps.retofut.ui.theme.*

@Composable
fun GuestModeDashboard(navController: NavHostController) {
    var currentTab by remember { mutableIntStateOf(0) } // 0: Partidos, 1: Torneos
    var showCourtDetail by remember { mutableStateOf(false) }
    var selectedCourtInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showRestrictionModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("guest_mode_dashboard"),
        containerColor = Color(0xFFF9FAFB),
        topBar = {
            GuestTopBanner(
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        },
        bottomBar = {
            GuestBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { tabIndex ->
                    currentTab = tabIndex
                },
                onLockedTabClicked = {
                    showRestrictionModal = true
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> {
                    GuestMatchTab(
                        onViewCourtClick = { complexName, address ->
                            selectedCourtInfo = Pair(complexName, address)
                            showCourtDetail = true
                        },
                        onRestrictedActionClick = {
                            showRestrictionModal = true
                        }
                    )
                }
                1 -> {
                    GuestTournamentsTab(
                        onRestrictedActionClick = {
                            showRestrictionModal = true
                        }
                    )
                }
            }
        }
    }

    // Modal de Detalle de Cancha
    if (showCourtDetail && selectedCourtInfo != null) {
        CourtDetailModal(
            complexName = selectedCourtInfo!!.first,
            address = selectedCourtInfo!!.second,
            onDismiss = {
                showCourtDetail = false
                selectedCourtInfo = null
            },
            onRequireAccountClick = {
                showRestrictionModal = true
            }
        )
    }

    // Modal de Restricción para Invitados (¡Crea tu Equipo y sé Capitán!)
    if (showRestrictionModal) {
        GuestRestrictionModal(
            onDismiss = { showRestrictionModal = false },
            onRegisterClick = {
                showRestrictionModal = false
                navController.navigate(Screen.Register.route)
            },
            onLoginClick = {
                showRestrictionModal = false
                navController.navigate(Screen.Login.route)
            }
        )
    }
}

@Composable
private fun GuestTopBanner(
    onCreateAccountClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = DarkForestGreen,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = "Modo Invitado",
                            tint = LimeNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Modo Invitado (Solo Lectura)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = "Explorando complejos de Chiclayo...",
                        fontSize = 9.5.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Botón verde "CREAR CUENTA GRATIS 🚀"
            Surface(
                onClick = onCreateAccountClick,
                shape = RoundedCornerShape(14.dp),
                color = ForestGreen,
                modifier = Modifier.testTag("guest_top_create_account_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CREAR CUENTA GRATIS",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.width(3.dp))
                    Text("🚀", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun GuestBottomNavigationBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    onLockedTabClicked: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: Partidos
            GuestNavTabItem(
                label = "Partidos",
                icon = Icons.Outlined.SportsSoccer,
                isSelected = currentTab == 0,
                isLocked = false,
                onClick = { onTabSelected(0) }
            )

            // Tab 1: Torneos
            GuestNavTabItem(
                label = "Torneos",
                icon = Icons.Outlined.EmojiEvents,
                isSelected = currentTab == 1,
                isLocked = false,
                onClick = { onTabSelected(1) }
            )

            // Tab 2: Equipos (Locked 🔒)
            GuestNavTabItem(
                label = "Equipos",
                icon = Icons.Outlined.Groups,
                isSelected = false,
                isLocked = true,
                onClick = onLockedTabClicked
            )

            // Tab 3: Perfil (Locked 🔒)
            GuestNavTabItem(
                label = "Perfil",
                icon = Icons.Outlined.Person,
                isSelected = false,
                isLocked = true,
                onClick = onLockedTabClicked
            )
        }
    }
}

@Composable
private fun GuestNavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (isLocked) TextSecondary.copy(alpha = 0.6f) else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Candado rojo si está bloqueado
            if (isLocked) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .size(13.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🔒",
                            fontSize = 7.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            color = if (isSelected) DarkForestGreen else if (isLocked) TextSecondary.copy(alpha = 0.7f) else TextSecondary
        )
    }
}
