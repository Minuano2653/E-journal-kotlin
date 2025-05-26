package com.likhachev.e_journal.di

import com.likhachev.e_journal.data.remote.AdminGroupsApi
import com.likhachev.e_journal.data.remote.AdminStudentsApi
import com.likhachev.e_journal.data.remote.AdminTeachersApi
import com.likhachev.e_journal.data.remote.AuthApi
import com.likhachev.e_journal.data.remote.GroupsApi
import com.likhachev.e_journal.data.remote.HomeworkApi
import com.likhachev.e_journal.data.remote.JournalApi
import com.likhachev.e_journal.data.remote.ScheduleApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(@Named("authRetrofit") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideScheduleApi(@Named("securedRetrofit") retrofit: Retrofit): ScheduleApi =
        retrofit.create(ScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideJournalApi(@Named("securedRetrofit") retrofit: Retrofit): JournalApi =
        retrofit.create(JournalApi::class.java)

    @Provides
    @Singleton
    fun provideHomeworkApi(@Named("securedRetrofit") retrofit: Retrofit): HomeworkApi =
        retrofit.create(HomeworkApi::class.java)

    @Provides
    @Singleton
    fun provideGroupsApi(@Named("securedRetrofit") retrofit: Retrofit): GroupsApi =
        retrofit.create(GroupsApi::class.java)

    @Provides
    @Singleton
    fun provideAdminGroupsApi(@Named("securedRetrofit") retrofit: Retrofit): AdminGroupsApi =
        retrofit.create(AdminGroupsApi::class.java)

    @Provides
    @Singleton
    fun provideAdminStudentsApi(@Named("securedRetrofit") retrofit: Retrofit): AdminStudentsApi =
        retrofit.create(AdminStudentsApi::class.java)

    @Provides
    @Singleton
    fun provideAdminTeachersApi(@Named("securedRetrofit") retrofit: Retrofit): AdminTeachersApi =
        retrofit.create(AdminTeachersApi::class.java)
}
