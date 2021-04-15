package com.github.epfl.meili.photo


import android.content.pm.ActivityInfo
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
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.github.epfl.meili.R
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CameraActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA"
        )

    @Throws(UiObjectNotFoundException::class)
    fun reactToPermission(device: UiDevice, text: String) {
        device.findObject(UiSelector().textContains(text)).click()
    }

    @Test
    fun cameraActivityTest() {
        PermissionGranter.allowPermissionsIfNeeded("android.permission.CAMERA")
        PermissionGranter.allowPermissionsIfNeeded("android.permissions.CAMERA")
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
        Thread.sleep(2000)

        var appCompatImageButton2 = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())

        val mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        mDevice.setOrientationLeft()
        mDevice.setOrientationNatural()
        mDevice.setOrientationRight()

        Thread.sleep(2000)

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

        onView(withId(R.id.camera_preview)).perform(touches)

        Thread.sleep(2000)

        mDevice.pressKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN)
        Thread.sleep(2000)


        appCompatImageButton2 = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())


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

