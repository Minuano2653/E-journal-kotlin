package com.likhachev.e_journal.presentation.ui.admin_groups

sealed class AdminGroupsUiState {
    object Idle : AdminGroupsUiState()
    object Loading : AdminGroupsUiState()
    object Success : AdminGroupsUiState()
    data class Error(val message: String) : AdminGroupsUiState()
}