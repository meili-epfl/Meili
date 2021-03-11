package com.github.epfl.meili.messages


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.github.epfl.meili.LatestMessagesActivity
import com.github.epfl.meili.NewMessageActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.registerlogin.LoginActivity
import com.github.epfl.meili.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class LatestMessageAutomaticRedirectionAndroidTest {

    private val TEST_EMAIL: String = "moderator1@gmail.com"
    private val TEST_PASSWORD: String = "123123"




    @get: Rule
    var testRule: ActivityScenarioRule<LatestMessagesActivity> =
            ActivityScenarioRule(LatestMessagesActivity::class.java)
    @Before
    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun notSignInReturns(){
        Thread.sleep(5000)
    }

}
