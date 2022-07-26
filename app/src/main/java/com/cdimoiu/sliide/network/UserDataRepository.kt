package com.cdimoiu.sliide.network

import com.cdimoiu.sliide.models.User
import javax.inject.Inject

class UserDataRepository @Inject constructor(private val userDataApi: UserDataApi) {
    suspend fun getUsers() = userDataApi.getUsers()
    suspend fun addUser(user: User) = userDataApi.addUser(user)
    suspend fun removeUser(id: String) = userDataApi.removeUser(id)
}