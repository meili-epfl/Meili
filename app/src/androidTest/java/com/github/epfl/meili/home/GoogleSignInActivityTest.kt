package com.github.epfl.meili.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GoogleSignInActivityTest {
    companion object{
        private const val MOCK_NAME = "Fake Name"
    }

    @get:Rule
    var testRule: ActivityScenarioRule<GoogleSignInActivity?>? = ActivityScenarioRule(
        GoogleSignInActivity::class.java
    )

    @Before
    fun before() {
        runOnUiThread{
            AuthenticationService.signOut()
            AuthenticationService.isLoggedIn.value = false
            AuthenticationService.email = null
            AuthenticationService.name = null
        }
    }

    private fun getGSO(): GoogleSignInOptions {
        return GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                MainApplication.applicationContext().getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()
    }

    @Test
    fun clickOnSignInShouldLaunchIntent() {
        Intents.init()
        onView(withId(R.id.signInButton)).check(matches(isClickable())).perform(click())

        val mGoogleSignInClient = GoogleSignIn.getClient(
            MainApplication.applicationContext(),
            getGSO()
        )
        Intents.intended(IntentMatchers.filterEquals(mGoogleSignInClient.signInIntent))
        Intents.release()
    }

    @Test
    fun whenIsLoggedInValuesAreUpdatedInterfaceShouldBeUpdated() {
        runOnUiThread {
            AuthenticationService.name = MOCK_NAME
            AuthenticationService.isLoggedIn.value = true
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText(MOCK_NAME)))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign Out")))
    }

    @Test
    fun whenIsLoggedOutValuesAreUpdatedInterfaceShouldBeUpdated() {
        runOnUiThread {
            AuthenticationService.name = MOCK_NAME
            AuthenticationService.isLoggedIn.value = true
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText(MOCK_NAME)))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign Out")))

        runOnUiThread {
            AuthenticationService.name = null
            AuthenticationService.isLoggedIn.value = false
        }

        onView(withId(R.id.textFieldSignIn)).check(matches(withText("Sign in")))
        onView(withId(R.id.signInButton)).check(matches(withText("Sign In")))
    }

}