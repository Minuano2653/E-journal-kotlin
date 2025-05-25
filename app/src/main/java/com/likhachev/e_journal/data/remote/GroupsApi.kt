package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.TeacherGroupsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GroupsApi {
    @GET("groups/teacher")
    suspend fun getTeacherGroups(@Query("search") search: String? = null): TeacherGroupsResponse
}