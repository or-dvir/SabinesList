package com.hotmail.or_dvir.sabinesList.di

import android.app.Application
import com.hotmail.or_dvir.sabinesList.BaseApplication
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.FirestoreUserListsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.ListItemsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.roomImpl.RoomListItemsRepositoryImpl
import com.hotmail.or_dvir.sabinesList.database.repositories.UserListsRepository
import com.hotmail.or_dvir.sabinesList.database.repositories.roomImpl.RoomUserListsRepositoryImpl
import com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl.FirestoreListItemsRepositoryImpl
import com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl.FirestoreUserListsRepositoryImpl
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
internal abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindUserListsRepository(
        impl: RoomUserListsRepositoryImpl
    ): UserListsRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreUserListsRepository(
        impl: FirestoreUserListsRepositoryImpl
    ): FirestoreUserListsRepository

    @Binds
    @Singleton
    abstract fun bindListItemsRepository(
        impl: RoomListItemsRepositoryImpl
    ): ListItemsRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreListItemsRepository(
        impl: FirestoreListItemsRepositoryImpl
    ): FirestoreListItemsRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoriesModuleHelper {
    @Provides
    @Singleton
    fun provideCoroutineScopeThatShouldNotBeCancelled(app: Application) =
        (app as BaseApplication).scopeThatShouldNotBeCancelled

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO
}
