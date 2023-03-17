package com.github.henriquechsf.syscredentialapp.di

import android.content.Context
import androidx.room.Room
import com.github.henriquechsf.syscredentialapp.data.local.AppDatabase
import com.github.henriquechsf.syscredentialapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideEventDao(database: AppDatabase) = database.eventDao()

    @Singleton
    @Provides
    fun providePersonDao(database: AppDatabase) = database.personDao()
}