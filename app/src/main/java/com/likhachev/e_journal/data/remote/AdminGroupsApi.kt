package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.CreateGroupRequest
import com.likhachev.e_journal.data.model.CreateGroupResponse
import com.likhachev.e_journal.data.model.Group
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminGroupsApi {
    @GET("groups/admin")
    suspend fun getAllGroups(): List<Group>

    @POST("groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): CreateGroupResponse
}