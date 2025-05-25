package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.CreateGroupRequest
import com.likhachev.e_journal.data.model.CreateGroupResponse
import com.likhachev.e_journal.data.model.Group
import com.likhachev.e_journal.data.remote.AdminGroupsApi
import com.likhachev.e_journal.domain.repository.AdminGroupsRepository
import javax.inject.Inject

class AdminGroupsRepositoryImpl @Inject constructor(
    private val api: AdminGroupsApi
) : AdminGroupsRepository {

    override suspend fun getAllGroups(): List<AdminGroupDto> {
        return api.getAllGroups()
    }

    override suspend fun createGroup(name: String, startYear: Int): CreateGroupResponse {
        val request = CreateGroupRequest(
            name = name,
            startYear = startYear,
            endYear = startYear + 1
        )
        return api.createGroup(request)
    }
}