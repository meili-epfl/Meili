package com.github.epfl.meili.home

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facebook.FacebookActivity
import com.facebook.login.LoginClientCreator
import com.github.epfl.meili.R
import com.github.epfl.meili.profile.ProfileActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FacebookLoginTest {

    @get:Rule
    var testRule = ActivityScenarioRule(ProfileActivity::class.java)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Test
    fun signupWithFacebook() {
        val resultData = Intent()
        resultData.putExtra("com.facebook.LoginFragment:Result", LoginClientCreator.createResult())
        Intents.intending(IntentMatchers.hasComponent(FacebookActivity::class.java.name))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))
        Espresso.onView(ViewMatchers.withId(R.id.facebook_sign_in))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())
    }
}