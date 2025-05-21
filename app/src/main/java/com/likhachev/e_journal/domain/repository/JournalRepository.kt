package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.GradeItem
import com.likhachev.e_journal.data.model.PerformanceResponse

interface JournalRepository {
    suspend fun getStudentGrades(subjectId: Int, year: Int, month: Int): List<GradeItem>
    suspend fun getStudentPerformance(subjectId: Int): PerformanceResponse
}