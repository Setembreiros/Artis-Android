package com.setembreiros.artis.common.di

import android.app.Application
import android.content.Context
import com.setembreiros.artis.data.preferences.SessionPrefs
import com.setembreiros.artis.data.service.S3Service
import com.setembreiros.artis.domain.interfaces.IPreferences
import com.setembreiros.artis.domain.model.Session
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): IPreferences<Session> {
        return SessionPrefs(context)
    }

    @Provides
    @Singleton
    fun providerE3Service(): S3Service{
        return S3Service()
    }


}