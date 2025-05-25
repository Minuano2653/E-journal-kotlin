package com.likhachev.e_journal.domain.usecases

import com.likhachev.e_journal.data.model.CreateGroupResponse
import com.likhachev.e_journal.domain.repository.AdminGroupsRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: AdminGroupsRepository
) {
    suspend operator fun invoke(name: String, startYear: Int): CreateGroupResponse {
        return repository.createGroup(name, startYear)
    }
}