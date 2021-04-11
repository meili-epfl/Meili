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
/*
    private val TEST_EMAIL: String = "moderator1@gmail.com"
    private val TEST_PASSWORD: String = "123123"


    @get: Rule
    var testRule: ActivityScenarioRule<LoginActivity> =
        ActivityScenarioRule(LoginActivity::class.java)


    @Before
    fun setupLatestMessageActivity() {
        // Type text and then press the button.
        onView(withId(R.id.email_edittext_login)).perform(
            clearText(),
            typeText(TEST_EMAIL),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_edittext_login)).perform(
            clearText(),
            typeText(TEST_PASSWORD),
            closeSoftKeyboard()
        )
        onView(withId(R.id.login_button)).perform(click())

        Thread.sleep(10000)
    }


//    @Test
//    fun signOutButtonGoesBackToRegister() {
//
//        Intents.init()
//
//        onView(withId(R.id.menu_sign_out)).perform(click())
//        Intents.intended(IntentMatchers.hasComponent(RegisterActivity::class.java.name))
//        Intents.release()
//    }
//
//    @Test
//    fun newMessageButtonShowsNewMessageActivity() {
//
//        Intents.init()
//
//        onView(withId(R.id.menu_new_message)).perform(click())
//        Intents.intended(IntentMatchers.hasComponent(NewMessageActivity::class.java.name))
//        Intents.release()
//    }

    @After
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Thread.sleep(2000)
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
 */
}
