package com.incanatoapps.retofut.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("retofut_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_POSITION = "user_position"
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

    fun clearAuthToken() {
        prefs.edit().clear().apply()
    }
}
