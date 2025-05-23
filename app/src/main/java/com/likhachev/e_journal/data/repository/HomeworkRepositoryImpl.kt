package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.CreateHomeworkRequest
import com.likhachev.e_journal.data.model.CreateHomeworkResponse
import com.likhachev.e_journal.data.model.HomeworkResponse
import com.likhachev.e_journal.data.model.UpdateHomeworkRequest
import com.likhachev.e_journal.data.model.UpdateHomeworkResponse
import com.likhachev.e_journal.data.remote.HomeworkApi
import com.likhachev.e_journal.domain.repository.HomeworkRepository
import retrofit2.HttpException
import javax.inject.Inject

class HomeworkRepositoryImpl @Inject constructor(
    private val api: HomeworkApi
) : HomeworkRepository {

    override suspend fun getHomeworkForTeacher(groupId: String, subjectId: Int, date: String): HomeworkResponse? {
        return try {
            api.getHomeworkForTeacher(groupId, subjectId, date)
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        }
    }

    override suspend fun createHomework(groupId: String, subjectId: Int, date: String, description: String): CreateHomeworkResponse {
        return api.createHomework(CreateHomeworkRequest(groupId, subjectId, date, description))
    }

    override suspend fun updateHomework(homeworkId: Int, description: String): UpdateHomeworkResponse {
        return api.updateHomework(UpdateHomeworkRequest(homeworkId, description))
    }
}