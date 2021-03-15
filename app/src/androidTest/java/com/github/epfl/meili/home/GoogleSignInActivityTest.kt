package com.github.epfl.meili.home

import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.epfl.meili.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GoogleSignInActivityTest {
    private lateinit var mockService: MockAuthenticationService

    companion object {
        private const val MOCK_NAME = "Fake Name"
    }

    @get:Rule
    var testRule: ActivityScenarioRule<GoogleSignInActivity?>? = ActivityScenarioRule(
            GoogleSignInActivity::class.java
    )

    @Before
    fun initiateAuthAndService() {
        runOnUiThread {
            //Injecting authentication Service
            mockService = MockAuthenticationService()
            Auth.setAuthenticationService(mockService)

            Auth.signOut()
            Auth.isLoggedIn.value = false
            Auth.email = null
            Auth.name = null
        }
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }


    @Test
    fun clickOnButtonShouldSignInIfUserNull() {
        onView(withId(R.id.signInButton))
                .check(matches(isClickable())).perform(click())

        //setting name to null makes currentUser return null and button will sign in
        mockService.mock_name = "null"

        val mockIntent = mockService.signInIntent()

        IntentMatchers.filterEquals(mockIntent)
    }

    @Test
    fun whenIsLoggedInValuesAreUpdatedInterfaceShouldBeUpdated() {
        runOnUiThread {
            Auth.name = MOCK_NAME
            Auth.isLoggedIn.value = true
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText(MOCK_NAME)))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign Out")))
    }

    @Test
    fun whenIsLoggedOutValuesAreUpdatedInterfaceShouldBeUpdated() {
        runOnUiThread {
            Auth.name = MOCK_NAME
            Auth.isLoggedIn.value = true
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText(MOCK_NAME)))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign Out")))

        runOnUiThread {
            Auth.name = null
            Auth.isLoggedIn.value = false
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText("Sign in")))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign In")))
    }

    @Test
    fun onActivityResultTest() {
        onView(withId(R.id.signInButton)).check(matches(isClickable())).perform(click())

        val resultData = Intent()
        resultData.putExtra("name", MOCK_NAME)
        val result = Instrumentation.ActivityResult(9001, resultData)

        val mockIntent = mockService.signInIntent()

        intending(IntentMatchers.filterEquals(mockIntent)).respondWith(result)
    }
}