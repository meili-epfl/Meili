package com.github.epfl.meili.messages


import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.home.AuthenticationService
import com.github.epfl.meili.models.User
import com.github.epfl.meili.poi.PointOfInterest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito


@LargeTest
class ChatLogActivityAndroidTest {

    private val MOCK_PATH = "POI/mock-poi"
    private val fake_message = "fake_text"
    private val fake_id = "fake_id"
    private val fake_name = "fake_name_sender"
    private val fake_poi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "fake_poi", "fake_poi")
    private val MOCK_EMAIL = "moderator2@gmail.com"
    private val MOCK_PASSWORD = "123123"

    @get:Rule
    val mActivityTestRule: ActivityTestRule<ChatLogActivity> =
        object : ActivityTestRule<ChatLogActivity>(ChatLogActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

                val intent = Intent(targetContext, ChatLogActivity::class.java).apply {
                    putExtra("POI_KEY", fake_poi)
                }

                UiThreadStatement.runOnUiThread {
                    val mockAuth = Mockito.mock(AuthenticationService::class.java)

                    Mockito.`when`(mockAuth.getCurrentUser())
                        .thenReturn(User("fake_uid", "fake_name", "fake_email"))

                    Mockito.`when`(mockAuth.signInIntent()).thenReturn(intent)
                    Auth.setAuthenticationService(mockAuth)
                }

                return intent

            }
        }


    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(MOCK_PATH))
            ChatMessageViewModel.addMessage(fake_message, fake_id, fake_id, 10, fake_name)
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

        appCompatEditText3.perform(replaceText(fake_message))

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

    @Test
    fun verifyAndUpdateUserIsLoggedInTrue() {
        pressKey(KeyEvent.KEYCODE_ESCAPE)
        UiThreadStatement.runOnUiThread {
            mActivityTestRule.activity.verifyAndUpdateUserIsLoggedIn(true)
            assertThat(mActivityTestRule.activity.supportActionBar?.title, `is`("fake_poi"))
        }
    }

    @Test
    fun verifyAndUpdateUserIsLoggedInFalse() {
        pressKey(KeyEvent.KEYCODE_ESCAPE)
        UiThreadStatement.runOnUiThread {
            mActivityTestRule.activity.verifyAndUpdateUserIsLoggedIn(false)
            assertThat(mActivityTestRule.activity.supportActionBar?.title, `is`("Not Signed In"))
        }

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
