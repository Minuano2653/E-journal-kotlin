package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.domain.usecases.CreateGroupUseCase
import com.likhachev.e_journal.presentation.ui.admin_groups.AdminGroupsUiState
import com.likhachev.e_journal.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdminGroupsViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminGroupsUiState>(AdminGroupsUiState.Idle)
    val uiState: StateFlow<AdminGroupsUiState> = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Event<String>>()
    val successEvent: SharedFlow<Event<String>> = _successEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    fun createGroup(name: String, startYear: Int) {
        viewModelScope.launch {
            if (!validateInput(name, startYear)) return@launch

            _uiState.value = AdminGroupsUiState.Loading
            try {
                val response = createGroupUseCase(name, startYear)
                _uiState.value = AdminGroupsUiState.Success
                _successEvent.emit(Event("Группа успешно создана"))
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Неверные данные для создания группы"
                    409 -> "Группа с таким названием уже существует"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _uiState.value = AdminGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.value = AdminGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.value = AdminGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    private suspend fun validateInput(name: String, startYear: Int): Boolean {
        return when {
            name.isBlank() -> {
                _errorEvent.emit(Event("Введите название группы"))
                false
            }
            startYear < 2000 || startYear > 2100 -> {
                _errorEvent.emit(Event("Введите корректный год начала обучения"))
                false
            }
            else -> true
        }
    }

    fun resetState() {
        _uiState.value = AdminGroupsUiState.Idle
    }
}