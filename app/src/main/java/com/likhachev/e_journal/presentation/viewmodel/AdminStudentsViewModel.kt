package com.likhachev.e_journal.presentation.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.Group
import com.likhachev.e_journal.domain.usecases.CreateStudentUseCase
import com.likhachev.e_journal.domain.usecases.GetAllGroupsUseCase
import com.likhachev.e_journal.presentation.ui.admin_students.AdminStudentsUiState
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
class AdminStudentsViewModel @Inject constructor(
    private val createStudentUseCase: CreateStudentUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminStudentsUiState>(AdminStudentsUiState.Idle)
    val uiState: StateFlow<AdminStudentsUiState> = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Event<String>>()
    val successEvent: SharedFlow<Event<String>> = _successEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    private var _groups = emptyList<AdminGroupDto>()
    val groups: List<AdminGroupDto> get() = _groups

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = AdminStudentsUiState.Loading
            try {
                _groups = getAllGroupsUseCase()
                _uiState.value = AdminStudentsUiState.GroupsLoaded(_groups)
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    401 -> "Необходима авторизация"
                    403 -> "Недостаточно прав для просмотра групп"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
                Log.d("RRRRR", e.message.toString())
            }
        }
    }

    fun createStudent(
        lastName: String,
        firstName: String,
        middleName: String,
        email: String,
        password: String,
        groupId: String
    ) {
        viewModelScope.launch {
            if (!validateInput(lastName, firstName, middleName, email, password, groupId)) return@launch

            _uiState.value = AdminStudentsUiState.CreatingStudent
            try {
                val response = createStudentUseCase(
                    email = email,
                    password = password,
                    name = firstName,
                    surname = lastName,
                    patronymic = middleName,
                    groupId = groupId
                )
                _uiState.value = AdminStudentsUiState.Success
                _successEvent.emit(Event("Студент успешно создан"))
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Неверные данные для создания студента"
                    409 -> "Пользователь с такой почтой уже существует"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.value = AdminStudentsUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    private suspend fun validateInput(
        lastName: String,
        firstName: String,
        middleName: String,
        email: String,
        password: String,
        groupId: String
    ): Boolean {
        return when {
            lastName.isBlank() -> {
                _errorEvent.emit(Event("Введите фамилию"))
                false
            }
            firstName.isBlank() -> {
                _errorEvent.emit(Event("Введите имя"))
                false
            }
            middleName.isBlank() -> {
                _errorEvent.emit(Event("Введите отчество"))
                false
            }
            email.isBlank() -> {
                _errorEvent.emit(Event("Введите адрес электронной почты"))
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _errorEvent.emit(Event("Введите корректный адрес электронной почты"))
                false
            }
            password.isBlank() -> {
                _errorEvent.emit(Event("Введите пароль"))
                false
            }
            password.length < 6 -> {
                _errorEvent.emit(Event("Пароль должен содержать минимум 6 символов"))
                false
            }
            groupId.isBlank() -> {
                _errorEvent.emit(Event("Выберите группу"))
                false
            }
            else -> true
        }
    }

    fun resetState() {
        _uiState.value = AdminStudentsUiState.Idle
    }
}