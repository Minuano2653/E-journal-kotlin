package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.CreateStudentRequest
import com.likhachev.e_journal.data.model.CreateStudentResponse
import com.likhachev.e_journal.data.remote.AdminStudentsApi
import com.likhachev.e_journal.domain.repository.AdminStudentsRepository
import javax.inject.Inject

class AdminStudentsRepositoryImpl @Inject constructor(
    private val api: AdminStudentsApi
) : AdminStudentsRepository {

    override suspend fun createStudent(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupId: String
    ): CreateStudentResponse {
        val request = CreateStudentRequest(
            email = email,
            password = password,
            name = name,
            surname = surname,
            patronymic = patronymic,
            groupId = groupId
        )
        return api.createStudent(request)
    }
}