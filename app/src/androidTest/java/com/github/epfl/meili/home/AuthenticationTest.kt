package com.github.epfl.meili.home

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class AuthenticationTest {
    private lateinit var mockService: MockAuthenticationService
    private val MOCK_NAME = "MOCK_NAME"
    private val MOCK_EMAIL = "MOCK_EMAIL"

    @get:Rule
    var testRule: ActivityScenarioRule<GoogleSignInActivity?>? = ActivityScenarioRule(
        GoogleSignInActivity::class.java
    )

    @Before
    fun before() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            mockService = MockAuthenticationService()
            Auth.setAuthenticationService(mockService)
        }
    }

    @Test
    fun updateUserDataTestWhenUserPresent() {
        mockService.mock_name = MOCK_NAME
        mockService.mock_email = MOCK_EMAIL

        UiThreadStatement.runOnUiThread {
            Auth.updateUserData()
        }

        val user = Auth.getCurrentUser()!!
        Assert.assertEquals(user.name, MOCK_NAME)
        Assert.assertEquals(user.email, MOCK_EMAIL)
        Assert.assertEquals(Auth.isLoggedIn.value, true)
    }

    @Test
    fun updateUserDataTestWhenUserNotPresent() {
        mockService.mock_name = "null"

        UiThreadStatement.runOnUiThread {
            Auth.updateUserData()
        }

        val user = Auth.getCurrentUser()
        Assert.assertEquals(true, Objects.isNull(user))
        Assert.assertEquals(Auth.isLoggedIn.value, false)
    }
}