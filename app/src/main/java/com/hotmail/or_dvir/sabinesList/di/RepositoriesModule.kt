package com.hotmail.or_dvir.sabinesList.di

import android.app.Application
import com.hotmail.or_dvir.sabinesList.MyApplication
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepositoryImpl
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepositoryImpl
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepository
import com.hotmail.or_dvir.sabinesList.preferences.repositories.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindUserListsRepository(
        impl: UserListsRepositoryImpl
    ): UserListsRepository

    @Binds
    @Singleton
    abstract fun bindListItemsRepository(
        impl: ListItemsRepositoryImpl
    ): ListItemsRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModuleHelper {
    @Provides
    @Singleton
    fun provideCoroutineScopeThatShouldNotBeCancelled(app: Application) =
        (app as MyApplication).scopeThatShouldNotBeCancelled

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO
}
