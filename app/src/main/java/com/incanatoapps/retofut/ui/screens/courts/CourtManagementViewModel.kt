package com.incanatoapps.retofut.ui.screens.courts

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.incanatoapps.retofut.data.SessionManager

enum class ShiftStatus(val label: String) {
    RESERVADO("RESERVADO"),
    POR_VALIDAR("POR VALIDAR"),
    TURNO_LIBRE("TURNO LIBRE"),
    EN_JUEGO("EN JUEGO"),
    BLOQUEADO("BLOQUEADO")
}

data class CourtShiftUiModel(
    val id: String,
    val courtId: Int,
    val courtName: String,
    val day: String,
    val timeRange: String,
    val status: ShiftStatus,
    val price: Double,
    val clientOrTeam: String,
    val paymentDetail: String,
    val avatarInitials: String = "",
    val hasLedLighting: Boolean = false,
    val roundInfo: String? = null,
    val depositAttached: Double? = null,
    val refereeBadge: String? = null
)

data class PendingSlipUiModel(
    val id: String,
    val senderName: String,
    val operationNumber: String,
    val timeStr: String,
    val shiftInfo: String,
    val amount: Double,
    val paymentApp: String
)

enum class ReservationStatus(val label: String) {
    CONFIRMADA("Confirmada"),
    SENA_PENDIENTE("Seña Pendiente"),
    BLOQUEADA("Bloqueada")
}

data class CourtReservationUiModel(
    val id: String,
    val clientName: String,
    val status: ReservationStatus,
    val courtId: Int,
    val courtFormat: String,
    val dateTimeLabel: String,
    val matchOrEvent: String,
    val paymentNote: String,
    val phone: String? = null,
    val depositAmount: Double? = null
)

data class CourtTournamentUiModel(
    val id: String,
    val title: String,
    val statusLabel: String,
    val courtLocation: String,
    val dateLabel: String,
    val prizePool: String,
    val teamsRegistered: Int,
    val maxTeams: Int,
    val feePerTeam: Double,
    val quotaBadge: String? = null,
    val formatDetail: String? = null
)

// Modelos heredados para retrocompatibilidad
enum class TimeSlotStatus(val displayName: String) {
    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    MANTENIMIENTO("Mantenimiento")
}

data class CourtField(
    val id: Int,
    val name: String,
    val format: String,
    val grassType: String,
    val hasLighting: Boolean = true,
    val isAvailable: Boolean = true
)

data class SportsComplex(
    val id: Int,
    val name: String,
    val district: String,
    val address: String,
    val phone: String,
    val rating: Double,
    val totalReviews: Int,
    val dayRate: Double,
    val nightRate: Double,
    val weekendRate: Double,
    val amenities: List<String>
)

class CourtManagementViewModel : ViewModel() {

    // Pestaña inferior activa (0: Mi Sede, 1: Reservas, 2: Torneos, 3: Ajustes)
    var selectedBottomTab by mutableIntStateOf(0)

    // Cancha seleccionada (0: Cancha 1 • F7, 1: Cancha 2 • F6, etc.)
    var selectedCourtTab by mutableIntStateOf(0)

    // Día seleccionado ("Hoy (Viernes)", "Mañana (Sábado)", "Domingo 16")
    var selectedDay by mutableStateOf("Hoy (Viernes)")

    // Lista de turnos / timeline
    val shifts = mutableStateListOf<CourtShiftUiModel>()

    // Comprobantes por validar en sección inferior
    val pendingPaymentSlips = mutableStateListOf<PendingSlipUiModel>()

    // Lista de reservas activas
    val reservations = mutableStateListOf<CourtReservationUiModel>()
    var reservationSearchQuery by mutableStateOf("")
    var reservationFilter by mutableStateOf("Todas (6)")

    // Torneos del complejo
    val venueTournaments = mutableStateListOf<CourtTournamentUiModel>()

    // Datos del complejo sincronizados con SessionManager
    var complexName by mutableStateOf("Complejo El Oasis")
    var complexDistrict by mutableStateOf("Santa Victoria, Chiclayo")
    var complexAddress by mutableStateOf("Av. Las Américas 420, Urb. Santa Victoria")
    var complexRuc by mutableStateOf("20608492011")
    var courtsCount by mutableIntStateOf(2)
    var turfType by mutableStateOf("Sintético Pro 50mm")
    var mapsUrl by mutableStateOf("https://maps.app.goo.gl/9X98Z1kOasisCix")
    var hasModularF11 by mutableStateOf(true)

    // Tarifas
    var priceDayF7 by mutableDoubleStateOf(70.0)
    var priceNightF7 by mutableDoubleStateOf(100.0)
    var priceDayF11 by mutableDoubleStateOf(180.0)
    var priceNightF11 by mutableDoubleStateOf(260.0)
    var vipCourtName by mutableStateOf("Cancha 1 Techada Pro LED")
    var vipPrice by mutableDoubleStateOf(130.0)

    // Ajustes operativos
    var operatingHours by mutableStateOf("08:00 AM — 23:30 PM")
    var yapePhone by mutableStateOf("974-812-300")
    var plinPhone by mutableStateOf("974-812-300")
    var paymentTitular by mutableStateOf("El Oasis SAC")

    // Diálogos
    var showNewReservationDialog by mutableStateOf(false)
    var showAssignShiftDialog by mutableStateOf(false)
    var selectedShiftForAssign by mutableStateOf<CourtShiftUiModel?>(null)
    var showCreateTournamentDialog by mutableStateOf(false)
    var showVoucherDialog by mutableStateOf(false)
    var activeVoucherSlip by mutableStateOf<PendingSlipUiModel?>(null)

    // Retrocompatibilidad
    var isAdminRoleActive by mutableStateOf(true)
    var complex by mutableStateOf(
        SportsComplex(
            id = 1,
            name = "Complejo El Oasis",
            district = "Santa Victoria, Chiclayo",
            address = "Av. Las Américas 420, Urb. Santa Victoria",
            phone = "+51 974 812 300",
            rating = 4.9,
            totalReviews = 142,
            dayRate = 70.0,
            nightRate = 100.0,
            weekendRate = 120.0,
            amenities = listOf("Césped Sintético Pro 50mm", "Iluminación LED 400W", "Vestuarios & Gradas", "Estacionamiento Privado")
        )
    )

    init {
        initializeSampleData()
    }

    fun syncWithSession(sessionManager: SessionManager) {
        val sName = sessionManager.fetchComplexName()
        if (sName.isNotBlank() && sName != "Complejo Deportivo El Oasis") {
            complexName = sName
        }
        val sDist = sessionManager.fetchComplexDistrict()
        if (sDist.isNotBlank()) {
            complexDistrict = "$sDist, Chiclayo"
        }
        val sAddr = sessionManager.fetchComplexAddress()
        if (sAddr.isNotBlank()) {
            complexAddress = sAddr
        }
        val sRuc = sessionManager.fetchComplexRuc()
        if (sRuc.isNotBlank()) {
            complexRuc = sRuc
        }
        courtsCount = sessionManager.fetchCourtsCount().coerceAtLeast(1)
        val sMaps = sessionManager.fetchComplexMapsUrl()
        if (sMaps.isNotBlank()) {
            mapsUrl = sMaps
        }
        hasModularF11 = sessionManager.fetchHasModularF11()
        priceDayF7 = sessionManager.fetchPriceDayF7()
        priceNightF7 = sessionManager.fetchPriceNightF7()
        priceDayF11 = sessionManager.fetchPriceDayF11()
        priceNightF11 = sessionManager.fetchPriceNightF11()
        vipCourtName = sessionManager.fetchSpecialVipCourtName()
        vipPrice = sessionManager.fetchSpecialVipPrice()
        operatingHours = sessionManager.fetchOperatingHours()
        yapePhone = sessionManager.fetchYapePhone()
        plinPhone = sessionManager.fetchPlinPhone()
        paymentTitular = sessionManager.fetchPaymentTitular()

        complex = complex.copy(
            name = complexName,
            district = complexDistrict,
            address = complexAddress,
            dayRate = priceDayF7,
            nightRate = priceNightF7
        )
    }

    private fun initializeSampleData() {
        // Turnos Cancha 1 - Hoy (Viernes)
        shifts.clear()
        shifts.addAll(
            listOf(
                // Cancha 1 - Hoy
                CourtShiftUiModel(
                    id = "c1_fri_1",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Hoy (Viernes)",
                    timeRange = "18:00 - 19:00",
                    status = ShiftStatus.RESERVADO,
                    price = 90.0,
                    clientOrTeam = "Pichanga Los Galácticos",
                    paymentDetail = "✔ 100% Yape",
                    avatarInitials = "LG"
                ),
                CourtShiftUiModel(
                    id = "c1_fri_2",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Hoy (Viernes)",
                    timeRange = "19:00 - 20:00",
                    status = ShiftStatus.POR_VALIDAR,
                    price = 90.0,
                    clientOrTeam = "Atlético Chiclayo vs Caimanes",
                    paymentDetail = "Seña S/ 50.00 adjunta",
                    depositAttached = 50.0
                ),
                CourtShiftUiModel(
                    id = "c1_fri_3",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Hoy (Viernes)",
                    timeRange = "20:00 - 21:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 100.0,
                    clientOrTeam = "Disponible para reserva directa",
                    paymentDetail = "✦ S/ 100.00 (Luz LED)",
                    hasLedLighting = true
                ),
                CourtShiftUiModel(
                    id = "c1_fri_4",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Hoy (Viernes)",
                    timeRange = "21:00 - 22:00",
                    status = ShiftStatus.EN_JUEGO,
                    price = 100.0,
                    clientOrTeam = "Torneo Nocturno F7",
                    paymentDetail = "▼ Árbitro CODAR en cancha",
                    roundInfo = "Fecha 3",
                    refereeBadge = "CODAR"
                ),

                // Cancha 2 - Hoy (Viernes)
                CourtShiftUiModel(
                    id = "c2_fri_1",
                    courtId = 2,
                    courtName = "Cancha 2 • F6",
                    day = "Hoy (Viernes)",
                    timeRange = "18:00 - 19:00",
                    status = ShiftStatus.RESERVADO,
                    price = 75.0,
                    clientOrTeam = "Pichanga Barrio Obrero",
                    paymentDetail = "✔ 100% Efectivo",
                    avatarInitials = "BO"
                ),
                CourtShiftUiModel(
                    id = "c2_fri_2",
                    courtId = 2,
                    courtName = "Cancha 2 • F6",
                    day = "Hoy (Viernes)",
                    timeRange = "19:00 - 20:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 75.0,
                    clientOrTeam = "Disponible para reserva inmediata",
                    paymentDetail = "S/ 75.00"
                ),
                CourtShiftUiModel(
                    id = "c2_fri_3",
                    courtId = 2,
                    courtName = "Cancha 2 • F6",
                    day = "Hoy (Viernes)",
                    timeRange = "20:00 - 21:00",
                    status = ShiftStatus.RESERVADO,
                    price = 85.0,
                    clientOrTeam = "Clásico Pimentel F6",
                    paymentDetail = "✔ 100% Yape",
                    hasLedLighting = true,
                    avatarInitials = "CP"
                ),
                CourtShiftUiModel(
                    id = "c2_fri_4",
                    courtId = 2,
                    courtName = "Cancha 2 • F6",
                    day = "Hoy (Viernes)",
                    timeRange = "21:00 - 22:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 85.0,
                    clientOrTeam = "Disponible con Luz LED nocturna",
                    paymentDetail = "S/ 85.00",
                    hasLedLighting = true
                ),

                // Mañana (Sábado) - Cancha 1
                CourtShiftUiModel(
                    id = "c1_sat_1",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Mañana (Sábado)",
                    timeRange = "16:00 - 17:00",
                    status = ShiftStatus.RESERVADO,
                    price = 80.0,
                    clientOrTeam = "Amigos de Santa Victoria",
                    paymentDetail = "✔ 100% Plin",
                    avatarInitials = "SV"
                ),
                CourtShiftUiModel(
                    id = "c1_sat_2",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Mañana (Sábado)",
                    timeRange = "17:00 - 18:00",
                    status = ShiftStatus.RESERVADO,
                    price = 80.0,
                    clientOrTeam = "Academia Juan Aurich Jr",
                    paymentDetail = "✔ 100% Efectivo",
                    avatarInitials = "JA"
                ),
                CourtShiftUiModel(
                    id = "c1_sat_3",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Mañana (Sábado)",
                    timeRange = "19:00 - 20:00",
                    status = ShiftStatus.RESERVADO,
                    price = 100.0,
                    clientOrTeam = "Clásico Chiclayano F7",
                    paymentDetail = "✔ 100% Yape",
                    avatarInitials = "CC"
                ),
                CourtShiftUiModel(
                    id = "c1_sat_4",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Mañana (Sábado)",
                    timeRange = "20:00 - 21:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 100.0,
                    clientOrTeam = "Disponible para reserva nocturna",
                    paymentDetail = "✦ S/ 100.00 (Luz LED)",
                    hasLedLighting = true
                ),

                // Domingo 16 - Cancha 1
                CourtShiftUiModel(
                    id = "c1_sun_1",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Domingo 16",
                    timeRange = "10:00 - 11:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 70.0,
                    clientOrTeam = "Horario matutino disponible",
                    paymentDetail = "S/ 70.00"
                ),
                CourtShiftUiModel(
                    id = "c1_sun_2",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Domingo 16",
                    timeRange = "11:00 - 12:00",
                    status = ShiftStatus.RESERVADO,
                    price = 70.0,
                    clientOrTeam = "Pichanga Dominical Pimentel",
                    paymentDetail = "✔ 100% Yape",
                    avatarInitials = "PD"
                ),
                CourtShiftUiModel(
                    id = "c1_sun_3",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Domingo 16",
                    timeRange = "18:00 - 19:00",
                    status = ShiftStatus.TURNO_LIBRE,
                    price = 90.0,
                    clientOrTeam = "Franja vespertina disponible",
                    paymentDetail = "S/ 90.00"
                ),
                CourtShiftUiModel(
                    id = "c1_sun_4",
                    courtId = 1,
                    courtName = "Cancha 1 • F7",
                    day = "Domingo 16",
                    timeRange = "19:00 - 20:00",
                    status = ShiftStatus.RESERVADO,
                    price = 100.0,
                    clientOrTeam = "Inter-Empresas Chiclayo",
                    paymentDetail = "✔ 100% Yape",
                    avatarInitials = "IE"
                )
            )
        )

        // Comprobantes de pago rápido
        pendingPaymentSlips.clear()
        pendingPaymentSlips.add(
            PendingSlipUiModel(
                id = "slip_784210",
                senderName = "Carlos M.",
                operationNumber = "#784210",
                timeStr = "18:42 hrs",
                shiftInfo = "Turno: Hoy 19:00 - Cancha 1",
                amount = 60.0,
                paymentApp = "YAPE"
            )
        )

        // Lista de reservas activas
        reservations.clear()
        reservations.addAll(
            listOf(
                CourtReservationUiModel(
                    id = "res_1",
                    clientName = "Carlos Mendoza",
                    status = ReservationStatus.SENA_PENDIENTE,
                    courtId = 1,
                    courtFormat = "Cancha 1 (F7)",
                    dateTimeLabel = "Hoy 19:00 - Cancha 1",
                    matchOrEvent = "Atlético Chiclayo vs Caimanes",
                    paymentNote = "Seña S/ 50.00 adjunta",
                    phone = "974-123-456",
                    depositAmount = 50.0
                ),
                CourtReservationUiModel(
                    id = "res_2",
                    clientName = "Pichanga Los Galácticos",
                    status = ReservationStatus.CONFIRMADA,
                    courtId = 1,
                    courtFormat = "Cancha 1 (F7)",
                    dateTimeLabel = "Hoy 18:00 - Cancha 1",
                    matchOrEvent = "Los Galácticos FC",
                    paymentNote = "100% Yape (S/ 90.00)",
                    phone = "979-554-112"
                ),
                CourtReservationUiModel(
                    id = "res_3",
                    clientName = "Renato Balta",
                    status = ReservationStatus.CONFIRMADA,
                    courtId = 2,
                    courtFormat = "Cancha 2 (F6)",
                    dateTimeLabel = "Hoy 18:00 - Cancha 2",
                    matchOrEvent = "Pichanga Barrio Obrero",
                    paymentNote = "100% Efectivo (S/ 75.00)",
                    phone = "941-889-223"
                ),
                CourtReservationUiModel(
                    id = "res_4",
                    clientName = "Clásico Pimentel F6",
                    status = ReservationStatus.CONFIRMADA,
                    courtId = 2,
                    courtFormat = "Cancha 2 (F6)",
                    dateTimeLabel = "Hoy 20:00 - Cancha 2",
                    matchOrEvent = "Amigos Pimentel",
                    paymentNote = "100% Yape (S/ 85.00)",
                    phone = "965-332-111"
                ),
                CourtReservationUiModel(
                    id = "res_5",
                    clientName = "Mantenimiento Cancha",
                    status = ReservationStatus.BLOQUEADA,
                    courtId = 2,
                    courtFormat = "Cancha 2 (F6)",
                    dateTimeLabel = "Mañana 14:00 - Cancha 2",
                    matchOrEvent = "Bloqueo Técnico Semanal",
                    paymentNote = "Cepillado Sintético",
                    phone = null
                ),
                CourtReservationUiModel(
                    id = "res_6",
                    clientName = "Diego Farro",
                    status = ReservationStatus.SENA_PENDIENTE,
                    courtId = 1,
                    courtFormat = "Cancha 1 (F7)",
                    dateTimeLabel = "Sábado 21:00 - Cancha 1",
                    matchOrEvent = "Copa Empresarial Lambayeque",
                    paymentNote = "Seña S/ 40.00 pendiente",
                    phone = "978-009-445",
                    depositAmount = 40.0
                )
            )
        )

        // Torneos de la sede
        venueTournaments.clear()
        venueTournaments.addAll(
            listOf(
                CourtTournamentUiModel(
                    id = "tourney_1",
                    title = "Torneo Apertura F7 Chiclayo",
                    statusLabel = "EN JUEGO • FECHA 3",
                    courtLocation = "Cancha 1 • Sintético Pro",
                    dateLabel = "En juego",
                    prizePool = "S/ 1,500.00",
                    teamsRegistered = 12,
                    maxTeams = 16,
                    feePerTeam = 150.0
                ),
                CourtTournamentUiModel(
                    id = "tourney_2",
                    title = "Copa Relámpago Nocturna",
                    statusLabel = "INSCRIPCIONES ABIERTAS",
                    courtLocation = "Cancha 2 • Grass Sintético",
                    dateLabel = "Sábado 22 Nov",
                    prizePool = "S/ 800.00",
                    teamsRegistered = 5,
                    maxTeams = 8,
                    feePerTeam = 120.0,
                    quotaBadge = "8 Equipos Cupo",
                    formatDetail = "Formato F6 Eliminación directa"
                )
            )
        )
    }

    // Cálculos dinámicos de métricas
    val currentDayRevenue: Double
        get() = when (selectedDay) {
            "Hoy (Viernes)" -> 480.0
            "Mañana (Sábado)" -> 620.0
            "Domingo 16" -> 360.0
            else -> 480.0
        }

    val currentDayOccupiedSlots: Int
        get() = when (selectedDay) {
            "Hoy (Viernes)" -> 6
            "Mañana (Sábado)" -> 7
            "Domingo 16" -> 4
            else -> 6
        }

    val currentDayOccupancyPercent: Int
        get() = when (selectedDay) {
            "Hoy (Viernes)" -> 75
            "Mañana (Sábado)" -> 88
            "Domingo 16" -> 50
            else -> 75
        }

    val pendingDepositsCount: Int
        get() = reservations.count { it.status == ReservationStatus.SENA_PENDIENTE }

    // Filtrado de turnos
    fun getVisibleShifts(): List<CourtShiftUiModel> {
        val targetCourtId = selectedCourtTab + 1
        return shifts.filter { it.courtId == targetCourtId && it.day == selectedDay }
    }

    // Filtrado de reservas
    fun getFilteredReservations(): List<CourtReservationUiModel> {
        return reservations.filter { res ->
            val matchesFilter = when (reservationFilter) {
                "Confirmadas" -> res.status == ReservationStatus.CONFIRMADA
                "Señas Pendientes" -> res.status == ReservationStatus.SENA_PENDIENTE
                "Bloqueadas" -> res.status == ReservationStatus.BLOQUEADA
                else -> true
            }
            val matchesQuery = if (reservationSearchQuery.isBlank()) true else {
                res.clientName.contains(reservationSearchQuery, ignoreCase = true) ||
                        res.matchOrEvent.contains(reservationSearchQuery, ignoreCase = true) ||
                        (res.phone != null && res.phone.contains(reservationSearchQuery))
            }
            matchesFilter && matchesQuery
        }
    }

    // Acciones de turno y validación
    fun confirmPendingShift(shiftId: String) {
        val idx = shifts.indexOfFirst { it.id == shiftId }
        if (idx != -1) {
            val old = shifts[idx]
            shifts[idx] = old.copy(
                status = ShiftStatus.RESERVADO,
                paymentDetail = "✔ 100% Confirmado"
            )
        }
    }

    fun validateVoucher(slipId: String) {
        pendingPaymentSlips.removeAll { it.id == slipId }
        // Actualizar turno correspondiente
        val targetShift = shifts.indexOfFirst { it.id == "c1_fri_2" }
        if (targetShift != -1) {
            shifts[targetShift] = shifts[targetShift].copy(
                status = ShiftStatus.RESERVADO,
                paymentDetail = "✔ 100% Yape (Validado)"
            )
        }
        // Actualizar reserva correspondiente
        val targetRes = reservations.indexOfFirst { it.id == "res_1" }
        if (targetRes != -1) {
            reservations[targetRes] = reservations[targetRes].copy(
                status = ReservationStatus.CONFIRMADA,
                paymentNote = "100% Yape (Validado S/ 90.00)"
            )
        }
        showVoucherDialog = false
    }

    fun rejectVoucher(slipId: String) {
        pendingPaymentSlips.removeAll { it.id == slipId }
        showVoucherDialog = false
    }

    fun confirmReservationDeposit(reservationId: String) {
        val idx = reservations.indexOfFirst { it.id == reservationId }
        if (idx != -1) {
            val old = reservations[idx]
            reservations[idx] = old.copy(
                status = ReservationStatus.CONFIRMADA,
                paymentNote = "100% Confirmada (Sello Manual)"
            )
        }
    }

    fun cancelOrUnblockReservation(reservationId: String) {
        reservations.removeAll { it.id == reservationId }
    }

    fun addManualReservation(
        clientName: String,
        phone: String,
        courtId: Int,
        courtFormat: String,
        dateTimeLabel: String,
        eventName: String,
        paymentNote: String,
        isConfirmed: Boolean
    ) {
        reservations.add(
            0,
            CourtReservationUiModel(
                id = "res_${System.currentTimeMillis()}",
                clientName = clientName,
                status = if (isConfirmed) ReservationStatus.CONFIRMADA else ReservationStatus.SENA_PENDIENTE,
                courtId = courtId,
                courtFormat = courtFormat,
                dateTimeLabel = dateTimeLabel,
                matchOrEvent = eventName,
                paymentNote = paymentNote,
                phone = phone
            )
        )
    }

    fun addTournament(
        title: String,
        format: String,
        prize: String,
        maxTeams: Int,
        fee: Double,
        date: String
    ) {
        venueTournaments.add(
            0,
            CourtTournamentUiModel(
                id = "tourney_${System.currentTimeMillis()}",
                title = title,
                statusLabel = "INSCRIPCIONES ABIERTAS",
                courtLocation = "Cancha 1 • Sintético Pro",
                dateLabel = date,
                prizePool = prize,
                teamsRegistered = 1,
                maxTeams = maxTeams,
                feePerTeam = fee,
                quotaBadge = "$maxTeams Equipos Cupo",
                formatDetail = format
            )
        )
        showCreateTournamentDialog = false
    }

    fun saveSettings(context: Context, onSaved: () -> Unit) {
        val sessionManager = SessionManager(context)
        sessionManager.updateComplexRates(
            dayF7 = priceDayF7,
            nightF7 = priceNightF7,
            hours = operatingHours,
            yapePhone = yapePhone,
            plinPhone = plinPhone
        )
        sessionManager.savePaymentMethods(
            yapePhone = yapePhone,
            plinPhone = plinPhone,
            titular = paymentTitular
        )
        sessionManager.saveOperatingHours(operatingHours)
        onSaved()
    }
}
