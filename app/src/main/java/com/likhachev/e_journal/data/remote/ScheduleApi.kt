package com.likhachev.e_journal.data.remote

import com.likhachev.e_journal.data.model.ScheduleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @GET("schedule/group/date/{date}")
    suspend fun getGroupScheduleForDay(@Path("date") date: String): ScheduleResponse
}