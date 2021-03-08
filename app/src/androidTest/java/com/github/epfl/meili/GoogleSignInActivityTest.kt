package com.github.epfl.meili

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.github.marceltorne.meili.GoogleSignInActivity
import com.nhaarman.mockitokotlin2.whenever
import com.schibsted.spain.barista.interaction.BaristaDrawerInteractions.openDrawer
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class GoogleSignInActivityTest {
    @Test
    fun clickSignInShouldLaunchIntent() {
        runOnUiThread {
            Auth.logout()
            Auth.loggedIn.postValue(false)
        }

        openDrawer()
        onView(withId(R.id.nav_signin_button)).check(matches(isClickable())).perform(click())

        val mGoogleSignInClient = GoogleSignIn.getClient(MainApplication.applicationContext(), getGSO())
        Intents.intended(IntentMatchers.filterEquals(mGoogleSignInClient.signInIntent))
        mUiDevice.pressBack()
    }

}