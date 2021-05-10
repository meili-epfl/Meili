package com.github.epfl.meili.photo


import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.github.epfl.meili.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(CameraActivity::class.java)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant("android.permission.CAMERA")

    @Before
    fun waitForCameraToStart() {
        Thread.sleep(1500)  // Camera is slow to start and sometimes fails tests
    }

    @Throws(UiObjectNotFoundException::class)
    fun reactToPermission(device: UiDevice, text: String) {
        device.findObject(UiSelector().textContains(text)).click()
    }

    @Test
    fun takePhotoWithCameraButtonTest() {
        val appCompatImageButton = onView(
                allOf(
                        withId(R.id.camera_switch_button), withContentDescription("Switch camera"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.camera_container),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0
                                        )
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton.perform(click())

        onView(withId(R.id.camera_capture_button))
                .check(ViewAssertions.matches(isClickable())).perform(click())
    }

    @Test
    fun takePhotoWithVolumeDownTest() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
                .pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN)
    }

    @Test
    fun touchTest() {
        val touches = object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayed()
            }

            override fun getDescription(): String {
                return "touch"
            }

            override fun perform(uiController: UiController, view: View) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)

                val down = MotionEvents.sendDown(
                        uiController,
                        floatArrayOf(10f + location[0], 10f + location[1]),
                        floatArrayOf(1f, 1f)
                ).down
                uiController.loopMainThreadForAtLeast(200)
                MotionEvents.sendUp(
                        uiController,
                        down,
                        floatArrayOf(10f + location[0], 10f + location[1])
                )
            }
        }

        //onView(withId(R.id.camera_preview)).perform(touches)
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


