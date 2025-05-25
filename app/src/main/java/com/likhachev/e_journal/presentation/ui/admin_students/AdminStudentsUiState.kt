package com.likhachev.e_journal.presentation.ui.admin_students

import com.likhachev.e_journal.data.model.AdminGroupDto

sealed class AdminStudentsUiState {
    object Idle : AdminStudentsUiState()
    object Loading : AdminStudentsUiState()
    object CreatingStudent : AdminStudentsUiState()
    object Success : AdminStudentsUiState()
    data class GroupsLoaded(val groups: List<AdminGroupDto>) : AdminStudentsUiState()
    data class Error(val message: String) : AdminStudentsUiState()
}