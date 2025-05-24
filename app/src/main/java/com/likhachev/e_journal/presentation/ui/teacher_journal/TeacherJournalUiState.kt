package com.likhachev.e_journal.presentation.ui.teacher_journal

import com.likhachev.e_journal.data.model.GroupJournalResponse

sealed class TeacherJournalUiState {
    object Idle : TeacherJournalUiState()
    object Loading : TeacherJournalUiState()
    data class Success(val journalData: GroupJournalResponse) : TeacherJournalUiState()
    data class Error(val message: String) : TeacherJournalUiState()
}