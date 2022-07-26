package com.cdimoiu.sliide.viewmodels

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cdimoiu.sliide.MainCoroutineRule
import com.cdimoiu.sliide.models.Result
import com.cdimoiu.sliide.models.User
import com.cdimoiu.sliide.network.UserDataRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var subject: UsersListViewModel
    private val mockkUserDataRepository: UserDataRepository = mockk(relaxed = true)
    private val mockkConnectivityManager: ConnectivityManager = mockk(relaxed = true)
    private val mockkNetwork: Network = mockk()
    private val mockkNetworkCapabilities: NetworkCapabilities = mockk()

    private val testUser1 = User("1", "Jhon Doe", "jd@gmail.com", "male", "active")
    private val testUser2 = User("2", "Jhon Doe", "jd@gmail.com", "male", "active")
    private val testUser3 = User("3", "Jhon Doe", "jd@gmail.com", "male", "active")
    private val testUsersList = listOf(testUser1, testUser2, testUser3)

    private val testUserRemovedList = listOf(testUser1, testUser2)
    private val testUserRemoveId = "3"

    private val testUserAdded = User("4", "Jhon Doe", "jd@gmail.com", "male", "active")
    private val testUserWithoutId = User("", "Jhon Doe", "jd@gmail.com", "male", "active")
    private val testUserWithoutName = User("4", "", "jd@gmail.com", "male", "active")
    private val testUserWithoutEmail = User("4", "Jhon Doe", "", "male", "active")
    private val testUserWithWrongEmail = User("4", "Jhon Doe", "jd", "male", "active")
    private val testUserAddedList = listOf(testUser1, testUser2, testUser3, testUserAdded)

    @Before
    fun setup() {
        subject = UsersListViewModel(mockkUserDataRepository, mockkConnectivityManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUsers returns error response when ActiveNetwork is null`() = runBlocking {
        coEvery { mockkConnectivityManager.activeNetwork } returns null

        subject.getUsers()
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "No internet connection!"), result)
    }

    @Test
    fun `getUsers returns error response when NetworkCapabilities is null`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns null

        subject.getUsers()
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "No internet connection!"), result)
    }

    @Test
    fun `getUsers returns error response when NetworkCapabilities doesn't have any transport`() =
        runBlocking {
            every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
            every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
            every { mockkNetworkCapabilities.hasTransport(any()) } returns false

            subject.getUsers()
            val result = subject.usersList.first()

            assertEquals(Result.error(null, "No internet connection!"), result)
        }

    @Test
    fun `getUsers returns error response when an exception is thrown`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.getUsers() } throws Exception()

        subject.getUsers()
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Unable to retrieve the users list!"), result)
    }

    @Test
    fun `getUsers returns success response when the api service works properly`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.getUsers() } returns testUsersList

        subject.getUsers()
        val result = subject.usersList.first()

        assertEquals(Result.success(testUsersList, null), result)
    }

    @Test
    fun `removeUser returns error response when an exception is thrown`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.removeUser(testUserRemoveId) } throws Exception()

        subject.removeUser(testUserRemoveId)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Unable to remove the user!"), result)
    }

    @Test
    fun `removeUser returns success response when the api service works properly`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.getUsers() } returns testUserRemovedList

        subject.removeUser(testUserRemoveId)
        val result = subject.usersList.first()

        assertEquals(Result.success(testUserRemovedList, "User removed successfully!"), result)
    }

    @Test
    fun `addUser returns error response when user id is missing`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true

        subject.addUser(testUserWithoutId)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Please insert a valid user Id!"), result)
    }

    @Test
    fun `addUser returns error response when user name is missing`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true

        subject.addUser(testUserWithoutName)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Please insert a valid user name!"), result)
    }

    @Test
    fun `addUser returns error response when user email is missing`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true

        subject.addUser(testUserWithoutEmail)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Please insert a valid user email!"), result)
    }

    @Test
    fun `addUser returns error response when user email is wrong`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true

        subject.addUser(testUserWithWrongEmail)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Please insert a valid user email!"), result)
    }

    @Test
    fun `addUser returns error response when an exception is thrown`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.addUser(testUserAdded) } throws Exception()

        subject.addUser(testUserAdded)
        val result = subject.usersList.first()

        assertEquals(Result.error(null, "Unable to add the user!"), result)
    }

    @Test
    fun `addUser returns success response when the api service works properly`() = runBlocking {
        every { mockkConnectivityManager.activeNetwork } returns mockkNetwork
        every { mockkConnectivityManager.getNetworkCapabilities(mockkNetwork) } returns mockkNetworkCapabilities
        every { mockkNetworkCapabilities.hasTransport(any()) } returns true
        coEvery { mockkUserDataRepository.getUsers() } returns testUserAddedList

        subject.addUser(testUserAdded)
        val result = subject.usersList.first()

        assertEquals(Result.success(testUserAddedList, "User added successfully!"), result)
    }
}