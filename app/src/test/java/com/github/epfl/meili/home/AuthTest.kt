package com.github.epfl.meili.home


import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Objects.isNull


class AuthTest {

    private lateinit var mockService: MockAuthenticationService

    companion object {
        private const val MOCK_NAME = "Fake Name"
        private const val MOCK_EMAIL = "Fake Email"
    }

    @Before
    fun before() {
        //Injecting authentication Service
        mockService = MockAuthenticationService()
        Auth.setAuthenticationService(mockService)
    }

    @Test
    fun updateUserDataTestWhenUserPresent() {
        mockService.MOCK_NAME = MOCK_NAME
        mockService.MOCK_EMAIL = MOCK_EMAIL

        Auth.updateUserData()

        val user = Auth.getCurrentUser()!!
        assertEquals(user.name, MOCK_NAME)
        assertEquals(user.email, MOCK_EMAIL)
        assertEquals(Auth.isLoggedIn.value, true)
    }

    @Test
    fun updateUserDataTestWhenUserNotPresent() {
        mockService.MOCK_NAME = "null"

        Auth.updateUserData()

        val user = Auth.getCurrentUser()
        assertEquals(true, isNull(user))
        assertEquals(Auth.isLoggedIn.value, false)
    }
}