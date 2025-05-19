package com.likhachev.e_journal.data.repository

import com.auth0.android.jwt.JWT
import com.likhachev.e_journal.SessionManager
import com.likhachev.e_journal.data.model.LoginRequestDto
import com.likhachev.e_journal.data.remote.AuthApi
import com.likhachev.e_journal.domain.model.LoginRequest
import com.likhachev.e_journal.domain.model.LoginResponse
import com.likhachev.e_journal.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val prefs: SessionManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): LoginResponse {
        val response = api.login(LoginRequestDto(request.email, request.password))

        val jwt = JWT(response.token)
        val roleId = jwt.getClaim("ROLE_ID").asInt() ?: -1

        prefs.saveSession(response.token, roleId)

        return LoginResponse(response.token, roleId)
    }

}