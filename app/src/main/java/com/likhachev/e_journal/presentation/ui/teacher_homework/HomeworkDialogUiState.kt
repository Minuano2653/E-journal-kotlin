package com.likhachev.e_journal.presentation.ui.teacher_homework

import com.likhachev.e_journal.data.model.HomeworkResponse

sealed class HomeworkDialogUiState {
    object Idle : HomeworkDialogUiState()
    object Loading : HomeworkDialogUiState()
    data class HomeworkLoaded(val homework: HomeworkResponse) : HomeworkDialogUiState()
    object NoHomework : HomeworkDialogUiState()
    object Saving : HomeworkDialogUiState()
    object Success : HomeworkDialogUiState()
    data class Error(val message: String) : HomeworkDialogUiState()
}