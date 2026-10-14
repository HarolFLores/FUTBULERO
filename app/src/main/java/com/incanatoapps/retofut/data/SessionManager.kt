package com.incanatoapps.retofut.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("retofut_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID = "user_id"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).commit()
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(USER_ID, userId).commit()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun fetchUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    fun clearAuthToken() {
        prefs.edit().remove(USER_TOKEN).remove(USER_ID).apply()
    }
}
