package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.Lesson
import com.likhachev.e_journal.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetScheduleForDayUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(date: String): List<Lesson> {
        return repository.getScheduleForDay(date)
    }
}