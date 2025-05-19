package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.GradeItem
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class GetStudentGradesUseCase @Inject constructor(
    private val repository: JournalRepository
) {
    suspend operator fun invoke(subjectId: Int, year: Int, month: Int): List<GradeItem> {
        return repository.getStudentGrades(subjectId, year, month)
    }
}