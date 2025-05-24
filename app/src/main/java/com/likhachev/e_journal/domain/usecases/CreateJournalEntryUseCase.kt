package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.CreateJournalEntryResponse
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class CreateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(studentId: String, subjectId: Int, date: String, value: String): CreateJournalEntryResponse {
        return repository.createJournalEntry(studentId, subjectId, date, value)
    }
}