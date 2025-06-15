package com.likhachev.e_journal.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME_MODE = "theme_mode"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        applySavedTheme()
    }

    fun getCurrentTheme(): String {
        return prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME_MODE, theme).apply()
        applyTheme(theme)
    }

    fun toggleTheme() {
        val currentTheme = getCurrentTheme()
        val newTheme = when (currentTheme) {
            THEME_LIGHT -> THEME_DARK
            THEME_DARK -> THEME_LIGHT
            THEME_SYSTEM -> {
                // Если система, то переключаем на противоположную текущей системной
                if (isSystemInDarkMode()) THEME_LIGHT else THEME_DARK
            }
            else -> THEME_LIGHT
        }
        setTheme(newTheme)
    }

    private fun applySavedTheme() {
        val savedTheme = getCurrentTheme()
        applyTheme(savedTheme)
    }

    private fun applyTheme(theme: String) {
        val mode = when (theme) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun isSystemInDarkMode(): Boolean {
        return (context.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    fun getCurrentThemeDisplayName(): String {
        return when (getCurrentTheme()) {
            THEME_LIGHT -> "Светлая"
            THEME_DARK -> "Темная"
            THEME_SYSTEM -> "Системная"
            else -> "Системная"
        }
    }
}