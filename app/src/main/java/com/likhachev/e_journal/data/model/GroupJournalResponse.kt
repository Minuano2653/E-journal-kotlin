package com.likhachev.e_journal.data.model

data class GroupJournalResponse(
    val date: String,
    val groupName: String,
    val subjectName: String,
    val students: List<StudentJournalEntry>
)