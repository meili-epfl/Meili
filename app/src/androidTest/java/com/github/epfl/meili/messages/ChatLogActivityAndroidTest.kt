package com.github.epfl.meili.messages


import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.auth.AuthenticationService
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.models.User
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

    private val mockPath = "POI/mock-poi"
    private val fakeMessage = "fake_text"
    private val fakeId = "fake_id"
    private val fakeName = "fake_name_sender"
    private val fakePoi: PointOfInterest =
        PointOfInterest(10.0, 10.0, "fake_poi", "fake_poi")


    private fun getIntent(): Intent {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(targetContext, ChatLogActivity::class.java).apply {
            putExtra("POI_KEY", fakePoi)
        }

        UiThreadStatement.runOnUiThread {
            val mockAuth = Mockito.mock(AuthenticationService::class.java)

            Mockito.`when`(mockAuth.getCurrentUser())
                .thenReturn(User("fake_uid", "fake_name", "fake_email", " "))

            Mockito.`when`(mockAuth.signInIntent(null)).thenReturn(intent)
            Auth.setAuthenticationService(mockAuth)
        }

        return intent
    }

    private val intent = getIntent()

    @get:Rule
    var mActivityTestRule: ActivityScenarioRule<ChatLogActivity> = ActivityScenarioRule(intent)


    @Before
    fun init() {
        UiThreadStatement.runOnUiThread {
            ChatMessageViewModel.setMessageDatabase(MockMessageDatabase(mockPath))
            ChatMessageViewModel.addMessage(fakeMessage, fakeId, fakeId, 10, fakeName)
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

        appCompatEditText3.perform(replaceText(fakeMessage))

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
            mActivityTestRule.scenario.onActivity { a ->
                a.verifyAndUpdateUserIsLoggedIn(true)
                assertThat(a.supportActionBar?.title, `is`("fake_poi"))
            }


        }
    }

    @Test
    fun verifyAndUpdateUserIsLoggedInFalse() {
        pressKey(KeyEvent.KEYCODE_ESCAPE)
        UiThreadStatement.runOnUiThread {
            mActivityTestRule.scenario.onActivity { a ->
                a.verifyAndUpdateUserIsLoggedIn(false)
                assertThat(a.supportActionBar?.title, `is`("Not Signed In"))
            }
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
