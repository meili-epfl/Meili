package com.github.epfl.meili

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.schibsted.spain.barista.interaction.PermissionGranter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MapActivityWithPermissionTest {

    @get: Rule
    var testRule = ActivityScenarioRule(MapActivity::class.java)

//    @get: Rule
//    var permissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION")

    @Test
    fun mapActivityWithPermissionTest() {
        PermissionGranter.allowPermissionsIfNeeded("android.permission.ACCESS_FINE_LOCATION")
        Thread.sleep(5000)
        val imageView = onView(
                allOf(withContentDescription("My Location"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.FrameLayout")),
                                        2),
                                0),
                        isDisplayed()))
        imageView.perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {
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