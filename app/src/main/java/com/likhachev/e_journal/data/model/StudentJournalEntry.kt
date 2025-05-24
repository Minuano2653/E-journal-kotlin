package com.likhachev.e_journal.data.model

data class StudentJournalEntry(
    val studentId: String,
    val journalEntryId: Int?,
    val studentName: String,
    val grade: Int?,
    val attendanceStatus: String?
)