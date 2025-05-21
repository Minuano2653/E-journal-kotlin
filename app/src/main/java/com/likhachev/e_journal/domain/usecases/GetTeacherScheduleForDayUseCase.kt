package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.TeacherLesson
import com.likhachev.e_journal.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetTeacherScheduleForDayUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(date: String): List<TeacherLesson> {
        return repository.getTeacherScheduleForDay(date)
    }
}