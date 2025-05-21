package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.PerformanceResponse
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class GetStudentPerformanceUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(subjectId: Int): PerformanceResponse {
        return repository.getStudentPerformance(subjectId)
    }
}