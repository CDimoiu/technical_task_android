package com.cdimoiu.sliide.viewmodels

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdimoiu.sliide.models.Result
import com.cdimoiu.sliide.models.User
import com.cdimoiu.sliide.network.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    private val _usersList = MutableStateFlow<Result<List<User>>>(Result.loading(data = null))
    val usersList: MutableStateFlow<Result<List<User>>> = _usersList

    init {
        getUsers()
    }

    fun getUsers() {
        viewModelScope.launch {
            if (isNetworkAvailable()) {
                try {
                    _usersList.value = Result.success(data = userDataRepository.getUsers())
                } catch (exception: Exception) {
                    emitError("Unable to retrieve the users list!")
                }
            }
        }
    }

    fun removeUser(userId: String) {
        viewModelScope.launch {
            if (isNetworkAvailable()) {
                try {
                    userDataRepository.removeUser(userId)
                    _usersList.value = Result.success(
                        data = userDataRepository.getUsers(),
                        message = "User removed successfully!"
                    )
                } catch (exception: Exception) {
                    emitError("Unable to remove the user!")
                }
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            if (isNetworkAvailable()) {
                if (isValidUser(user)) {
                    try {
                        userDataRepository.addUser(user)
                        _usersList.value = Result.success(
                            data = userDataRepository.getUsers(),
                            message = "User added successfully!"
                        )
                    } catch (exception: Exception) {
                        emitError("Unable to add the user!")
                    }
                }
            }
        }
    }

    private fun isValidUser(user: User): Boolean {
        return when {
            user.id.isBlank() -> {
                emitError("Please insert a valid user Id!")
                return false
            }
            user.name.isBlank() -> {
                emitError("Please insert a valid user name!")
                return false
            }
            !user.email.isValidEmail() -> {
                emitError("Please insert a valid user email!")
                return false
            }
            else -> true
        }
    }

    private fun emitError(message: String? = null) {
        _usersList.value = Result.error(data = null, message = message)
    }

    private fun isNetworkAvailable(): Boolean {
        _usersList.value = Result.loading(data = null)
        return if (!checkInternetConnection()) {
            _usersList.value = Result.error(data = null, message = "No internet connection!")
            false
        } else {
            true
        }
    }

    private fun checkInternetConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun String.isValidEmail() =
        !isBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}
