package com.likhachev.e_journal.presentation.ui.login

import com.likhachev.e_journal.domain.model.LoginResponse

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val response: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}