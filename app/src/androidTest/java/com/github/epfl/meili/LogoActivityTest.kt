package com.github.epfl.meili

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogoActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(LogoActivity::class.java)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun launchingTriggersIntent() {
        Thread.sleep(1000) // logo activity purposefully delays a little
        Intents.intended(IntentMatchers.isInternal())
    }
}