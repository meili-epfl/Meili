package com.github.epfl.meili.home

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
    fun firebaseAuthWithGoogleTest() {
        var fake_id = "1234"
        Espresso.onView(ViewMatchers.withId(R.id.signInButton))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        testRule!!.scenario.onActivity { activity ->

            fauth.firebaseAuthWithGoogle(activity!!, fake_id) { Auth.updateUserData() }

            assert(!Auth.isLoggedIn.value!!)
            assert(Auth.name == null)
            assert(Auth.email == null)
        }
    }
}