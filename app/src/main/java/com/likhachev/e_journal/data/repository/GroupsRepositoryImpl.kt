package com.likhachev.e_journal.data.repository

import com.likhachev.e_journal.data.model.TeacherGroupDto
import com.likhachev.e_journal.data.remote.GroupsApi
import com.likhachev.e_journal.domain.repository.GroupsRepository
import javax.inject.Inject

class GroupsRepositoryImpl @Inject constructor(
    private val api: GroupsApi
) : GroupsRepository {
    override suspend fun getTeacherGroups(search: String?): List<TeacherGroupDto> {
        return api.getTeacherGroups(search).groups
    }
}