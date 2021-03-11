package com.github.epfl.meili

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity?>? = ActivityScenarioRule(
            MainActivity::class.java
    )

    @Test
    fun clickingOnSignInViewButtonShouldLaunchIntent() {
        Intents.init()
        onView(withId(R.id.launchSignInView))
            .check(matches(isClickable())).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
        Intents.release()
    }

    @Test
    fun clickingOnMapButtonShouldLaunchIntent() {
        Intents.init()
        onView(withId(R.id.launchMap))
            .check(matches(isClickable())).perform(click())

        Intents.intended(toPackage("com.github.epfl.meili"))
        Intents.release()
    }

    @Test
    fun testNavigation() {
        PermissionGranter.allowPermissionsIfNeeded("android.permissions.ACCESS_FINE_LOCATION")
        onView(withId(R.id.launchSignInView)).perform(click())
        onView(withId(R.id.signInButton)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.launchMap)).perform(click())
        onView(withId(R.id.map)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.launchSignInView)).check(matches(isClickable())).perform(click())
    }
}