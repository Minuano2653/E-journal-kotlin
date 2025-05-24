package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.StudentJournalEntry
import com.likhachev.e_journal.domain.usecases.CreateJournalEntryUseCase
import com.likhachev.e_journal.domain.usecases.GetGroupGradesUseCase
import com.likhachev.e_journal.domain.usecases.UpdateJournalEntryUseCase
import com.likhachev.e_journal.presentation.ui.teacher_journal.TeacherJournalUiState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TeacherJournalViewModel @Inject constructor(
    private val getGroupGradesUseCase: GetGroupGradesUseCase,
    private val createJournalEntryUseCase: CreateJournalEntryUseCase,
    private val updateJournalEntryUseCase: UpdateJournalEntryUseCase
) : ViewModel() {

    private val dateFormatApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatDisplay = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val _currentDate = MutableStateFlow<Date?>(null)
    val currentDate: StateFlow<Date?> = _currentDate.asStateFlow()

    private val _journalState = MutableStateFlow<TeacherJournalUiState>(TeacherJournalUiState.Idle)
    val journalState: StateFlow<TeacherJournalUiState> = _journalState

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    private val _successEvent = MutableSharedFlow<Event<String>>()
    val successEvent: SharedFlow<Event<String>> = _successEvent.asSharedFlow()

    private var groupId: String = ""
    private var subjectId: Int = 0

    fun initialize(groupId: String, subjectId: Int, dateString: String) {
        this.groupId = groupId
        this.subjectId = subjectId
        parseAndSetDate(dateString)
        getGroupGrades()
    }

    fun getGroupGrades() {
        if (groupId.isEmpty() || subjectId == 0) return

        viewModelScope.launch {
            _journalState.value = TeacherJournalUiState.Loading
            try {
                val formattedDate = getApiFormattedDate()
                val journalData = getGroupGradesUseCase(groupId, subjectId, formattedDate)
                _journalState.value = TeacherJournalUiState.Success(journalData)
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    404 -> "Данные не найдены"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _journalState.value = TeacherJournalUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _journalState.value = TeacherJournalUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _journalState.value = TeacherJournalUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    fun updateStudentGrade(student: StudentJournalEntry, newGrade: String) {
        viewModelScope.launch {
            try {
                if (student.journalEntryId != null) {
                    // Обновляем существующую запись
                    val response = updateJournalEntryUseCase(student.journalEntryId, newGrade)
                    _successEvent.emit(Event("Отметка успешно обновлена"))
                } else {
                    // Создаем новую запись
                    val formattedDate = getApiFormattedDate()
                    val response = createJournalEntryUseCase(student.studentId, subjectId, formattedDate, newGrade)
                    _successEvent.emit(Event("Отметка успешно создана"))
                }
                // Обновляем данные после успешного изменения
                getGroupGrades()
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    400 -> "Неверные данные"
                    404 -> "Запись не найдена"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                _errorEvent.emit(Event("Проверьте подключение к интернету"))
            } catch (e: Exception) {
                _errorEvent.emit(Event("Ошибка: ${e.message}"))
            }
        }
    }

    fun setDate(date: Date) {
        _currentDate.value = date
        getGroupGrades()
    }

    private fun parseAndSetDate(dateString: String) {
        try {
            val parsedDate = dateFormatApi.parse(dateString)
            if (parsedDate != null) {
                _currentDate.value = parsedDate
            }
        } catch (e: Exception) {
            _currentDate.value = Date()
        }
    }

    fun getDisplayFormattedDate(): String {
        return dateFormatDisplay.format(_currentDate.value!!)
    }

    private fun getApiFormattedDate(date: Date = _currentDate.value!!): String {
        return dateFormatApi.format(date)
    }
}