package com.github.epfl.meili.photo


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoEditorActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA"
        )

    @Test
    fun test() {
        val materialButton = onView(
            allOf(
                withId(R.id.launchCameraView), withText("Launch Camera View"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.camera_capture_button), withContentDescription("Take photo"),
                childAtPosition(
                    allOf(
                        withId(R.id.camera_container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val materialTextView = onView(
            allOf(
                withId(R.id.filters), withText("Filters"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val materialTextView2 = onView(
            allOf(
                withId(R.id.bw), withText("None"),
                childAtPosition(
                    allOf(
                        withId(R.id.filters_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView2.perform(click())

        val materialTextView3 = onView(
            allOf(
                withId(R.id.sepia), withText("Sepia"),
                childAtPosition(
                    allOf(
                        withId(R.id.filters_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialTextView3.perform(click())

        val materialTextView4 = onView(
            allOf(
                withId(R.id.saturate), withText("Saturate"),
                childAtPosition(
                    allOf(
                        withId(R.id.filters_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView4.perform(click())

        val materialTextView5 = onView(
            allOf(
                withId(R.id.sharpen), withText("Sharpen"),
                childAtPosition(
                    allOf(
                        withId(R.id.filters_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialTextView5.perform(click())

        val materialTextView6 = onView(
            allOf(
                withId(R.id.fish_eye), withText("Fish Eye"),
                childAtPosition(
                    allOf(
                        withId(R.id.filters_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            3
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialTextView6.perform(click())

        val materialTextView7 = onView(
            allOf(
                withId(R.id.undo), withText("Undo"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        materialTextView7.perform(click())

        val materialTextView8 = onView(
            allOf(
                withId(R.id.filters), withText("Filters"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialTextView8.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.paint_mode_button), withContentDescription("Draw on image"),
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
        appCompatImageButton2.perform(click())

        val materialTextView9 = onView(
            allOf(
                withId(R.id.undo), withText("Undo"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        materialTextView9.perform(click())

        val materialTextView10 = onView(
            allOf(
                withId(R.id.redo), withText("Redo"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        materialTextView10.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.paint_mode_button), withContentDescription("Draw on image"),
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
        appCompatImageButton3.perform(click())

        val materialTextView11 = onView(
            allOf(
                withId(R.id.show), withText("Show preview"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        materialTextView11.perform(click())

        val materialTextView12 = onView(
            allOf(
                withId(R.id.hide), withText("Hide preview"),
                childAtPosition(
                    allOf(
                        withId(R.id.preview_container),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            5
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialTextView12.perform(click())
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
