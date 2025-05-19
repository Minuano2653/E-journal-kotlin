package com.likhachev.e_journal.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudentTabsViewModel @Inject constructor(): ViewModel() {
    private val _title = MutableLiveData("Расписание на")
    val title: LiveData<String> get() = _title

    fun setTitle(value: String) {
        _title.value = value
    }
}