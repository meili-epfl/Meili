package com.github.epfl.meili.photo


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.R
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoEditActivityTest {

    @get:Rule
    var testRule: ActivityScenarioRule<CameraActivity> = ActivityScenarioRule(CameraActivity::class.java)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Before
    fun gotoPhotoEdit() {
        Thread.sleep(1500)  // Camera is slow to start and sometimes fails tests
        onView(withId(R.id.camera_capture_button)).perform(click())
        Thread.sleep(1500)
        onView(withId(R.id.effects)).perform(click())
        Thread.sleep(1500)
    }

    @Test
    fun paintButtonClick() {
        onView(withId(R.id.colorSlider)).check(matches(not(isDisplayed())))
        onView(withId(R.id.paint_mode_button)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.colorSlider)).check(matches(isDisplayed()))
        onView(withId(R.id.paint_mode_button)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.colorSlider)).check(matches(not(isDisplayed())))
    }

    @Test
    fun undoButtonClick() {
        onView(withId(R.id.undo)).check(matches(isClickable())).perform(click())
    }

    @Test
    fun redoButtonClick() {
        onView(withId(R.id.redo)).check(matches(isClickable())).perform(click())
    }

    @Test
    fun filtersClick() {
        onView(withId(R.id.fish_eye)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sharpen)).check(matches(not(isDisplayed())))
        onView(withId(R.id.bw)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sepia)).check(matches(not(isDisplayed())))
        onView(withId(R.id.saturate)).check(matches(not(isDisplayed())))

        onView(withId(R.id.filters)).check(matches(isClickable())).perform(click())

        onView(withId(R.id.fish_eye)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.sharpen)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.bw)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.sepia)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.saturate)).check(matches(isClickable())).perform(click())

        onView(withId(R.id.filters)).check(matches(isClickable())).perform(click())

        onView(withId(R.id.fish_eye)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sharpen)).check(matches(not(isDisplayed())))
        onView(withId(R.id.bw)).check(matches(not(isDisplayed())))
        onView(withId(R.id.sepia)).check(matches(not(isDisplayed())))
        onView(withId(R.id.saturate)).check(matches(not(isDisplayed())))
    }

    @Test
    fun addTextClick(){
        onView(withId(R.id.text_button)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.add_text)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.text_button)).check(matches(isClickable())).perform(click())
    }

    @Test
    fun fabClick() {
        onView(withId(R.id.finish_edit)).check(matches(isClickable())).perform(click())
    }

    @Test
    fun emojisClick() {
        onView(withId(R.id.emojis)).check(matches(isClickable())).perform(click())
        onView(withText("\uD83D\uDE04")).check(matches(isClickable())).perform(click())
    }
}