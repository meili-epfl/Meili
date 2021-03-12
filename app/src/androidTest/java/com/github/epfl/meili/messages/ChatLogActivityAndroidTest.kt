package com.github.epfl.meili.messages


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.github.epfl.meili.R
import com.github.epfl.meili.registerlogin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class ChatLogActivityAndroidTest {

    private val TEST_EMAIL: String = "moderator1@gmail.com"
    private val TEST_PASSWORD: String = "123123"
    private val TEST_MESSAGE: String = "Hello!"


    @get: Rule
    var testRule: ActivityScenarioRule<LoginActivity> =
        ActivityScenarioRule(LoginActivity::class.java)


    @Before
    fun setupChatLogActivity() {
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

        Thread.sleep(5000)

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.menu_new_message), withText("New Message"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        Thread.sleep(10000)

        val recyclerView = onView(
            allOf(
                withId(R.id.recyclerview_newmessage),
                childAtPosition(
                    withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

    }

    @Test
    fun buttonSendsWrittenMessage() {

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.edit_text_chat_log),
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
        appCompatEditText3.perform(replaceText(TEST_MESSAGE), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.button_chat_log), withText("Send"),
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
        materialButton2.perform(click())
    }

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
}
