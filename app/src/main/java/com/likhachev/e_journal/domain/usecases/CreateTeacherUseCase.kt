package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.CreateTeacherResponse
import com.likhachev.e_journal.domain.repository.AdminTeachersRepository
import javax.inject.Inject

class CreateTeacherUseCase @Inject constructor(
    private val repository: AdminTeachersRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        surname: String,
        patronymic: String,
        groupIdList: List<String>,
        subjectId: Int
    ): CreateTeacherResponse {
        return repository.createTeacher(email, password, name, surname, patronymic, groupIdList, subjectId)
    }
}