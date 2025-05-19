package com.likhachev.e_journal.di

import com.likhachev.e_journal.utils.SessionManager
import com.likhachev.e_journal.data.remote.AuthApi
import com.likhachev.e_journal.data.remote.JournalApi
import com.likhachev.e_journal.data.remote.ScheduleApi
import com.likhachev.e_journal.data.repository.AuthRepositoryImpl
import com.likhachev.e_journal.data.repository.JournalRepositoryImpl
import com.likhachev.e_journal.data.repository.ScheduleRepositoryImpl
import com.likhachev.e_journal.domain.repository.AuthRepository
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
}