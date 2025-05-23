package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.CreateHomeworkResponse
import com.likhachev.e_journal.data.model.HomeworkResponse
import com.likhachev.e_journal.data.model.UpdateHomeworkResponse

interface HomeworkRepository {
    suspend fun getHomeworkForTeacher(groupId: String, subjectId: Int, date: String): HomeworkResponse?
    suspend fun createHomework(groupId: String, subjectId: Int, date: String, description: String): CreateHomeworkResponse
    suspend fun updateHomework(homeworkId: Int, description: String): UpdateHomeworkResponse
}
