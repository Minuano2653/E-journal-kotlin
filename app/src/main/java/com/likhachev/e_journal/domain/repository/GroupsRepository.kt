package com.likhachev.e_journal.domain.repository

import com.likhachev.e_journal.data.model.TeacherGroupDto

interface GroupsRepository {
    suspend fun getTeacherGroups(search: String? = null): List<TeacherGroupDto>
}