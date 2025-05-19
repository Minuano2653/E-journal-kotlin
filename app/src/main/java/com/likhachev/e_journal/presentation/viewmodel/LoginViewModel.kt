package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.domain.usecases.LoginUseCase
import com.likhachev.e_journal.presentation.ui.login.LoginUiEvent
import com.likhachev.e_journal.presentation.ui.login.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val result = loginUseCase(email, password)
                _loginState.value = LoginUiState.Success(result)

                _uiEvent.emit(LoginUiEvent.NavigateToScreen(result.roleId))

            } catch (e: HttpException) {
                _loginState.value = LoginUiState.Error("Ошибка сервера: ${e.code()}")
                when (e.code()) {
                    401 -> _uiEvent.emit(LoginUiEvent.ShowToast("Неверный логин или пароль"))
                    403 -> _uiEvent.emit(LoginUiEvent.ShowToast("Доступ запрещен"))
                    else -> _uiEvent.emit(LoginUiEvent.ShowToast("Ошибка сервера: ${e.code()}"))
                }
            } catch (e: IOException) {
                _loginState.value = LoginUiState.Error("Ошибка сети")
                _uiEvent.emit(LoginUiEvent.ShowToast("Проверьте подключение к интернету"))
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error("Неизвестная ошибка")
                _uiEvent.emit(LoginUiEvent.ShowToast("Ошибка: ${e.message}"))
            }
        }
    }
}