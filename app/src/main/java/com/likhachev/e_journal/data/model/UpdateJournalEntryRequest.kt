package com.likhachev.e_journal.data.model

data class UpdateJournalEntryRequest(
    val journalEntryId: Int,
    val value: String
)