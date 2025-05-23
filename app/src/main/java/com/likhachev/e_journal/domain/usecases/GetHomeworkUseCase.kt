package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.HomeworkResponse
import com.likhachev.e_journal.domain.repository.HomeworkRepository
import javax.inject.Inject

class GetHomeworkUseCase @Inject constructor(
    private val repository: HomeworkRepository
) {
    suspend operator fun invoke(groupId: String, subjectId: Int, date: String): HomeworkResponse? {
         return repository.getHomeworkForTeacher(groupId, subjectId, date)
    }
}