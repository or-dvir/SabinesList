package com.hotmail.or_dvir.sabinesList.di

import android.content.Context
import androidx.room.Room
import com.hotmail.or_dvir.sabinesList.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DB_NAME = "SabinesList-db"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideUserListsDao(db: AppDatabase) = db.userListsDao()

    @Provides
    @Singleton
    fun provideListItemsDao(db: AppDatabase) = db.listItemsDao()
}
