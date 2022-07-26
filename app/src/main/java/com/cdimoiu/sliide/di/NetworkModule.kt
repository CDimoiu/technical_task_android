package com.cdimoiu.sliide.di

import com.cdimoiu.sliide.network.UserDataApi
import com.cdimoiu.sliide.network.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val OKHTTP_CALL_TIMEOUT_SEC = 90L
private const val OKHTTP_CONNECT_TIMEOUT_SEC = 10L
private const val OKHTTP_READ_TIMEOUT_SEC = 60L
private const val OKHTTP_WRITE_TIMEOUT_SEC = 20L
private const val OKHTTP_MAX_IDLE_CONNECTIONS = 2
private const val OKHTTP_IDLE_CONNECTION_KEEP_ALIVE_SEC = 60L
private const val API_BASE_URL = "https://gorest.co.in"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun okHttpProvider(): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .callTimeout(Duration.ofSeconds(OKHTTP_CALL_TIMEOUT_SEC))
            .connectTimeout(Duration.ofSeconds(OKHTTP_CONNECT_TIMEOUT_SEC))
            .readTimeout(Duration.ofSeconds(OKHTTP_READ_TIMEOUT_SEC))
            .writeTimeout(Duration.ofSeconds(OKHTTP_WRITE_TIMEOUT_SEC))
            .retryOnConnectionFailure(true)
            .connectionPool(
                ConnectionPool(
                    OKHTTP_MAX_IDLE_CONNECTIONS,
                    OKHTTP_IDLE_CONNECTION_KEEP_ALIVE_SEC,
                    TimeUnit.SECONDS
                )
            )
            .build()

    @Provides
    fun retrofitProvider(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun userDataApiProvider(retrofit: Retrofit): UserDataApi =
        retrofit.create(UserDataApi::class.java)

    @Provides
    fun userDataRepositoryProvider(userDataApi: UserDataApi): UserDataRepository =
        UserDataRepository(userDataApi)
}
