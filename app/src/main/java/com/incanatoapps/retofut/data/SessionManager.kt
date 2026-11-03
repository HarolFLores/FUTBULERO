package com.incanatoapps.retofut.data

import android.content.Context
import android.content.SharedPreferences
import com.incanatoapps.retofut.data.network.model.UserRole

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("retofut_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_POSITION = "user_position"
        const val USER_ROLE = "user_role"
        const val IS_GUEST = "is_guest"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).commit()
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(USER_ID, userId).commit()
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(USER_NAME, name).apply()
    }

    fun fetchUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun savePreferredPosition(position: String) {
        prefs.edit().putString(USER_POSITION, position).apply()
    }

    fun fetchPreferredPosition(): String {
        return prefs.getString(USER_POSITION, "Delantero") ?: "Delantero"
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    // --- Sistema de Roles de FUTBULERO ---

    fun saveUserRole(role: UserRole) {
        prefs.edit().putString(USER_ROLE, role.code).apply()
    }

    fun saveUserRole(roleCode: String) {
        val role = UserRole.fromString(roleCode)
        prefs.edit().putString(USER_ROLE, role.code).apply()
    }

    fun fetchUserRole(): UserRole {
        if (isGuest()) return UserRole.INVITADO
        val roleCode = prefs.getString(USER_ROLE, null)
        return UserRole.fromString(roleCode)
    }

    // --- Modo Invitado (Exploración sin Registro) ---

    fun setGuestSession() {
        prefs.edit()
            .putBoolean(IS_GUEST, true)
            .putString(USER_ROLE, UserRole.INVITADO.code)
            .putString(USER_NAME, "Invitado")
            .putString(USER_TOKEN, "guest_token")
            .putInt(USER_ID, -1)
            .apply()
    }

    fun isGuest(): Boolean {
        val isGuestFlag = prefs.getBoolean(IS_GUEST, false)
        val roleStr = prefs.getString(USER_ROLE, null)
        val token = prefs.getString(USER_TOKEN, null)
        return isGuestFlag || roleStr == UserRole.INVITADO.code || token == "guest_token"
    }

    fun clearGuestSession() {
        prefs.edit()
            .putBoolean(IS_GUEST, false)
            .remove(USER_ROLE)
            .apply()
    }

    // --- Permisos por Rol ---

    fun isCaptain(): Boolean {
        return fetchUserRole() == UserRole.CAPITAN
    }

    fun isReferee(): Boolean {
        return fetchUserRole() == UserRole.ARBITRO
    }

    fun isFieldAdmin(): Boolean {
        return fetchUserRole() == UserRole.ADMIN_CANCHA
    }

    fun canCreateContent(): Boolean {
        return !isGuest()
    }

    // --- Datos de Dueño / Admin de Cancha ---
    fun saveCourtAdminDetails(
        complexName: String,
        district: String,
        address: String,
        ruc: String,
        courtsCount: Int,
        formats: String,
        mapsUrl: String = "",
        imageUri: String = "",
        hasModularF11: Boolean = false,
        priceDayF7: Double = 70.0,
        priceNightF7: Double = 110.0,
        priceDayF11: Double = 180.0,
        priceNightF11: Double = 260.0,
        specialVipCourtName: String = "",
        specialVipPrice: Double = 0.0
    ) {
        prefs.edit()
            .putString("court_complex_name", complexName)
            .putString("court_district", district)
            .putString("court_address", address)
            .putString("court_ruc", ruc)
            .putInt("court_count", courtsCount)
            .putString("court_formats", formats)
            .putString("court_maps_url", mapsUrl)
            .putString("court_image_uri", imageUri)
            .putBoolean("court_has_modular_f11", hasModularF11)
            .putString("court_price_day_f7", priceDayF7.toString())
            .putString("court_price_night_f7", priceNightF7.toString())
            .putString("court_price_day_f11", priceDayF11.toString())
            .putString("court_price_night_f11", priceNightF11.toString())
            .putString("court_vip_name", specialVipCourtName)
            .putString("court_vip_price", specialVipPrice.toString())
            .apply()
    }

    fun fetchComplexName(): String = prefs.getString("court_complex_name", "Complejo Deportivo El Oasis") ?: "Complejo Deportivo El Oasis"
    fun fetchComplexDistrict(): String = prefs.getString("court_district", "Chiclayo Centro") ?: "Chiclayo Centro"
    fun fetchComplexAddress(): String = prefs.getString("court_address", "Av. Los Tréboles 450, Urb. Santa Victoria") ?: "Av. Los Tréboles 450, Urb. Santa Victoria"
    fun fetchComplexRuc(): String = prefs.getString("court_ruc", "20608765432") ?: "20608765432"
    fun fetchCourtsCount(): Int = prefs.getInt("court_count", 3)
    fun fetchCourtFormats(): String = prefs.getString("court_formats", "F5, F7, F8") ?: "F5, F7, F8"
    fun fetchComplexMapsUrl(): String = prefs.getString("court_maps_url", "https://maps.google.com/?q=Complejo+Deportivo+El+Oasis+Chiclayo") ?: ""
    fun fetchComplexImageUri(): String = prefs.getString("court_image_uri", "") ?: ""
    fun fetchHasModularF11(): Boolean = prefs.getBoolean("court_has_modular_f11", true)
    fun fetchPriceDayF7(): Double = prefs.getString("court_price_day_f7", "70.0")?.toDoubleOrNull() ?: 70.0
    fun fetchPriceNightF7(): Double = prefs.getString("court_price_night_f7", "110.0")?.toDoubleOrNull() ?: 110.0
    fun fetchPriceDayF11(): Double = prefs.getString("court_price_day_f11", "180.0")?.toDoubleOrNull() ?: 180.0
    fun fetchPriceNightF11(): Double = prefs.getString("court_price_night_f11", "260.0")?.toDoubleOrNull() ?: 260.0
    fun fetchSpecialVipCourtName(): String = prefs.getString("court_vip_name", "Cancha 1 Techada LED") ?: ""
    fun fetchSpecialVipPrice(): Double = prefs.getString("court_vip_price", "130.0")?.toDoubleOrNull() ?: 0.0

    // --- Datos de Árbitro Colegiado ---
    fun saveRefereeDetails(
        license: String,
        category: String,
        fee: Double,
        specialty: String
    ) {
        prefs.edit()
            .putString("referee_license", license)
            .putString("referee_category", category)
            .putString("referee_fee", fee.toString())
            .putString("referee_specialty", specialty)
            .apply()
    }

    fun fetchRefereeLicense(): String = prefs.getString("referee_license", "CODAR-LAMB-#4829") ?: "CODAR-LAMB-#4829"
    fun fetchRefereeCategory(): String = prefs.getString("referee_category", "Primera Categoría FPF") ?: "Primera Categoría FPF"
    fun fetchRefereeFee(): Double = prefs.getString("referee_fee", "35.0")?.toDoubleOrNull() ?: 35.0
    fun fetchRefereeSpecialty(): String = prefs.getString("referee_specialty", "Fútbol 7 & 8 Grass Sintético") ?: "Fútbol 7 & 8 Grass Sintético"

    // --- Transferencia de Capitanía ("Ceder la cinta") ---
    fun savePendingCaptaincyTransfer(fromName: String, toName: String, toPlayerId: Int) {
        prefs.edit()
            .putString("pending_cap_from", fromName)
            .putString("pending_cap_to", toName)
            .putInt("pending_cap_to_id", toPlayerId)
            .putBoolean("pending_cap_active", true)
            .apply()
    }

    fun fetchPendingCaptaincyToName(): String? {
        if (!prefs.getBoolean("pending_cap_active", false)) return null
        return prefs.getString("pending_cap_to", null)
    }

    fun fetchPendingCaptaincyToId(): Int = prefs.getInt("pending_cap_to_id", -1)

    fun clearPendingCaptaincyTransfer() {
        prefs.edit()
            .remove("pending_cap_from")
            .remove("pending_cap_to")
            .remove("pending_cap_to_id")
            .putBoolean("pending_cap_active", false)
            .apply()
    }

    // --- Ficha de Jugador Extras ---
    fun savePlayerFoot(foot: String) {
        prefs.edit().putString("player_foot", foot).apply()
    }
    fun fetchPlayerFoot(): String = prefs.getString("player_foot", "Derecha") ?: "Derecha"

    fun savePlayerNickname(nickname: String) {
        prefs.edit().putString("player_nickname", nickname).apply()
    }
    fun fetchPlayerNickname(): String = prefs.getString("player_nickname", "") ?: ""

    // --- Ajustes Operativos y Medios de Pago de Cancha ---
    fun updateComplexRates(
        dayF7: Double,
        nightF7: Double,
        hours: String = "08:00 AM — 23:30 PM",
        yapePhone: String = "974-812-300",
        plinPhone: String = "974-812-300"
    ) {
        prefs.edit()
            .putString("court_price_day_f7", dayF7.toString())
            .putString("court_price_night_f7", nightF7.toString())
            .putString("court_operating_hours", hours)
            .putString("court_yape_phone", yapePhone)
            .putString("court_plin_phone", plinPhone)
            .apply()
    }

    fun saveOperatingHours(hours: String) {
        prefs.edit().putString("court_operating_hours", hours).apply()
    }
    fun fetchOperatingHours(): String = prefs.getString("court_operating_hours", "08:00 AM — 23:30 PM") ?: "08:00 AM — 23:30 PM"

    fun savePaymentMethods(yapePhone: String, plinPhone: String, titular: String = "El Oasis SAC") {
        prefs.edit()
            .putString("court_yape_phone", yapePhone)
            .putString("court_plin_phone", plinPhone)
            .putString("court_payment_titular", titular)
            .apply()
    }
    fun fetchYapePhone(): String = prefs.getString("court_yape_phone", "974-812-300") ?: "974-812-300"
    fun fetchPlinPhone(): String = prefs.getString("court_plin_phone", "974-812-300") ?: "974-812-300"
    fun fetchPaymentTitular(): String = prefs.getString("court_payment_titular", "El Oasis SAC") ?: "El Oasis SAC"

    fun clearAuthToken() {
        prefs.edit().clear().apply()
    }
}

