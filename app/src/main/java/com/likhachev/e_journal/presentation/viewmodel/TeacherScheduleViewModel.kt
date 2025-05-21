package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.domain.usecases.GetTeacherScheduleForDayUseCase
import com.likhachev.e_journal.presentation.ui.teacher_schedule.TeacherScheduleUiState
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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TeacherScheduleViewModel @Inject constructor(
    private val getTeacherScheduleForDayUseCase: GetTeacherScheduleForDayUseCase
) : ViewModel() {

    private val dateFormatApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatDisplay = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val _currentDate = MutableStateFlow(Calendar.getInstance().time)
    val currentDate: StateFlow<Date> = _currentDate.asStateFlow()

    private val _scheduleState = MutableStateFlow<TeacherScheduleUiState>(TeacherScheduleUiState.Idle)
    val scheduleState: StateFlow<TeacherScheduleUiState> = _scheduleState

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    init {
        getScheduleForDay()
    }

    fun getScheduleForDay() {
        viewModelScope.launch {
            _scheduleState.value = TeacherScheduleUiState.Loading
            try {
                val formattedDate = getApiFormattedDate()
                val lessons = getTeacherScheduleForDayUseCase(formattedDate)
                _scheduleState.value = TeacherScheduleUiState.Success(lessons)
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _scheduleState.value = TeacherScheduleUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _scheduleState.value = TeacherScheduleUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _scheduleState.value = TeacherScheduleUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    fun setDate(date: Date) {
        _currentDate.value = date
        getScheduleForDay()
    }

    fun getDisplayFormattedDate(): String {
        return dateFormatDisplay.format(_currentDate.value)
    }

    private fun getApiFormattedDate(date: Date = _currentDate.value): String {
        return dateFormatApi.format(date)
    }
}