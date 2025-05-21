package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.LoginRequest
import com.likhachev.e_journal.data.model.LoginResponse
import com.likhachev.e_journal.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): LoginResponse {
        return repository.login(LoginRequest(email, password))
    }
}