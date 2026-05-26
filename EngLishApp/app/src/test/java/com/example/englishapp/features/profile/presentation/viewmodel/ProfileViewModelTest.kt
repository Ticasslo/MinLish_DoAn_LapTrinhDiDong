package com.example.englishapp.features.profile.presentation.viewmodel

import app.cash.turbine.test
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.profile.domain.usecase.ChangePasswordUseCase
import com.example.englishapp.features.profile.domain.usecase.GetUserDataUseCase
import com.example.englishapp.features.profile.domain.usecase.LogoutUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val getUserDataUseCase: GetUserDataUseCase = mockk()
    private val logoutUseCase: LogoutUseCase = mockk()
    private val changePasswordUseCase: ChangePasswordUseCase = mockk()

    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserData success updates uiState`() = runTest {
        val user = User(userId = "123", email = "test@example.com", name = "Test User")
        every { getUserDataUseCase() } returns flowOf(AuthResult.Success(user))

        viewModel = ProfileViewModel(getUserDataUseCase, logoutUseCase, changePasswordUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(user, state.user)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun `logout updates isLoggedOut to true`() = runTest {
        every { getUserDataUseCase() } returns flowOf(AuthResult.Loading)
        coEvery { logoutUseCase() } returns Unit

        viewModel = ProfileViewModel(getUserDataUseCase, logoutUseCase, changePasswordUseCase)

        viewModel.logout()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(true, state.isLoggedOut)
        }
    }

    @Test
    fun `changePassword updates changePasswordResult`() = runTest {
        every { getUserDataUseCase() } returns flowOf(AuthResult.Loading)
        every { changePasswordUseCase("old", "new") } returns flowOf(AuthResult.Success(Unit))

        viewModel = ProfileViewModel(getUserDataUseCase, logoutUseCase, changePasswordUseCase)

        viewModel.changePassword("old", "new")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(AuthResult.Success(Unit), state.changePasswordResult)
        }
    }
}
