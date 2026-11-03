package com.incanatoapps.retofut.ui.screens.referees

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.incanatoapps.retofut.data.SessionManager
import com.incanatoapps.retofut.data.network.model.UserRole

enum class MatchActStatus(val label: String) {
    PENDIENTE("EN ESPERA DE INICIO"),
    FIRMADA_VALIDADA("FIRMADA CODAR • VALIDADA")
}

data class RefereeMatchActUiModel(
    val id: String,
    val actNumber: String,
    val teamsMatch: String,
    val tournamentAndVenue: String,
    val timeLabel: String,
    val status: MatchActStatus,
    val statsSummary: String,
    val tournamentCategory: String, // "Torneo Apertura", "Liga Nocturna", etc.
    val feeCollected: Double,
    val hasPdfAvailable: Boolean = true
)

data class RefereeDesignationUiModel(
    val id: String,
    val timeLabel: String,
    val statusChip: String,
    val formatBadge: String,
    val venueLabel: String,
    val tournamentName: String,
    val teamsMatch: String,
    val fee: Double,
    val custodyStatus: String,
    val isReadyToStart: Boolean
)

data class RefereeHonoraryRecordUiModel(
    val id: String,
    val teamsMatch: String,
    val dateAndVenue: String,
    val amount: Double,
    val isCustody: Boolean
)

class RefereePortalViewModel : ViewModel() {

    // Pestaña activa (0: Agenda, 1: Actas, 2: Honorarios, 3: CODAR)
    var selectedBottomTab by mutableIntStateOf(0)

    // Datos del árbitro sincronizados
    var refereeName by mutableStateOf("Juan Carlos Pérez Delgado")
    var refereeLicense by mutableStateOf("Carnet Profesional CODAR #4829 • Chiclayo")
    var refereeCategory by mutableStateOf("Árbitro de Primera Categoría")
    var dniNumber by mutableStateOf("44829102")
    var regNumber by mutableStateOf("#4829")
    var phoneContact by mutableStateOf("+51 974 812 390")
    var specialty by mutableStateOf("Fútbol 7 / Futsal / F8 Sintético")
    var personalFee by mutableDoubleStateOf(35.0)

    // Disponibilidad
    var isAvailableFridayNight by mutableStateOf(true)

    // Estadísticas
    var rating by mutableDoubleStateOf(4.9)
    var matchesWhistled by mutableIntStateOf(84)
    var punctualityPercent by mutableIntStateOf(100)
    var rankingPosition by mutableIntStateOf(3)

    // Billetera y Honorarios
    var walletBalance by mutableDoubleStateOf(420.0)
    var inCustodyToday by mutableDoubleStateOf(75.0)
    var retiredThisMonth by mutableDoubleStateOf(680.0)

    // Mesa de Control en Vivo
    var lastIncidentRegistered by mutableStateOf("Min 14' Gol #9 P. Guerrero (Atlético)")

    // Lista de Designaciones (Hoy)
    val designations = mutableStateListOf<RefereeDesignationUiModel>()

    // Lista de Actas Oficiales
    val acts = mutableStateListOf<RefereeMatchActUiModel>()
    var actasFilter by mutableStateOf("Todas (14)")
    var actasSearchQuery by mutableStateOf("")

    // Historial de Honorarios
    val honoraryHistory = mutableStateListOf<RefereeHonoraryRecordUiModel>()

    // Estados de diálogo
    var showIncidentDialog by mutableStateOf(false)
    var activeIncidentType by mutableStateOf("GOL") // GOL, AMARILLA, ROJA, FIRMA
    var showMatchSheetDialog by mutableStateOf(false)
    var activeDesignationForSheet by mutableStateOf<RefereeDesignationUiModel?>(null)
    var showPdfViewerDialog by mutableStateOf(false)
    var activeActForPdf by mutableStateOf<RefereeMatchActUiModel?>(null)
    var showWithdrawalDialog by mutableStateOf(false)
    var showQrVerificationDialog by mutableStateOf(false)

    init {
        initializeData()
    }

    fun syncWithSession(sessionManager: SessionManager) {
        val sName = sessionManager.fetchUserName()
        if (!sName.isNullOrBlank() && sName != "Piero" && sName != "Invitado") {
            refereeName = sName
        }
        val sLicense = sessionManager.fetchRefereeLicense()
        if (sLicense.isNotBlank()) {
            refereeLicense = "Carnet Profesional $sLicense • Chiclayo"
        }
        val sCategory = sessionManager.fetchRefereeCategory()
        if (sCategory.isNotBlank()) {
            refereeCategory = sCategory
        }
        val sFee = sessionManager.fetchRefereeFee()
        if (sFee > 0.0) {
            personalFee = sFee
        }
        val sSpec = sessionManager.fetchRefereeSpecialty()
        if (sSpec.isNotBlank()) {
            specialty = sSpec
        }
    }

    private fun initializeData() {
        // Designaciones de hoy
        designations.clear()
        designations.addAll(
            listOf(
                RefereeDesignationUiModel(
                    id = "des_1",
                    timeLabel = "20:30 hrs",
                    statusChip = "● INICIA EN 45 MIN",
                    formatBadge = "FUTBOL 7",
                    venueLabel = "📍 Complejo El Oasis • Cancha 2 (Césped Sintético)",
                    tournamentName = "TORNEO APERTURA F7",
                    teamsMatch = "Atlético Chiclayo vs Los Galácticos",
                    fee = 35.0,
                    custodyStatus = "✔ Custodia ChocoFulbo ✔",
                    isReadyToStart = true
                ),
                RefereeDesignationUiModel(
                    id = "des_2",
                    timeLabel = "22:00 hrs",
                    statusChip = "PROGRAMADO",
                    formatBadge = "FUTBOL 8",
                    venueLabel = "📍 Canchas Patazca • Cancha Principal",
                    tournamentName = "RETO NOCTURNO F8",
                    teamsMatch = "La Victoria FC vs Santos de Reque",
                    fee = 40.0,
                    custodyStatus = "✔ Confirmado",
                    isReadyToStart = false
                )
            )
        )

        // Actas Oficiales
        acts.clear()
        acts.addAll(
            listOf(
                RefereeMatchActUiModel(
                    id = "act_1",
                    actNumber = "Acta CODAR #2025-0819",
                    teamsMatch = "Atlético Chiclayo vs Los Galácticos",
                    tournamentAndVenue = "Torneo Apertura F7 • Sede El Oasis Cancha 2",
                    timeLabel = "Hoy • 20:30",
                    status = MatchActStatus.PENDIENTE,
                    statsSummary = "Planilla digital en espera de alineaciones de capitanes",
                    tournamentCategory = "Torneo Apertura",
                    feeCollected = 35.0,
                    hasPdfAvailable = false
                ),
                RefereeMatchActUiModel(
                    id = "act_2",
                    actNumber = "Acta CODAR #2025-0812",
                    teamsMatch = "Sport Balta (4) vs (2) Los Jipijapas",
                    tournamentAndVenue = "Liga Nocturna Chiclayo • Canchas Patazca",
                    timeLabel = "Ayer • 21:00",
                    status = MatchActStatus.FIRMADA_VALIDADA,
                    statsSummary = "2 Amarillas • 0 Rojas • Honorario S/ 35 cobrado",
                    tournamentCategory = "Liga Nocturna",
                    feeCollected = 35.0,
                    hasPdfAvailable = true
                ),
                RefereeMatchActUiModel(
                    id = "act_3",
                    actNumber = "Acta CODAR #2025-0798",
                    teamsMatch = "Deportivo Pimentel (3) vs (3) San Lorenzo",
                    tournamentAndVenue = "Copa Interclubes Lambayeque • DeporClub",
                    timeLabel = "12 May • 19:30",
                    status = MatchActStatus.FIRMADA_VALIDADA,
                    statsSummary = "1 Roja directa • Informe complementario anexo",
                    tournamentCategory = "Torneo Apertura",
                    feeCollected = 40.0,
                    hasPdfAvailable = true
                )
            )
        )

        // Historial de Honorarios
        honoraryHistory.clear()
        honoraryHistory.addAll(
            listOf(
                RefereeHonoraryRecordUiModel("hon_1", "Atlético Chiclayo vs Los Galácticos", "Hoy 20:30 • Complejo El Oasis", 35.0, true),
                RefereeHonoraryRecordUiModel("hon_2", "La Victoria FC vs Santos de Reque", "Hoy 22:00 • Canchas Patazca", 40.0, true),
                RefereeHonoraryRecordUiModel("hon_3", "Sport Balta vs Los Jipijapas", "Ayer 21:00 • Patazca", 35.0, false),
                RefereeHonoraryRecordUiModel("hon_4", "Deportivo Pimentel vs San Lorenzo", "12 May • DeporClub", 40.0, false)
            )
        )
    }

    // Filtrado de Actas
    fun getFilteredActs(): List<RefereeMatchActUiModel> {
        return acts.filter { act ->
            val matchesFilter = when (actasFilter) {
                "Firmadas CODAR" -> act.status == MatchActStatus.FIRMADA_VALIDADA
                "Pendientes" -> act.status == MatchActStatus.PENDIENTE
                "Torneo Apertura" -> act.tournamentCategory == "Torneo Apertura"
                else -> true
            }
            val matchesQuery = if (actasSearchQuery.isBlank()) true else {
                act.teamsMatch.contains(actasSearchQuery, ignoreCase = true) ||
                        act.tournamentAndVenue.contains(actasSearchQuery, ignoreCase = true) ||
                        act.actNumber.contains(actasSearchQuery, ignoreCase = true)
            }
            matchesFilter && matchesQuery
        }
    }

    // Registrar incidencia rápida en vivo
    fun registerIncident(type: String, minute: Int, player: String, team: String) {
        lastIncidentRegistered = when (type) {
            "GOL" -> "Min $minute' Gol #$player ($team)"
            "AMARILLA" -> "Min $minute' Tarjeta Amarilla #$player ($team)"
            "ROJA" -> "Min $minute' Tarjeta Roja Directa #$player ($team)"
            "FIRMA" -> "Planilla Firmada Digitalmente por Árbitro CODAR"
            else -> "Incidencia registrada en Min $minute'"
        }
        showIncidentDialog = false
    }

    // Cerrar y firmar acta
    fun signAndValidateAct(actId: String) {
        val idx = acts.indexOfFirst { it.id == actId }
        if (idx != -1) {
            acts[idx] = acts[idx].copy(
                status = MatchActStatus.FIRMADA_VALIDADA,
                statsSummary = "Acta firmada digitalmente con sello CODAR Chiclayo",
                hasPdfAvailable = true
            )
        }
        lastIncidentRegistered = "Acta Oficial Firmada y Subida a FPF Chiclayo"
        showMatchSheetDialog = false
    }

    // Retirar honorarios
    fun withdrawFunds(amount: Double, paymentPhone: String, onCompleted: () -> Unit) {
        if (amount <= walletBalance && amount > 0) {
            walletBalance -= amount
            retiredThisMonth += amount
            onCompleted()
        }
        showWithdrawalDialog = false
    }

    // Cambiar a modo jugador
    fun switchToPlayerMode(context: Context, onSwitched: () -> Unit) {
        val sessionManager = SessionManager(context)
        sessionManager.saveUserRole(UserRole.JUGADOR_CON_EQUIPO)
        onSwitched()
    }
}
