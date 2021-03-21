package com.github.epfl.meili.registerlogin


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.github.epfl.meili.LatestMessagesActivity
import com.github.epfl.meili.R
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
class LoginActivityAndroidTest {

    private val TEST_EMAIL: String = "moderator1@gmail.com"
    private val TEST_PASSWORD: String = "123123"
    private val TEST_BAD_EMAIL: String = "mods"
    private val TEST_BAD_PASSWORD: String = "123"

    @get: Rule
    var testRule: ActivityScenarioRule<LoginActivity> =
        ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup(){
        CustomAuthentication.setAuthenticationService(CustomMockAuthenticationService())
    }

    @Test
    fun textFieldsAreWritable() {

        val appCompatEditText = onView(
            allOf(
                withId(R.id.email_edittext_login),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText(TEST_EMAIL), closeSoftKeyboard())

        val editText = onView(
            allOf(
                withId(R.id.email_edittext_login), withText(TEST_EMAIL),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText.check(matches(withText(TEST_EMAIL)))

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.password_edittext_login),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText(TEST_PASSWORD), closeSoftKeyboard())

        val editText2 = onView(
            allOf(
                withId(R.id.password_edittext_login), withText("••••••"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("••••••")))
    }

    @Test
    fun loginButtonSendsIntent() {
        Intents.init()
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


        Intents.intended(hasComponent(LatestMessagesActivity::class.java.name))
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
