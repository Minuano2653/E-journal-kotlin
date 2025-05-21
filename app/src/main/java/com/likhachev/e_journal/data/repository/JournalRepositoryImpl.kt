package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.GradeItem
import com.likhachev.e_journal.data.model.PerformanceResponse
import com.likhachev.e_journal.data.remote.JournalApi
import com.likhachev.e_journal.domain.repository.JournalRepository
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val api: JournalApi
) : JournalRepository {
    override suspend fun getStudentGrades(subjectId: Int, year: Int, month: Int): List<GradeItem> {
        return api.getStudentGrades(subjectId, year, month).grades
    }

    override suspend fun getStudentPerformance(subjectId: Int): PerformanceResponse {
        return api.getStudentPerformance(subjectId)
    }
}