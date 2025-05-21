package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.LoginRequest
import com.likhachev.e_journal.data.model.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): LoginResponse
}