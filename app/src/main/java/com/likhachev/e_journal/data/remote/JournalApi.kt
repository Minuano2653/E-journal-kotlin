package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.GradesResponse
import com.likhachev.e_journal.data.model.GroupJournalResponse
import com.likhachev.e_journal.data.model.PerformanceResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface JournalApi {
    @GET("journal/grades/subject/{subjectId}/year/{year}/month/{month}")
    suspend fun getStudentGrades(
        @Path("subjectId") subjectId: Int,
        @Path("year") year: Int,
        @Path("month") month: Int
    ): GradesResponse

    @GET("journal/performance/subject/{subjectId}")
    suspend fun getStudentPerformance(
        @Path("subjectId") subjectId: Int
    ): PerformanceResponse

    @GET("journal/group/{groupId}/subject/{subjectId}/date/{date}")
    suspend fun getGroupGrades(
        @Path("groupId") groupId: String,
        @Path("subjectId") subjectId: Int,
        @Path("date") date: String
    ): GroupJournalResponse
}