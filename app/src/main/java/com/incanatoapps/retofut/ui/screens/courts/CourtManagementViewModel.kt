package com.incanatoapps.retofut.ui.screens.courts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.incanatoapps.retofut.data.network.model.UserRole

enum class TimeSlotStatus(val displayName: String) {
    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    MANTENIMIENTO("Mantenimiento")
}

data class CourtField(
    val id: Int,
    val name: String,
    val format: String, // Fútbol 5, Fútbol 7, Fútbol 8, Fútbol 11
    val grassType: String,
    val hasLighting: Boolean = true,
    val isAvailable: Boolean = true
)

data class CourtScheduleSlot(
    val id: String,
    val courtId: Int,
    val date: String,
    val time: String,
    val isNight: Boolean,
    val status: TimeSlotStatus,
    val reservedBy: String? = null,
    val contactPhone: String? = null,
    val price: Double
)

data class CourtTournamentSummary(
    val id: Int,
    val name: String,
    val format: String,
    val startDate: String,
    val teamsRegistered: Int,
    val maxTeams: Int,
    val prize: String,
    val status: String
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

    // Información del local deportivo
    var complex by mutableStateOf(
        SportsComplex(
            id = 1,
            name = "Complejo Deportivo El Oasis",
            district = "Chiclayo Centro",
            address = "Av. Salaverry 1240, Chiclayo",
            phone = "+51 974 821 902",
            rating = 4.9,
            totalReviews = 142,
            dayRate = 60.0,
            nightRate = 90.0,
            weekendRate = 100.0,
            amenities = listOf(
                "Iluminación LED Nocturna",
                "Vestuarios & Duchas con Agua Caliente",
                "Estacionamiento Privado Vigilado",
                "Tribunas Techadas & Cafetería",
                "Balones Oficiales & Chalecos"
            )
        )
    )
        private set

    // Canchas registradas de la sede
    val courts = mutableStateListOf(
        CourtField(1, "Cancha 1 (Estelar)", "Fútbol 7", "Césped Sintético Monofilamento FIFA Quality", true),
        CourtField(2, "Cancha 2 (Rápida)", "Fútbol 5", "Césped Sintético Bicolor 50mm", true),
        CourtField(3, "Cancha 3 (Pro)", "Fútbol 8", "Césped Sintético de Alto Tránsito", true),
        CourtField(4, "Cancha 4 (Libre)", "Fútbol 5", "Césped Sintético Premium", true)
    )

    var selectedCourtId by mutableStateOf(1)
    var selectedDate by mutableStateOf("Hoy (Viernes)")

    // Lista de horarios/turnos de la cancha seleccionada
    val scheduleSlots = mutableStateListOf<CourtScheduleSlot>()

    // Torneos organizados en la sede
    val venueTournaments = mutableStateListOf(
        CourtTournamentSummary(
            id = 101,
            name = "Copa Nocturna El Oasis 2026",
            format = "Relámpago Eliminatorio",
            startDate = "Sábado 24 Oct • 19:00 hrs",
            teamsRegistered = 6,
            maxTeams = 8,
            prize = "S/. 1,200 + Trofeo",
            status = "INSCRIPCIONES_ABIERTAS"
        ),
        CourtTournamentSummary(
            id = 102,
            name = "Torneo Inter-Empresas Chiclayo F7",
            format = "Fase de Grupos + Llaves",
            startDate = "Viernes 30 Oct • 20:00 hrs",
            teamsRegistered = 8,
            maxTeams = 8,
            prize = "S/. 2,000 + Medallas",
            status = "EN_DISPUTA"
        )
    )

    // Modo demostrativo de Admin de Cancha
    var isAdminRoleActive by mutableStateOf(true)

    // Estados de diálogo
    var showEditRatesDialog by mutableStateOf(false)
    var showEditComplexDialog by mutableStateOf(false)
    var showAddCourtDialog by mutableStateOf(false)
    var showManualBookingDialog by mutableStateOf(false)
    var selectedSlotForAction by mutableStateOf<CourtScheduleSlot?>(null)

    init {
        loadScheduleForSelectedCourt()
    }

    fun loadScheduleForSelectedCourt() {
        scheduleSlots.clear()
        val times = listOf(
            Pair("08:00 - 09:00", false),
            Pair("09:00 - 10:00", false),
            Pair("10:00 - 11:00", false),
            Pair("11:00 - 12:00", false),
            Pair("14:00 - 15:00", false),
            Pair("15:00 - 16:00", false),
            Pair("16:00 - 17:00", false),
            Pair("17:00 - 18:00", false),
            Pair("18:00 - 19:00", true),
            Pair("19:00 - 20:00", true),
            Pair("20:00 - 21:00", true),
            Pair("21:00 - 22:00", true),
            Pair("22:00 - 23:00", true)
        )

        times.forEachIndexed { index, (timeStr, isNight) ->
            val status = when {
                index == 1 -> TimeSlotStatus.MANTENIMIENTO
                index in listOf(8, 10, 11) -> TimeSlotStatus.RESERVADO
                else -> TimeSlotStatus.DISPONIBLE
            }
            val reservedTeam = when (index) {
                8 -> "Los Galácticos FC (Reserva App)"
                10 -> "Rayo Chiclayano (Pichanga)"
                11 -> "Atlético Pimentel (Liga)"
                else -> null
            }
            val price = if (isNight) complex.nightRate else complex.dayRate

            scheduleSlots.add(
                CourtScheduleSlot(
                    id = "court_${selectedCourtId}_slot_$index",
                    courtId = selectedCourtId,
                    date = selectedDate,
                    time = timeStr,
                    isNight = isNight,
                    status = status,
                    reservedBy = reservedTeam,
                    contactPhone = if (reservedTeam != null) "+51 987 654 321" else null,
                    price = price
                )
            )
        }
    }

    fun onSelectCourt(courtId: Int) {
        selectedCourtId = courtId
        loadScheduleForSelectedCourt()
    }

    fun onSelectDate(date: String) {
        selectedDate = date
        loadScheduleForSelectedCourt()
    }

    fun toggleSlotStatus(slot: CourtScheduleSlot) {
        val nextStatus = when (slot.status) {
            TimeSlotStatus.DISPONIBLE -> TimeSlotStatus.RESERVADO
            TimeSlotStatus.RESERVADO -> TimeSlotStatus.MANTENIMIENTO
            TimeSlotStatus.MANTENIMIENTO -> TimeSlotStatus.DISPONIBLE
        }
        val nextReservedBy = when (nextStatus) {
            TimeSlotStatus.RESERVADO -> slot.reservedBy ?: "Reserva Manual Mostrador"
            else -> null
        }
        val idx = scheduleSlots.indexOfFirst { it.id == slot.id }
        if (idx != -1) {
            scheduleSlots[idx] = slot.copy(
                status = nextStatus,
                reservedBy = nextReservedBy
            )
        }
    }

    fun bookSlotManually(slotId: String, clientName: String, clientPhone: String) {
        val idx = scheduleSlots.indexOfFirst { it.id == slotId }
        if (idx != -1) {
            val current = scheduleSlots[idx]
            scheduleSlots[idx] = current.copy(
                status = TimeSlotStatus.RESERVADO,
                reservedBy = "$clientName (Presencial)",
                contactPhone = clientPhone
            )
        }
        showManualBookingDialog = false
    }

    fun releaseSlot(slotId: String) {
        val idx = scheduleSlots.indexOfFirst { it.id == slotId }
        if (idx != -1) {
            val current = scheduleSlots[idx]
            scheduleSlots[idx] = current.copy(
                status = TimeSlotStatus.DISPONIBLE,
                reservedBy = null,
                contactPhone = null
            )
        }
    }

    fun updateRates(dayRate: Double, nightRate: Double, weekendRate: Double) {
        complex = complex.copy(
            dayRate = dayRate,
            nightRate = nightRate,
            weekendRate = weekendRate
        )
        loadScheduleForSelectedCourt()
        showEditRatesDialog = false
    }

    fun updateComplexInfo(name: String, address: String, district: String, phone: String) {
        complex = complex.copy(
            name = name,
            address = address,
            district = district,
            phone = phone
        )
        showEditComplexDialog = false
    }

    fun addCourt(name: String, format: String, grass: String) {
        val newId = (courts.maxOfOrNull { it.id } ?: 0) + 1
        courts.add(CourtField(newId, name, format, grass, true, true))
        showAddCourtDialog = false
    }

    fun addVenueTournament(tournament: CourtTournamentSummary) {
        venueTournaments.add(0, tournament)
    }
}
