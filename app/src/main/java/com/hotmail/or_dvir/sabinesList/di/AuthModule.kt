package com.hotmail.or_dvir.sabinesList.di

import com.hotmail.or_dvir.sabinesList.auth.repositories.AuthRepository
import com.hotmail.or_dvir.sabinesList.auth.repositories.FirebaseAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}
