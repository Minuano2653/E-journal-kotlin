package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.Subject
import com.likhachev.e_journal.domain.usecases.GetStudentPerformanceUseCase
import com.likhachev.e_journal.presentation.ui.student_performance.StudentPerformanceUiState
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
class StudentPerformanceViewModel @Inject constructor(
    private val getStudentPerformanceUseCase: GetStudentPerformanceUseCase
) : ViewModel() {

    private val _subjects = MutableStateFlow(getSubjects())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow(_subjects.value.first())
    val selectedSubject: StateFlow<Subject> = _selectedSubject.asStateFlow()

    private val _performanceState = MutableStateFlow<StudentPerformanceUiState>(StudentPerformanceUiState.Idle)
    val performanceState: StateFlow<StudentPerformanceUiState> = _performanceState.asStateFlow()

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    init {
        loadPerformance()
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
        loadPerformance()
    }

    fun loadPerformance() {
        viewModelScope.launch {
            _performanceState.value = StudentPerformanceUiState.Loading
            try {
                val performance = getStudentPerformanceUseCase(
                    subjectId = _selectedSubject.value.id
                )

                _performanceState.value = StudentPerformanceUiState.Success(performance)
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _performanceState.value = StudentPerformanceUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _performanceState.value = StudentPerformanceUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _performanceState.value = StudentPerformanceUiState.Error(message)
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
}