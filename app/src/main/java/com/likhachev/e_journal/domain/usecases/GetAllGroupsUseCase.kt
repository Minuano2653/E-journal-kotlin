package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.Group
import com.likhachev.e_journal.domain.repository.AdminGroupsRepository
import javax.inject.Inject

class GetAllGroupsUseCase @Inject constructor(
    private val repository: AdminGroupsRepository
) {
    suspend operator fun invoke(): List<AdminGroupDto> {
        return repository.getAllGroups()
    }
}