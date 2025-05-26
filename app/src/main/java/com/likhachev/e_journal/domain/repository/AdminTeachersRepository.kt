package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.CreateTeacherResponse
import com.likhachev.e_journal.data.model.Subject

interface AdminTeachersRepository {
    suspend fun createTeacher(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupIdList: List<String>,
        subjectId: Int
    ): CreateTeacherResponse

    suspend fun getAllSubjects(): List<Subject>
}
