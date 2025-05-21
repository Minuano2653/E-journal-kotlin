package com.likhachev.e_journal.presentation.ui.student_performance

import com.likhachev.e_journal.data.model.PerformanceResponse

sealed class StudentPerformanceUiState {
    object Idle : StudentPerformanceUiState()
    object Loading : StudentPerformanceUiState()
    data class Success(val performance: PerformanceResponse) : StudentPerformanceUiState()
    data class Error(val message: String) : StudentPerformanceUiState()
}