package com.likhachev.e_journal.presentation.ui.login

sealed class LoginUiEvent {
    data class ShowToast(val message: String) : LoginUiEvent()
    data class NavigateToScreen(val roleId: Int) : LoginUiEvent()
}