package com.github.epfl.meili.forum

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.home.MockAuthenticationService
import com.github.epfl.meili.registerlogin.LoginActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumTest {

    private val TEST_TEXT = "test text"
    private val TEST_TITLE = "test title"
    private val TEST_USERNAME = "test_username"
    private val TEST_EMAIL = "test@meili.com"

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initializeMockDatabase() {
        UiThreadStatement.runOnUiThread {
            val mockPostService = MockPostService()

            ForumViewModel.setService(mockPostService)
            PostViewModel.setService(mockPostService)
            NewPostViewModel.setService(mockPostService)
        }
    }

    @Before
    fun initiateAuthAndService() {
        UiThreadStatement.runOnUiThread {
            //Injecting authentication Service
            val mockAuthService = MockAuthenticationService()
            Auth.setAuthenticationService(mockAuthService)

            Auth.isLoggedIn.value = true
            Auth.email = TEST_EMAIL
            Auth.name = TEST_USERNAME
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
    fun addPostToForumTest() {
        // Press Forum button
        Espresso.onView(ViewMatchers.withId(R.id.launchForumView))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        // Press + button
        Espresso.onView(ViewMatchers.withId(R.id.forum_new_post_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        // Type test title
        Espresso.onView(ViewMatchers.withId(R.id.new_post_title)).perform(
            ViewActions.typeText(TEST_TITLE),
            ViewActions.closeSoftKeyboard()
        )

        // Type test text
        Espresso.onView(ViewMatchers.withId(R.id.new_post_text)).perform(
            ViewActions.typeText(TEST_TEXT),
            ViewActions.closeSoftKeyboard()
        )

        // Press Create Post button
        Espresso.onView(ViewMatchers.withId(R.id.new_post_create_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable())).perform(ViewActions.click())

        val post = Espresso.onView(
            allOf(
                childAtPosition(
                    withText(TEST_TITLE),
                    0
                ),
                isDisplayed()
            ))
        post.perform(click())
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