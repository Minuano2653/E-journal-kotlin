package com.likhachev.e_journal.presentation.ui.student_grades

import com.likhachev.e_journal.data.model.GradeItem

sealed class StudentGradesUiState {
    object Idle : StudentGradesUiState()
    object Loading : StudentGradesUiState()
    data class Success(val grades: List<GradeItem>) : StudentGradesUiState()
    data class Error(val message: String) : StudentGradesUiState()
}