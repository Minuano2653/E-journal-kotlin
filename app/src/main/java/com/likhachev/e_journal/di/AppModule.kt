package com.likhachev.e_journal.di

import com.likhachev.e_journal.SessionManager
import com.likhachev.e_journal.data.remote.AuthApi
import com.likhachev.e_journal.data.repository.AuthRepositoryImpl
import com.likhachev.e_journal.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAuthApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.100:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        prefs: SessionManager
    ): AuthRepository = AuthRepositoryImpl(api, prefs)
}