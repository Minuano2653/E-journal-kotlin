package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.GroupJournalResponse
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class GetGroupGradesUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(groupId: String, subjectId: Int, date: String): GroupJournalResponse {
        return repository.getGroupGrades(groupId, subjectId, date)
    }
}