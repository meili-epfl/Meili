package com.github.epfl.meili.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.facebook.AccessTokenCreator
import com.facebook.Profile
import com.github.epfl.meili.models.User
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

class FacebookAuthenticationServiceTest {
    private lateinit var fauth: FacebookAuthenticationService

    @Before
    fun before() {
        fauth = FacebookAuthenticationService()
    }

    @Test
    fun testA() {
        assert(fauth.getCurrentUser() == null)
    }

    @Test
    fun testB() {
        UiThreadStatement.runOnUiThread {
            val mockProfile = Profile(TEST_ID, "", "", "", TEST_NAME, null)

            fauth.setProfile(mockProfile)
            fauth.setAccessToken(AccessTokenCreator.createToken(listOf()))
            Auth.setAuthenticationService(fauth)

            Auth.signOut()
            Auth.isLoggedIn.value = false
            Auth.email = null
            Auth.name = null
            assertEquals(fauth.getCurrentUser(), User(TEST_ID, TEST_NAME, "", " "))
            fauth.signInIntent()
        }
    }

    companion object {
        private const val TEST_ID = "id"
        private const val TEST_NAME = "Haziz"
    }


}