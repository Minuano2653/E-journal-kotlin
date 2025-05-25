package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.CreateStudentRequest
import com.likhachev.e_journal.data.model.CreateStudentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AdminStudentsApi {
    @POST("users/students")
    suspend fun createStudent(@Body request: CreateStudentRequest): CreateStudentResponse
}