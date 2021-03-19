package com.github.epfl.meili.messages


import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.epfl.meili.ChatLogActivity
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@LargeTest
class ChatLogActivityAndroidTest {

    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message = "fake_text"
    private val fake_id = "fake_id"
    private val fake_poi : PointOfInterest = PointOfInterest(LatLng(10.0,10.0), "fake_poi", "fake_poi")
    private val MOCK_EMAIL = "moderator2@gmail.com"
    private val MOCK_PASSWORD = "123123"

    @get:Rule
    val mActivityTestRule: ActivityTestRule<ChatLogActivity> =
        object : ActivityTestRule<ChatLogActivity>(ChatLogActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                return Intent(targetContext, ChatLogActivity::class.java).apply {
                    putExtra("POI_KEY", fake_poi)
                }
            }
        }


    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.database = MockMessageDatabase(MOCK_PATH)
            ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10)
            Firebase.auth.signInWithEmailAndPassword(MOCK_EMAIL, MOCK_PASSWORD)
            Thread.sleep(5000)
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

        appCompatEditText3.perform(replaceText(fake_message), closeSoftKeyboard())

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
