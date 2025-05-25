package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.CreateStudentResponse
import com.likhachev.e_journal.domain.repository.AdminStudentsRepository
import javax.inject.Inject

class CreateStudentUseCase @Inject constructor(
    private val repository: AdminStudentsRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupId: String
    ): CreateStudentResponse {
        return repository.createStudent(email, password, name, surname, patronymic, groupId)
    }
}