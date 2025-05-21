package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.Lesson
import com.likhachev.e_journal.data.model.TeacherLesson
import com.likhachev.e_journal.data.remote.ScheduleApi
import com.likhachev.e_journal.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val api: ScheduleApi,
) : ScheduleRepository {

    override suspend fun getScheduleForDay(date: String): List<Lesson> {
        return api.getGroupScheduleForDay(date).lessons
    }

    override suspend fun getTeacherScheduleForDay(date: String): List<TeacherLesson> {
        return api.getTeacherScheduleForDay(date).lessons
    }
}