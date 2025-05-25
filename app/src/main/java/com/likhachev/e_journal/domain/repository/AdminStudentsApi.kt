package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.CreateStudentResponse

interface AdminStudentsRepository {
    suspend fun createStudent(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupId: String
    ): CreateStudentResponse
}