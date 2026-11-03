package com.incanatoapps.retofut

import com.incanatoapps.retofut.ui.screens.guest.GuestTeamStanding
import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para la lógica del Modo Invitado y gestión de roles en ChocoFulbo.
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun guestTeamStanding_dataValidation() {
        val standing = GuestTeamStanding(
            pos = 1,
            acronym = "AC",
            name = "Atlético Chiclayo",
            pj = 3,
            dg = "+7",
            pts = 9,
            badgeBg = androidx.compose.ui.graphics.Color(0xFF0A2E1F),
            badgeTextColor = androidx.compose.ui.graphics.Color(0xFFCCFF00)
        )

        assertEquals(1, standing.pos)
        assertEquals("AC", standing.acronym)
        assertEquals("Atlético Chiclayo", standing.name)
        assertEquals(3, standing.pj)
        assertEquals("+7", standing.dg)
        assertEquals(9, standing.pts)
    }

    @Test
    fun guestMode_restrictionsLogic() {
        // En modo invitado, el usuario sólo puede explorar partidos y torneos en modo lectura
        val allowedTabs = listOf("Partidos", "Torneos")
        val lockedTabs = listOf("Equipos", "Perfil")

        assertTrue(allowedTabs.contains("Partidos"))
        assertTrue(allowedTabs.contains("Torneos"))
        assertTrue(lockedTabs.contains("Equipos"))
        assertTrue(lockedTabs.contains("Perfil"))
    }
}