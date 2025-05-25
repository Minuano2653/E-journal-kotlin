package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.likhachev.e_journal.data.model.TeacherGroupDto
import com.likhachev.e_journal.domain.usecases.GetTeacherGroupsUseCase
import com.likhachev.e_journal.presentation.ui.teacher_groups.TeacherGroupsUiState
import com.likhachev.e_journal.utils.Event
import com.likhachev.e_journal.utils.SearchHistoryManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TeacherGroupsViewModel @Inject constructor(
    private val getTeacherGroupsUseCase: GetTeacherGroupsUseCase,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private val _groupsState = MutableStateFlow<TeacherGroupsUiState>(TeacherGroupsUiState.Idle)
    val groupsState: StateFlow<TeacherGroupsUiState> = _groupsState

    private val _errorEvent = MutableSharedFlow<Event<String>>()
    val errorEvent: SharedFlow<Event<String>> = _errorEvent.asSharedFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _isSearchFocused = MutableStateFlow(false)
    val isSearchFocused: StateFlow<Boolean> = _isSearchFocused

    // Храним все загруженные группы
    private var allGroups: List<TeacherGroupDto> = emptyList()

    init {
        loadGroups()
        loadSearchHistory()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(1000) // 300ms debounce
                .distinctUntilChanged()
                .collect { query ->
                    filterGroups(query)
                }
        }
    }

    fun loadGroups() {
        viewModelScope.launch {
            _groupsState.value = TeacherGroupsUiState.Loading
            try {
                val groups = getTeacherGroupsUseCase(null)
                allGroups = groups
                _groupsState.value = TeacherGroupsUiState.Success(groups)
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    404 -> "Группы не найдены"
                    else -> "Ошибка сервера: ${e.code()}"
                }
                _groupsState.value = TeacherGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: IOException) {
                val message = "Проверьте подключение к интернету"
                _groupsState.value = TeacherGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            } catch (e: Exception) {
                val message = "Ошибка: ${e.message}"
                _groupsState.value = TeacherGroupsUiState.Error(message)
                _errorEvent.emit(Event(message))
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSearchSubmitted(query: String) {
        // Сохраняем в историю только при подтверждении поиска
        if (query.isNotEmpty()) {
            searchHistoryManager.addSearchQuery(query)
            loadSearchHistory()
        }
    }

    fun onSearchFocusChanged(isFocused: Boolean) {
        _isSearchFocused.value = isFocused
    }

    fun onHistoryItemClicked(query: String) {
        _searchQuery.value = query
        _isSearchFocused.value = false
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearSearchHistory()
        loadSearchHistory()
    }

    private fun filterGroups(query: String) {
        if (query.isEmpty()) {
            // Если поисковый запрос пустой, показываем все группы
            _groupsState.value = TeacherGroupsUiState.Success(allGroups)
        } else {
            // Фильтруем группы по groupName (регистронезависимый поиск)
            val filteredGroups = allGroups.filter { group ->
                group.groupName.contains(query, ignoreCase = true)
            }
            _groupsState.value = TeacherGroupsUiState.Success(filteredGroups)
        }
    }

    private fun loadSearchHistory() {
        _searchHistory.value = searchHistoryManager.getSearchHistory()
    }
}