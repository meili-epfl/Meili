package com.github.epfl.meili.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.facebook.AccessTokenCreator
import com.facebook.Profile
import com.github.epfl.meili.models.User
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FacebookAuthenticationServiceTest {
    private lateinit var fauth: FacebookAuthenticationService

    @Test
    fun test() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            val mockProfile = Profile("id", "", "", "", "Haziz", null)


            fauth = FacebookAuthenticationService()
            assert(fauth.getCurrentUser() == null)
            fauth.setProfile(mockProfile)
            fauth.setAccessToken(AccessTokenCreator.createToken(listOf()))
            Auth.setAuthenticationService(fauth)

            Auth.signOut()
            Auth.isLoggedIn.value = false
            Auth.email = null
            Auth.name = null
            assertEquals(fauth.getCurrentUser(), User("id", "Haziz", "", " "))
            fauth.signInIntent()

        }
    }


}