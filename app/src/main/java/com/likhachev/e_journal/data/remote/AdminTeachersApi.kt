package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.CreateTeacherRequest
import com.likhachev.e_journal.data.model.CreateTeacherResponse
import com.likhachev.e_journal.data.model.Subject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminTeachersApi {
    @POST("users/teachers")
    suspend fun createTeacher(@Body request: CreateTeacherRequest): CreateTeacherResponse

    @GET("groups/subjects")
    suspend fun getAllSubjects(): List<Subject>
}