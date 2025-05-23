package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.HomeworkResponse
import com.likhachev.e_journal.domain.usecases.CreateHomeworkUseCase
import com.likhachev.e_journal.domain.usecases.GetHomeworkUseCase
import com.likhachev.e_journal.domain.usecases.UpdateHomeworkUseCase
import com.likhachev.e_journal.presentation.ui.teacher_homework.HomeworkDialogUiState
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
class HomeworkViewModel @Inject constructor(
    private val getHomeworkUseCase: GetHomeworkUseCase,
    private val createHomeworkUseCase: CreateHomeworkUseCase,
    private val updateHomeworkUseCase: UpdateHomeworkUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<HomeworkDialogUiState>(HomeworkDialogUiState.Idle)
    val uiState: LiveData<HomeworkDialogUiState> = _uiState

    private val _messageEvent = MutableLiveData<Event<String>>()
    val messageEvent: LiveData<Event<String>> = _messageEvent

    private var currentHomework: HomeworkResponse? = null
    private var groupId: String = ""
    private var subjectId: Int = 0
    private var date: String = ""

    fun initialize(groupId: String, subjectId: Int, date: String) {
        this.groupId = groupId
        this.subjectId = subjectId
        this.date = date
        loadHomework()
    }

    private fun loadHomework() {
        _uiState.value = HomeworkDialogUiState.Loading
        viewModelScope.launch {
            try {
                val homework = getHomeworkUseCase(groupId, subjectId, date)
                currentHomework = homework
                if (homework != null) {
                    _uiState.postValue(HomeworkDialogUiState.HomeworkLoaded(homework))
                } else {
                    _uiState.postValue(HomeworkDialogUiState.NoHomework)
                }
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            }
        }
    }

    fun saveHomework(description: String) {
        if (description.isBlank()) {
            _messageEvent.value = Event("Введите текст домашнего задания")
            return
        }

        _uiState.value = HomeworkDialogUiState.Saving
        viewModelScope.launch {
            try {
                if (currentHomework != null) {
                    val response = updateHomeworkUseCase(currentHomework!!.id, description)
                    if (response.success) {
                        _uiState.postValue(HomeworkDialogUiState.Success)
                        _messageEvent.postValue(Event("Домашнее задание обновлено"))
                    } else {
                        _uiState.postValue(HomeworkDialogUiState.Error("Не удалось обновить задание"))
                        _messageEvent.postValue(Event("Не удалось обновить домашнее задание"))
                    }
                } else {
                    createHomeworkUseCase(groupId, subjectId, date, description)
                    _uiState.postValue(HomeworkDialogUiState.Success)
                    _messageEvent.postValue(Event("Домашнее задание задано"))
                }
            } catch (e: HttpException) {
                val message = "Ошибка сервера: ${e.code()}"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _uiState.postValue(HomeworkDialogUiState.Error(message))
                _messageEvent.postValue(Event(message))
            }
        }
    }
}
