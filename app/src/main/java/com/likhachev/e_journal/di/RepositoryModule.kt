package com.likhachev.e_journal.di

import com.likhachev.e_journal.data.remote.AdminGroupsApi
import com.likhachev.e_journal.data.remote.AdminStudentsApi
import com.likhachev.e_journal.data.remote.AdminTeachersApi
import com.likhachev.e_journal.utils.SessionManager
import com.likhachev.e_journal.data.remote.AuthApi
import com.likhachev.e_journal.data.remote.GroupsApi
import com.likhachev.e_journal.data.remote.HomeworkApi
import com.likhachev.e_journal.data.remote.JournalApi
import com.likhachev.e_journal.data.remote.ScheduleApi
import com.likhachev.e_journal.data.repository.AdminGroupsRepositoryImpl
import com.likhachev.e_journal.data.repository.AdminStudentsRepositoryImpl
import com.likhachev.e_journal.data.repository.AdminTeachersRepositoryImpl
import com.likhachev.e_journal.data.repository.AuthRepositoryImpl
import com.likhachev.e_journal.data.repository.GroupsRepositoryImpl
import com.likhachev.e_journal.data.repository.HomeworkRepositoryImpl
import com.likhachev.e_journal.data.repository.JournalRepositoryImpl
import com.likhachev.e_journal.data.repository.ScheduleRepositoryImpl
import com.likhachev.e_journal.domain.repository.AdminGroupsRepository
import com.likhachev.e_journal.domain.repository.AdminStudentsRepository
import com.likhachev.e_journal.domain.repository.AdminTeachersRepository
import com.likhachev.e_journal.domain.repository.AuthRepository
import com.likhachev.e_journal.domain.repository.GroupsRepository
import com.likhachev.e_journal.domain.repository.HomeworkRepository
import com.likhachev.e_journal.domain.repository.JournalRepository
import com.likhachev.e_journal.domain.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        prefs: SessionManager
    ): AuthRepository = AuthRepositoryImpl(api, prefs)

    @Provides
    @Singleton
    fun provideScheduleRepository(
        api: ScheduleApi
    ): ScheduleRepository = ScheduleRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideJournalRepository(
        api: JournalApi
    ): JournalRepository = JournalRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideHomeworkRepository(
        api: HomeworkApi
    ): HomeworkRepository = HomeworkRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideGroupsRepository(
        api: GroupsApi
    ): GroupsRepository = GroupsRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAdminGroupsRepository(
        api: AdminGroupsApi
    ): AdminGroupsRepository = AdminGroupsRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAdminStudentsRepository(
        api: AdminStudentsApi
    ): AdminStudentsRepository = AdminStudentsRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAdminTeachersRepository(
        api: AdminTeachersApi
    ): AdminTeachersRepository = AdminTeachersRepositoryImpl(api)
}