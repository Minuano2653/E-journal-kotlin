package com.likhachev.e_journal.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val maxHistorySize = 10

    fun addSearchQuery(query: String) {
        if (query.isBlank()) return

        val currentHistory = getSearchHistory().toMutableList()

        // Удаляем если уже есть, чтобы переместить в начало
        currentHistory.remove(query)

        // Добавляем в начало
        currentHistory.add(0, query)

        // Ограничиваем размер до maxHistorySize
        if (currentHistory.size > maxHistorySize) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveSearchHistory(currentHistory)
    }

    fun getSearchHistory(): List<String> {
        val historyString = prefs.getString(HISTORY_KEY, "") ?: ""
        return if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(SEPARATOR)
        }
    }

    fun clearSearchHistory() {
        prefs.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveSearchHistory(history: List<String>) {
        val historyString = history.joinToString(SEPARATOR)
        prefs.edit().putString(HISTORY_KEY, historyString).apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val SEPARATOR = "|||"
    }
}