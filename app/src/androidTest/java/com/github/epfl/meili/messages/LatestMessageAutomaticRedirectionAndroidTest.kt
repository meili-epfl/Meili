package com.github.epfl.meili.messages


import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.github.epfl.meili.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
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
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        Thread.sleep(5000)
    }

    @Test
    fun notSignInReturns() {
        Thread.sleep(5000)
    }

}
