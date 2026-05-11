package com.hotmail.or_dvir.sabinesList.di

import android.content.Context
import androidx.room.Room
import com.hotmail.or_dvir.sabinesList.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    @Singleton
    fun provideUserListsDao(db: AppDatabase) = db.userListsDao()

    @Provides
    @Singleton
    fun provideListItemsDao(db: AppDatabase) = db.listItemsDao()
}
