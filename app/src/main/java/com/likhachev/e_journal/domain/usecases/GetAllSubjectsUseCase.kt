package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.Subject
import com.likhachev.e_journal.domain.repository.AdminTeachersRepository
import javax.inject.Inject

class GetAllSubjectsUseCase @Inject constructor(
    private val repository: AdminTeachersRepository
) {
    suspend operator fun invoke(): List<Subject> {
        return repository.getAllSubjects()
    }
}