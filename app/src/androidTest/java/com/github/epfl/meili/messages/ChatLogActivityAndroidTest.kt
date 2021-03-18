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
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.github.epfl.meili.ChatLogActivity
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

    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message = "fake_text"

    @get: Rule
    var testRule: ActivityScenarioRule<ChatLogActivity> =
        ActivityScenarioRule(ChatLogActivity::class.java)


    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.database = MockMessageDatabase(MOCK_PATH)
        }
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
        //appCompatEditText3.perform(replaceText(TEST_MESSAGE), closeSoftKeyboard())

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
       // materialButton2.perform(click())
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
