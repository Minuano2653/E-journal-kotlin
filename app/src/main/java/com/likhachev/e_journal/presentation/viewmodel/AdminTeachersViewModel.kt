package com.likhachev.e_journal.presentation.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.Subject
import com.likhachev.e_journal.domain.usecases.CreateTeacherUseCase
import com.likhachev.e_journal.domain.usecases.GetAllGroupsUseCase
import com.likhachev.e_journal.domain.usecases.GetAllSubjectsUseCase
import com.likhachev.e_journal.presentation.ui.admin_teachers.AdminTeachersUiState
import com.likhachev.e_journal.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
class AdminTeachersViewModel @Inject constructor(
    private val createTeacherUseCase: CreateTeacherUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminTeachersUiState>(AdminTeachersUiState.Idle)
    val uiState: StateFlow<AdminTeachersUiState> = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Event<String>>()
    val successEvent: SharedFlow<Event<String>> = _successEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    private var _groups = emptyList<AdminGroupDto>()
    val groups: List<AdminGroupDto> get() = _groups

    private var _subjects = emptyList<Subject>()
    val subjects: List<Subject> get() = _subjects

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = AdminTeachersUiState.Loading
            try {
                val groupsDeferred = getAllGroupsUseCase()
                val subjectsDeferred = getAllSubjectsUseCase()

                _groups = groupsDeferred
                _subjects = subjectsDeferred

                _uiState.value = AdminTeachersUiState.DataLoaded(_groups, _subjects)
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    401 -> "Необходима авторизация"
                    403 -> "Недостаточно прав"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _uiState.value = AdminTeachersUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.value = AdminTeachersUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.value = AdminTeachersUiState.Error(message)
                _errorEvent.emit(Event(message))
                Log.d("AdminTeachersVM", e.message.toString())
            }
        }
    }

    fun createTeacher(
        lastName: String,
        firstName: String,
        middleName: String,
        email: String,
        password: String,
        selectedGroupIds: List<String>,
        selectedSubjectId: Int
    ) {
        viewModelScope.launch {
            if (!validateInput(lastName, firstName, middleName, email, password, selectedGroupIds, selectedSubjectId)) return@launch

            _uiState.value = AdminTeachersUiState.CreatingTeacher
            try {
                val response = createTeacherUseCase(
                    email = email,
                    password = password,
                    name = firstName,
                    surname = lastName,
                    patronymic = middleName,
                    groupIdList = selectedGroupIds,
                    subjectId = selectedSubjectId
                )
                _uiState.value = AdminTeachersUiState.Success
                _successEvent.emit(Event("Учитель успешно создан"))
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Неверные данные для создания учителя"
                    409 -> "Пользователь с такой почтой уже существует"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _uiState.value = AdminTeachersUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.value = AdminTeachersUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.value = AdminTeachersUiState.Error(message)
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
        selectedGroupIds: List<String>,
        selectedSubjectId: Int
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
            selectedSubjectId == -1 -> {
                _errorEvent.emit(Event("Выберите предмет"))
                false
            }
            selectedGroupIds.isEmpty() -> {
                _errorEvent.emit(Event("Выберите хотя бы одну группу"))
                false
            }
            else -> true
        }
    }

    fun resetState() {
        _uiState.value = AdminTeachersUiState.Idle
    }
}