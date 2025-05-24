package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.UpdateJournalEntryResponse
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class UpdateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(journalEntryId: Int, value: String): UpdateJournalEntryResponse {
        return repository.updateJournalEntry(journalEntryId, value)
    }
}