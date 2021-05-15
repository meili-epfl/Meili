package com.github.epfl.meili.auth

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class FirebaseAuthenticationServiceTest {
    private lateinit var fauth: FirebaseAuthenticationService

    @get:Rule
    var testRule: ActivityScenarioRule<GoogleSignInActivity?>? = ActivityScenarioRule(
        GoogleSignInActivity::class.java
    )

    @Before
    fun before() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            fauth = FirebaseAuthenticationService()
            Auth.setAuthenticationService(fauth)

            Auth.signOut()
            Auth.isLoggedIn.value = false
            Auth.email = null
            Auth.name = null
        }
    }

    @Test
    fun getCurrentUserNullTest(){
        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.currentUser).thenReturn(null)

        fauth.setAuth(mockAuth)

        assert(fauth.getCurrentUser() == null)
    }

    @Test
    fun getCurrentUserPresentTest(){
        val user = User("fake_id", "fake_name", "fake_email", " ")
        val mockAuth = mock(FirebaseAuth::class.java)
        val mockUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn(user.uid)
        `when`(mockUser.email).thenReturn(user.email)
        `when`(mockUser.displayName).thenReturn(user.username)
        `when`(mockAuth.currentUser).thenReturn(mockUser)

        fauth.setAuth(mockAuth)

        assertEquals(fauth.getCurrentUser(), user)
    }

    @Test
    fun onActivityResultWrongRequestCode(){
        testRule!!.scenario.onActivity {
            fauth.onActivityResult(it!!, 0, 0, null){}
        }
    }

    @Test
    fun onActivityResultCorrectRequestCode(){
        testRule!!.scenario.onActivity {
            fauth.onActivityResult(it!!, 9001, 0, null){}
        }
    }
}