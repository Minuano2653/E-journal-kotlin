package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.CreateHomeworkRequest
import com.likhachev.e_journal.data.model.CreateHomeworkResponse
import com.likhachev.e_journal.data.model.HomeworkResponse
import com.likhachev.e_journal.data.model.UpdateHomeworkRequest
import com.likhachev.e_journal.data.model.UpdateHomeworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomeworkApi {
    @GET("homework/group/{groupId}/subject/{subjectId}/date/{date}")
    suspend fun getHomeworkForTeacher(
        @Path("groupId") groupId: String,
        @Path("subjectId") subjectId: Int,
        @Path("date") date: String
    ): HomeworkResponse

    @POST("homework")
    suspend fun createHomework(@Body request: CreateHomeworkRequest): CreateHomeworkResponse

    @PUT("homework")
    suspend fun updateHomework(@Body request: UpdateHomeworkRequest): UpdateHomeworkResponse
}