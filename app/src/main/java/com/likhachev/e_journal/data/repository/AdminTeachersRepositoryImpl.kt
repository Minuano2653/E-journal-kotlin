package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.CreateTeacherRequest
import com.likhachev.e_journal.data.model.CreateTeacherResponse
import com.likhachev.e_journal.data.model.Subject
import com.likhachev.e_journal.data.remote.AdminTeachersApi
import com.likhachev.e_journal.domain.repository.AdminTeachersRepository
import javax.inject.Inject

class AdminTeachersRepositoryImpl @Inject constructor(
    private val api: AdminTeachersApi
) : AdminTeachersRepository {

    override suspend fun createTeacher(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupIdList: List<String>,
        subjectId: Int
    ): CreateTeacherResponse {
        val request = CreateTeacherRequest(
            email = email,
            password = password,
            name = name,
            surname = surname,
            patronymic = patronymic,
            groupIdList = groupIdList,
            subjectId = subjectId
        )
        return api.createTeacher(request)
    }

    override suspend fun getAllSubjects(): List<Subject> {
        return api.getAllSubjects()
    }
}