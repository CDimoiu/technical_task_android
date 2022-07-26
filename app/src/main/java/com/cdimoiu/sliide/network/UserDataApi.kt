package com.cdimoiu.sliide.network

import com.cdimoiu.sliide.BuildConfig
import com.cdimoiu.sliide.models.User
import retrofit2.Response
import retrofit2.http.*

interface UserDataApi {
    @GET("/public/v2/users")
    suspend fun getUsers(
        @Query("access-token") token: String = BuildConfig.GO_REST_API_KEY
    ): List<User>

    @POST("/public/v2/users")
    suspend fun addUser(
        @Body user: User,
        @Query("access-token") token: String = BuildConfig.GO_REST_API_KEY,
    ): Response<Unit>

    @DELETE("/public/v2/users/{id}")
    suspend fun removeUser(
        @Path("id") id: String,
        @Query("access-token") token: String = BuildConfig.GO_REST_API_KEY,
    ): Response<Unit>
}