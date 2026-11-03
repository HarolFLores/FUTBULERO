package com.incanatoapps.retofut.ui.screens.tournaments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class TournamentStatus(val displayName: String) {
    INSCRIPCIONES_ABIERTAS("Inscripciones Abiertas"),
    EN_DISPUTA("En Disputa"),
    FINALIZADO("Finalizado")
}

enum class TournamentFormat(val displayName: String) {
    RELAMPAGO("Relámpago (1 Día)"),
    LLAVES_ELIMINATORIAS("Llaves Directas (Playoffs)"),
    FASE_GRUPOS("Grupos + Eliminatorias")
}

data class TournamentTeam(
    val id: Int,
    val name: String,
    val captainName: String,
    val logoInitials: String,
    val points: Int = 0,
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val status: String = "CONFIRMADO"
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}

data class TournamentReferee(
    val id: Int,
    val name: String,
    val association: String,
    val rating: Double,
    val feePerMatch: Double
)

data class TournamentMatch(
    val id: Int,
    val tournamentId: Int,
    val phase: String, // Cuartos de Final, Semifinal, Gran Final, Fecha 1
    val teamA: String,
    val teamB: String,
    val scoreA: Int? = null,
    val scoreB: Int? = null,
    val date: String,
    val time: String,
    val field: String,
    val referee: TournamentReferee,
    val status: String = "PROGRAMADO", // PROGRAMADO, EN_VIVO, FINALIZADO
    val linkedMatchId: Int = 1
)

data class TournamentItem(
    val id: Int,
    val name: String,
    val organizerRole: String, // ADMIN_CANCHA o CAPITAN
    val organizerName: String,
    val format: TournamentFormat,
    val gameFormat: String, // Fútbol 5, Fútbol 7, Fútbol 8, Fútbol 11
    val venueName: String,
    val startDate: String,
    val entryFee: Double,
    val prizePool: String,
    val status: TournamentStatus,
    val maxTeams: Int,
    val registeredTeams: List<TournamentTeam>,
    val assignedReferees: List<TournamentReferee>,
    val matches: List<TournamentMatch>
)

class TournamentsViewModel : ViewModel() {

    // Lista principal de torneos disponibles en Chiclayo
    val tournaments = mutableStateListOf<TournamentItem>()

    var selectedTournament by mutableStateOf<TournamentItem?>(null)
    var selectedFilter by mutableStateOf<TournamentStatus?>(null)

    // Diálogos
    var showCreateTournamentDialog by mutableStateOf(false)
    var showRegisterTeamDialog by mutableStateOf(false)
    var selectedTournamentForRegistration by mutableStateOf<TournamentItem?>(null)

    // Árbitros federados disponibles para asignar a los torneos
    val availableFederatedReferees = listOf(
        TournamentReferee(1, "Juan Carlos Pérez", "Asoc. Árbitros Lambayeque (Rol ARBITRO)", 4.9, 35.0),
        TournamentReferee(2, "Roberto 'Cholo' Miranda", "Colegio Árbitros Chiclayo (Rol ARBITRO)", 4.8, 40.0),
        TournamentReferee(3, "Marcos Delgado S.", "Liga Distrital Pimentel (Rol ARBITRO)", 4.7, 30.0)
    )

    init {
        initializeSampleTournaments()
    }

    private fun initializeSampleTournaments() {
        tournaments.clear()

        // 1. Torneo Relámpago (Inscripciones Abiertas)
        val t1Teams = listOf(
            TournamentTeam(1, "Los Galácticos FC", "Piero Flores", "LG", points = 0, played = 0),
            TournamentTeam(2, "Rayo Chiclayano", "Mario Silva", "RC", points = 0, played = 0),
            TournamentTeam(3, "Atlético Pimentel", "Carlos R.", "AP", points = 0, played = 0),
            TournamentTeam(4, "Los Caimanes Pro", "Fabricio D.", "LC", points = 0, played = 0),
            TournamentTeam(5, "Sporting Victoria", "Mateo F.", "SV", points = 0, played = 0),
            TournamentTeam(6, "Deportivo La Unión", "Jorge V.", "DU", points = 0, played = 0)
        )
        val t1Matches = listOf(
            TournamentMatch(
                id = 1001,
                tournamentId = 1,
                phase = "Cuartos de Final (Llave 1)",
                teamA = "Los Galácticos FC",
                teamB = "Rayo Chiclayano",
                date = "Sábado 24 Oct",
                time = "19:00 hrs",
                field = "Cancha 1 (Fútbol 7)",
                referee = availableFederatedReferees[0],
                status = "PROGRAMADO",
                linkedMatchId = 1
            ),
            TournamentMatch(
                id = 1002,
                tournamentId = 1,
                phase = "Cuartos de Final (Llave 2)",
                teamA = "Atlético Pimentel",
                teamB = "Los Caimanes Pro",
                date = "Sábado 24 Oct",
                time = "20:00 hrs",
                field = "Cancha 2 (Fútbol 7)",
                referee = availableFederatedReferees[1],
                status = "PROGRAMADO",
                linkedMatchId = 2
            ),
            TournamentMatch(
                id = 1003,
                tournamentId = 1,
                phase = "Semifinal (Pendiente Ganadores)",
                teamA = "Ganador Llave 1",
                teamB = "Ganador Llave 2",
                date = "Sábado 24 Oct",
                time = "21:30 hrs",
                field = "Cancha 1 (Fútbol 7)",
                referee = availableFederatedReferees[0],
                status = "PROGRAMADO",
                linkedMatchId = 1
            )
        )
        tournaments.add(
            TournamentItem(
                id = 1,
                name = "Copa Relámpago El Oasis 2026",
                organizerRole = "ADMIN_CANCHA",
                organizerName = "Complejo Deportivo El Oasis",
                format = TournamentFormat.RELAMPAGO,
                gameFormat = "Fútbol 7",
                venueName = "Complejo Deportivo El Oasis (Chiclayo Centro)",
                startDate = "Sábado 24 Octubre 2026",
                entryFee = 100.0,
                prizePool = "S/. 1,200 en efectivo + Trofeo Dorado",
                status = TournamentStatus.INSCRIPCIONES_ABIERTAS,
                maxTeams = 8,
                registeredTeams = t1Teams,
                assignedReferees = listOf(availableFederatedReferees[0], availableFederatedReferees[1]),
                matches = t1Matches
            )
        )

        // 2. Superliga Nocturna (En Disputa)
        val t2Teams = listOf(
            TournamentTeam(11, "Chiclayo City FC", "Harold V.", "CC", points = 9, played = 3, won = 3, drawn = 0, lost = 0, goalsFor = 11, goalsAgainst = 3),
            TournamentTeam(12, "Furia Norteña", "Ricardo M.", "FN", points = 6, played = 3, won = 2, drawn = 0, lost = 1, goalsFor = 8, goalsAgainst = 5),
            TournamentTeam(13, "Los Tigres de Reque", "César T.", "TR", points = 3, played = 3, won = 1, drawn = 0, lost = 2, goalsFor = 6, goalsAgainst = 9),
            TournamentTeam(14, "Patazca United", "Andrés S.", "PU", points = 0, played = 3, won = 0, drawn = 0, lost = 3, goalsFor = 2, goalsAgainst = 10)
        )
        val t2Matches = listOf(
            TournamentMatch(
                id = 2001,
                tournamentId = 2,
                phase = "Fecha 4 (En Vivo)",
                teamA = "Chiclayo City FC",
                teamB = "Furia Norteña",
                scoreA = 3,
                scoreB = 2,
                date = "Hoy",
                time = "20:30 hrs",
                field = "Cancha Sintética El Gol #2",
                referee = availableFederatedReferees[0],
                status = "EN_VIVO",
                linkedMatchId = 1
            ),
            TournamentMatch(
                id = 2002,
                tournamentId = 2,
                phase = "Fecha 4",
                teamA = "Los Tigres de Reque",
                teamB = "Patazca United",
                scoreA = null,
                scoreB = null,
                date = "Hoy",
                time = "21:30 hrs",
                field = "Cancha Sintética El Gol #2",
                referee = availableFederatedReferees[2],
                status = "PROGRAMADO",
                linkedMatchId = 2
            )
        )
        tournaments.add(
            TournamentItem(
                id = 2,
                name = "Superliga F7 Chiclayo Nocturno",
                organizerRole = "CAPITAN",
                organizerName = "Capitán Harold V. (Chiclayo City)",
                format = TournamentFormat.FASE_GRUPOS,
                gameFormat = "Fútbol 7",
                venueName = "Canchas Sintéticas El Gol (Pimentel)",
                startDate = "En desarrollo (Octubre 2026)",
                entryFee = 150.0,
                prizePool = "S/. 2,000 + Medallas + Copa Challenger",
                status = TournamentStatus.EN_DISPUTA,
                maxTeams = 4,
                registeredTeams = t2Teams,
                assignedReferees = listOf(availableFederatedReferees[0], availableFederatedReferees[2]),
                matches = t2Matches
            )
        )
    }

    fun selectTournament(tournament: TournamentItem?) {
        selectedTournament = tournament
    }

    fun createTournament(
        name: String,
        organizerRole: String,
        organizerName: String,
        format: TournamentFormat,
        gameFormat: String,
        venueName: String,
        startDate: String,
        entryFee: Double,
        prizePool: String,
        maxTeams: Int
    ) {
        val newId = (tournaments.maxOfOrNull { it.id } ?: 0) + 1
        val initialTeams = listOf(
            TournamentTeam(1, "Equipo Anfitrión", organizerName, "EA", 0, 0, 0, 0, 0, 0, 0)
        )
        val initialMatches = listOf(
            TournamentMatch(
                id = newId * 1000 + 1,
                tournamentId = newId,
                phase = "Ronda Inaugural",
                teamA = "Equipo Anfitrión",
                teamB = "Por Definir (Inscripción)",
                date = startDate,
                time = "19:30 hrs",
                field = "Cancha 1 Oficial",
                referee = availableFederatedReferees.first(),
                status = "PROGRAMADO",
                linkedMatchId = 1
            )
        )
        val newTournament = TournamentItem(
            id = newId,
            name = name,
            organizerRole = organizerRole,
            organizerName = organizerName,
            format = format,
            gameFormat = gameFormat,
            venueName = venueName,
            startDate = startDate,
            entryFee = entryFee,
            prizePool = prizePool,
            status = TournamentStatus.INSCRIPCIONES_ABIERTAS,
            maxTeams = maxTeams,
            registeredTeams = initialTeams,
            assignedReferees = listOf(availableFederatedReferees.first()),
            matches = initialMatches
        )
        tournaments.add(0, newTournament)
        showCreateTournamentDialog = false
    }

    fun registerTeam(tournamentId: Int, teamName: String, captainName: String) {
        val idx = tournaments.indexOfFirst { it.id == tournamentId }
        if (idx != -1) {
            val tourney = tournaments[idx]
            val newTeamId = (tourney.registeredTeams.maxOfOrNull { it.id } ?: 0) + 1
            val initials = teamName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
            val newTeam = TournamentTeam(
                id = newTeamId,
                name = teamName,
                captainName = captainName,
                logoInitials = if (initials.isNotEmpty()) initials else "EQ",
                points = 0
            )
            val updatedTeams = tourney.registeredTeams + newTeam
            tournaments[idx] = tourney.copy(registeredTeams = updatedTeams)

            if (selectedTournament?.id == tournamentId) {
                selectedTournament = tournaments[idx]
            }
        }
        showRegisterTeamDialog = false
    }
}
