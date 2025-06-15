package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeacherTabsViewModel @Inject constructor() : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _subtitle = MutableLiveData<String>()
    val subtitle: LiveData<String> = _subtitle

    fun setTitle(title: String) {
        _title.value = title
        _subtitle.value = ""
    }

    fun setJournalTitle(groupName: String) {
        _title.value = "Журнал"
        _subtitle.value = groupName
    }
}