package com.likhachev.e_journal.presentation.ui.teacher_groups

import com.likhachev.e_journal.data.model.TeacherGroupDto

sealed class TeacherGroupsUiState {
    object Idle : TeacherGroupsUiState()
    object Loading : TeacherGroupsUiState()
    data class Success(val groups: List<TeacherGroupDto>) : TeacherGroupsUiState()
    data class Error(val message: String) : TeacherGroupsUiState()
}