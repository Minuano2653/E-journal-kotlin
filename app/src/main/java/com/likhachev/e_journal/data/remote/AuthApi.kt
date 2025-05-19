package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.LoginRequestDto
import com.likhachev.e_journal.data.model.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}