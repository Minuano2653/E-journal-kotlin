package com.likhachev.e_journal

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveSession(token: String, role: Int) {
        prefs.edit().putString("token", token).putInt("role", role).apply()
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun getRole(): Int = prefs.getInt("role", ROLE_UNDEFINED)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val ROLE_STUDENT = 1
        const val ROLE_TEACHER = 2
        const val ROLE_ADMINISTRATOR = 3
        const val ROLE_UNDEFINED = -1
    }
}
