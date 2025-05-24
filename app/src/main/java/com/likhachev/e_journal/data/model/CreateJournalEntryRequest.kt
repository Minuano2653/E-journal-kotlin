package com.likhachev.e_journal.data.model

data class CreateJournalEntryRequest(
    val studentId: String,
    val subjectId: Int,
    val date: String,
    val value: String
)