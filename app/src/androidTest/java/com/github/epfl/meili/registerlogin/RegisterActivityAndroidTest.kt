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
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class RegisterActivityAndroidTest {

    private val TEST_USERNAME: String = "moderator"
    private val TEST_EMAIL: String = "moderator@gmail.com"
    private val TEST_PASSWORD: String = "123123"

    @get: Rule
    var testRule: ActivityScenarioRule<RegisterActivity> =
        ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setup(){
        CustomAuthentication.setAuthenticationService(CustomMockAuthenticationService())
    }

    @Test
    fun textFieldsAreWritable() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.username_edittext_register),
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
        appCompatEditText.perform(replaceText(TEST_USERNAME), closeSoftKeyboard())

        val editText = onView(
            allOf(
                withId(R.id.username_edittext_register), withText(TEST_USERNAME),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText.check(matches(withText(TEST_USERNAME)))

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.email_edittext_register),
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
        appCompatEditText2.perform(replaceText(TEST_EMAIL), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.password_edittext_register),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText(TEST_PASSWORD), closeSoftKeyboard())

        val editText2 = onView(
            allOf(
                withId(R.id.email_edittext_register), withText(TEST_EMAIL),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText(TEST_EMAIL)))

        val editText3 = onView(
            allOf(
                withId(R.id.password_edittext_register), withText("••••••"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText3.check(matches(withText("••••••")))
    }

    @Test
    fun registerButtonSendsIntent() {
        Intents.init()
        // Type text and then press the button.
        onView(withId(R.id.username_edittext_register)).perform(
            clearText(),
            typeText(TEST_USERNAME),
            closeSoftKeyboard()
        )
        onView(withId(R.id.email_edittext_register)).perform(
            clearText(),
            typeText(TEST_EMAIL),
            closeSoftKeyboard()
        )
        onView(withId(R.id.password_edittext_register)).perform(
            clearText(),
            typeText(TEST_PASSWORD),
            closeSoftKeyboard()
        )

        onView(withId(R.id.register_button)).perform(click())

        Intents.intended(hasComponent(LatestMessagesActivity::class.java.name))
        Intents.release()

    }



    @Test
    fun alreadyHaveAnAccount() {
        Intents.init()
        onView(withId(R.id.already_have_account_text_view)).perform(click())
        Intents.intended(hasComponent(LoginActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun backToRegistration() {
        Intents.init()
        onView(withId(R.id.already_have_account_text_view)).perform(click())
        onView(withId(R.id.back_to_registration_text_view)).perform(click())
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
