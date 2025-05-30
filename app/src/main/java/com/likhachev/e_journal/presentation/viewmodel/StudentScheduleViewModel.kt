package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.domain.usecases.GetScheduleForDayUseCase
import com.likhachev.e_journal.presentation.ui.student_schedule.ScheduleUiState
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
class StudentScheduleViewModel @Inject constructor(
    private val getScheduleForDayUseCase: GetScheduleForDayUseCase
) : ViewModel() {

    private val dateFormatApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatDisplay = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val _currentDate = MutableStateFlow(Calendar.getInstance().time)
    val currentDate: StateFlow<Date> = _currentDate.asStateFlow()

    private val _scheduleState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Idle)
    val scheduleState: StateFlow<ScheduleUiState> = _scheduleState

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    init {
        getScheduleForDay()
    }

    fun getScheduleForDay() {
        viewModelScope.launch {
            _scheduleState.value = ScheduleUiState.Loading
            try {
                val formattedDate = getApiFormattedDate()
                val lessons = getScheduleForDayUseCase(formattedDate)
                _scheduleState.value = ScheduleUiState.Success(lessons)
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _scheduleState.value = ScheduleUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _scheduleState.value = ScheduleUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _scheduleState.value = ScheduleUiState.Error(message)
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



