package com.likhachev.e_journal.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.Month
import com.likhachev.e_journal.data.model.Subject
import com.likhachev.e_journal.domain.usecases.GetStudentGradesUseCase
import com.likhachev.e_journal.presentation.ui.student_grades.StudentGradesUiState
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
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StudentGradesViewModel @Inject constructor(
    private val getStudentGradesUseCase: GetStudentGradesUseCase
) : ViewModel() {

    private val _subjects = MutableStateFlow(getSubjects())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _months = MutableStateFlow(getMonths())
    val months: StateFlow<List<Month>> = _months.asStateFlow()

    private val _selectedSubject = MutableStateFlow(_subjects.value.first())
    val selectedSubject: StateFlow<Subject> = _selectedSubject.asStateFlow()

    private val _selectedMonth = MutableStateFlow(getCurrentMonth())
    val selectedMonth: StateFlow<Month> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _gradesState = MutableStateFlow<StudentGradesUiState>(StudentGradesUiState.Idle)
    val gradesState: StateFlow<StudentGradesUiState> = _gradesState.asStateFlow()

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    init {
        loadGrades()
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
        loadGrades()
    }

    fun selectMonth(month: Month) {
        _selectedMonth.value = month
        _selectedYear.value = adjustAcademicYear(month)
        loadGrades()
    }

    fun loadGrades() {
        viewModelScope.launch {
            _gradesState.value = StudentGradesUiState.Loading
            try {
                val grades = getStudentGradesUseCase(
                    subjectId = _selectedSubject.value.id,
                    year = _selectedYear.value,
                    month = _selectedMonth.value.number
                )

                // Sort grades by date in descending order
                val sortedGrades = grades.sortedByDescending { it.date }
                Log.d("GRADES", sortedGrades.toString())
                _gradesState.value = StudentGradesUiState.Success(sortedGrades)
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _gradesState.value = StudentGradesUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _gradesState.value = StudentGradesUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _gradesState.value = StudentGradesUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    private fun getSubjects(): List<Subject> {
        return listOf(
            Subject(13, "Английский язык"),
            Subject(9, "Биология"),
            Subject(1, "Математика"),
            Subject(2, "Алгебра"),
            Subject(3, "Геометрия"),
            Subject(4, "Кубановедение"),
            Subject(5, "Русский язык"),
            Subject(6, "Литература"),
            Subject(7, "Физика"),
            Subject(8, "Химия"),
            Subject(10, "География"),
            Subject(11, "История"),
            Subject(12, "Обществознание"),
            Subject(14, "Информатика"),
            Subject(15, "Физическая культура"),
            Subject(16, "ОБЖ"),
            Subject(17, "Изобразительное искусство"),
            Subject(18, "Музыка"),
            Subject(19, "Технология")
        )
    }

    private fun getMonths(): List<Month> {
        return listOf(
            Month(1, "Январь"),
            Month(2, "Февраль"),
            Month(3, "Март"),
            Month(4, "Апрель"),
            Month(5, "Май"),
            Month(9, "Сентябрь"),
            Month(10, "Октябрь"),
            Month(11, "Ноябрь"),
            Month(12, "Декабрь")
        )
    }

    private fun adjustAcademicYear(selectedMonth: Month): Int {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH) + 1 // Calendar.MONTH от 0 до 11
        val currentYear = now.get(Calendar.YEAR)

        return if (selectedMonth.number < 6 && currentMonth >= 9) {
            // Сентябрь-декабрь -> Январь-май следующего года
            currentYear + 1
        } else if (selectedMonth.number >= 9 && currentMonth < 6) {
            // Январь-май -> Сентябрь-декабрь предыдущего года
            currentYear - 1
        } else {
            currentYear
        }
    }

    private fun getCurrentMonth(): Month {
        val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH) + 1
        return _months.value.find { it.number == currentMonthIndex } ?: _months.value.first()
    }
}