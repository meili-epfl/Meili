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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class LatestMessageActivityAndroidTest {

    private val TEST_EMAIL: String = "moderator1@gmail.com"
    private val TEST_PASSWORD: String = "123123"


    @get: Rule
    var testRule: ActivityScenarioRule<LatestMessagesActivity> =
            ActivityScenarioRule(LatestMessagesActivity::class.java)


    @Before
    @After
    fun login(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)
        Thread.sleep(5000)
    }


    @Test
    fun signOutButtonGoesBackToRegister() {

        Intents.init()

        val actionMenuItemView = onView(
                allOf(
                        withId(R.id.menu_sign_out), withText("Sign out"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        actionMenuItemView.perform(click())
        Intents.intended(IntentMatchers.hasComponent(RegisterActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun newMessageButtonShowsNewMessageActivity() {

        Intents.init()

        val actionMenuItemView = onView(
                allOf(withId(R.id.menu_new_message), withText("New Message"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()))
        actionMenuItemView.perform(click())
        Intents.intended(IntentMatchers.hasComponent(NewMessageActivity::class.java.name))
        Intents.release()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
