package com.likhachev.e_journal.presentation.ui.teacher_schedule

import com.likhachev.e_journal.domain.model.TeacherLesson


sealed class TeacherScheduleUiState {
    object Idle : TeacherScheduleUiState()
    object Loading : TeacherScheduleUiState()
    data class Success(val lessons: List<TeacherLesson>) : TeacherScheduleUiState()
    data class Error(val message: String) : TeacherScheduleUiState()
}