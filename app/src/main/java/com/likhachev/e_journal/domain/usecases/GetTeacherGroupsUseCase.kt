package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.TeacherGroupDto
import com.likhachev.e_journal.domain.repository.GroupsRepository
import javax.inject.Inject

class GetTeacherGroupsUseCase @Inject constructor(
    private val repository: GroupsRepository
) {
    suspend operator fun invoke(search: String? = null): List<TeacherGroupDto> {
        return repository.getTeacherGroups(search)
    }
}