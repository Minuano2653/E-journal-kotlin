package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.UpdateHomeworkResponse
import com.likhachev.e_journal.domain.repository.HomeworkRepository
import javax.inject.Inject

class UpdateHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(homeworkId: Int, description: String): UpdateHomeworkResponse {
        return repository.updateHomework(homeworkId, description)
    }
}