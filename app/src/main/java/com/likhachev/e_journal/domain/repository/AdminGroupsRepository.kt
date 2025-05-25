package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.AdminGroupDto
import com.likhachev.e_journal.data.model.CreateGroupResponse
import com.likhachev.e_journal.data.model.Group

interface AdminGroupsRepository {
    suspend fun getAllGroups(): List<AdminGroupDto>
    suspend fun createGroup(name: String, startYear: Int): CreateGroupResponse
}