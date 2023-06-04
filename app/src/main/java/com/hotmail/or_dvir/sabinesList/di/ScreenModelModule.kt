package com.hotmail.or_dvir.sabinesList.di

import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.hotmail.or_dvir.sabinesList.ui.listItemsScreen.ListItemsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class ScreenModelModule {
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ListItemsViewModel.Factory::class)
    abstract fun bindListItemsViewModelFactory(
        factory: ListItemsViewModel.Factory
    ): ScreenModelFactory



}
