package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.CreateJournalEntryRequest
import com.likhachev.e_journal.data.model.CreateJournalEntryResponse
import com.likhachev.e_journal.data.model.GradeItem
import com.likhachev.e_journal.data.model.GroupJournalResponse
import com.likhachev.e_journal.data.model.PerformanceResponse
import com.likhachev.e_journal.data.model.UpdateJournalEntryRequest
import com.likhachev.e_journal.data.model.UpdateJournalEntryResponse
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

    override suspend fun getGroupGrades(groupId: String, subjectId: Int, date: String): GroupJournalResponse {
        return api.getGroupGrades(groupId, subjectId, date)
    }

    override suspend fun createJournalEntry(studentId: String, subjectId: Int, date: String, value: String): CreateJournalEntryResponse {
        return api.createJournalEntry(CreateJournalEntryRequest(studentId, subjectId, date, value))
    }

    override suspend fun updateJournalEntry(journalEntryId: Int, value: String): UpdateJournalEntryResponse {
        return api.updateJournalEntry(UpdateJournalEntryRequest(journalEntryId, value))
    }
}