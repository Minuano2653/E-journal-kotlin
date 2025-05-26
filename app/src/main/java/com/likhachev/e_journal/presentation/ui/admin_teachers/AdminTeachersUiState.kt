package com.likhachev.e_journal.presentation.ui.admin_teachers

import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.Subject

sealed class AdminTeachersUiState {
    object Idle : AdminTeachersUiState()
    object Loading : AdminTeachersUiState()
    object CreatingTeacher : AdminTeachersUiState()
    object Success : AdminTeachersUiState()
    data class DataLoaded(
        val groups: List<AdminGroupDto>,
        val subjects: List<Subject>
    ) : AdminTeachersUiState()
    data class Error(val message: String) : AdminTeachersUiState()
}