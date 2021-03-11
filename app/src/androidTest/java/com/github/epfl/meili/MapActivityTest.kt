package com.github.epfl.meili

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class MapActivityTest {

    @get: Rule
    var testRule = ActivityScenarioRule(MapActivity::class.java)

    private fun assertViewWithTextIsVisible(device: UiDevice, text: String) {
        val allowButton = device.findObject(UiSelector().textContains(text))
        if (!allowButton.exists()) {
            throw AssertionError("View with text <$text> not found!")
        }
    }

    @Throws(UiObjectNotFoundException::class)
    fun reactToPermission(device: UiDevice, text: String) {
        device.findObject(UiSelector().textContains(text)).click()
    }

//    @Test
//    fun a_shouldDisplayPermissionRequestDialogAtStartup() {
//        val device = UiDevice.getInstance(getInstrumentation());
//        Thread.sleep(2000)
//        assertViewWithTextIsVisible(device, "ALLOW")
//        assertViewWithTextIsVisible(device, "DENY")
//
//        // cleanup for the next test
//        reactToPermission(device, "DENY")
//    }

    @Test
    fun b_locationButtonClickableAfterPermissionGrant() {
        PermissionGranter.allowPermissionsIfNeeded("android.permissions.ACCESS_FINE_LOCATION")
        Thread.sleep(2000)
        val imageView = onView(
            allOf(
                withContentDescription("My Location"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageView.perform(click())
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