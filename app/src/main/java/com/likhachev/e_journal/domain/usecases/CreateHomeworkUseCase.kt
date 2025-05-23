package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.CreateHomeworkResponse
import com.likhachev.e_journal.domain.repository.HomeworkRepository
import javax.inject.Inject

class CreateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(groupId: String, subjectId: Int, date: String, description: String): CreateHomeworkResponse {
        return repository.createHomework(groupId, subjectId, date, description)
    }
}