package com.cdimoiu.sliide.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun connectivityManagerProvider(@ApplicationContext appContext: Context) =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}
