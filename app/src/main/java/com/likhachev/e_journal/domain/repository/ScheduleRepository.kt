package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.Lesson
import com.likhachev.e_journal.data.model.TeacherLesson

interface ScheduleRepository {
    suspend fun getScheduleForDay(date: String): List<Lesson>
    suspend fun getTeacherScheduleForDay(date: String): List<TeacherLesson>
}