package com.github.epfl.meili.photo


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoEditActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(CameraActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA"
        )

    @Test
    fun photoEditorActivityTest() {
        val appCompatImageButton = onView(
            allOf(
                withId(R.id.camera_capture_button)
            )
        )
        appCompatImageButton.check(ViewAssertions.matches(isDisplayed()))
        appCompatImageButton.perform(click())

        Thread.sleep(1000)

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.paint_mode_button)
            )
        )
        appCompatImageButton2.check(ViewAssertions.matches(isDisplayed()))
        appCompatImageButton2.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.paint_mode_button)
            )
        )
        appCompatImageButton3.check(ViewAssertions.matches(isDisplayed()))
        appCompatImageButton3.perform(click())

        val appCompatImageButton4 = onView(
            allOf(
                withId(R.id.undo)
            )
        )
        appCompatImageButton4.check(ViewAssertions.matches(isDisplayed()))
        appCompatImageButton4.perform(click())

        val appCompatImageButton5 = onView(
            allOf(
                withId(R.id.redo)
            )
        )
        appCompatImageButton5.check(ViewAssertions.matches(isDisplayed()))
        appCompatImageButton5.perform(click())

        val materialTextView3 = onView(
            allOf(
                withId(R.id.filters)
            )
        )
        materialTextView3.check(ViewAssertions.matches(isDisplayed()))
        materialTextView3.perform(click())

        val materialTextView4 = onView(
            allOf(
                withId(R.id.saturate)
            )
        )
        materialTextView4.check(ViewAssertions.matches(isDisplayed()))
        materialTextView4.perform(click())

        val materialTextView5 = onView(
            allOf(
                withId(R.id.sepia)
            )
        )
        materialTextView5.check(ViewAssertions.matches(isDisplayed()))
        materialTextView5.perform(click())

        val materialTextView6 = onView(
            allOf(
                withId(R.id.bw)
            )
        )
        materialTextView6.check(ViewAssertions.matches(isDisplayed()))
        materialTextView6.perform(click())

        val materialTextView7 = onView(
            allOf(
                withId(R.id.sharpen)
            )
        )
        materialTextView7.check(ViewAssertions.matches(isDisplayed()))
        materialTextView7.perform(click())

        val materialTextView8 = onView(
            allOf(
                withId(R.id.fish_eye)
            )
        )
        materialTextView8.check(ViewAssertions.matches(isDisplayed()))
        materialTextView8.perform(click())

        val materialTextView9 = onView(
            allOf(
                withId(R.id.filters)
            )
        )
        materialTextView9.check(ViewAssertions.matches(isDisplayed()))
        materialTextView9.perform(click())


        val fab = onView(
            allOf(
                withId(R.id.fab)
            )
        )
        fab.check(ViewAssertions.matches(isDisplayed()))
        fab.perform(click())
    }
}