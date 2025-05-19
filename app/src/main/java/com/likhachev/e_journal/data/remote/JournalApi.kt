package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.GradesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface JournalApi {
    @GET("journal/grades/subject/{subjectId}/year/{year}/month/{month}")
    suspend fun getStudentGrades(
        @Path("subjectId") subjectId: Int,
        @Path("year") year: Int,
        @Path("month") month: Int
    ): GradesResponse
}