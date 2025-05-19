package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.Lesson

interface ScheduleRepository {
    suspend fun getScheduleForDay(date: String): List<Lesson>
}