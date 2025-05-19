package com.likhachev.e_journal.presentation.ui.student_schedule

import com.likhachev.e_journal.data.model.Lesson

sealed class ScheduleUiState {
    object Idle : ScheduleUiState()
    object Loading : ScheduleUiState()
    data class Success(val lessons: List<Lesson>) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}