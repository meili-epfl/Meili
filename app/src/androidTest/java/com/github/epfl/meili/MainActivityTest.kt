package com.github.epfl.meili

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
    fun clickingOnSignInViewButtonShouldLaunchIntent(){
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.launchSignInView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        Intents.intended(toPackage("com.github.epfl.meili"))
        Intents.release()
    }
}