package com.github.epfl.meili.photo


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.R
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
        onView(withId(R.id.paint_mode_button)).perform(click())
        onView(withId(R.id.paint_mode_button)).perform(click())
    }

    @Test
    fun undoButtonClick() {
        onView(withId(R.id.undo)).perform(click())
    }

    @Test
    fun redoButtonClick() {
        onView(withId(R.id.redo)).perform(click())
    }

    @Test
    fun filtersClick() {
        onView(withId(R.id.filters)).perform(click())
        onView(withId(R.id.fish_eye)).perform(click())
        onView(withId(R.id.sharpen)).perform(click())
        onView(withId(R.id.bw)).perform(click())
        onView(withId(R.id.sepia)).perform(click())
        onView(withId(R.id.saturate)).perform(click())
        onView(withId(R.id.filters)).perform(click())
    }

    @Test
    fun addTextClick(){
        onView(withId(R.id.`@+id/text_button`)).perform(click())
        onView(withId(R.id.button_addText)).perform(click())
        onView(withId(R.id.`@+id/text_button`)).perform(click())
    }

    @Test
    fun fabClick() {
        onView(withId(R.id.fab)).perform(click())
    }

    @Test
    fun emojisClick() {
        onView(withId(R.id.emojis)).perform(click())
        onView(withText("\uD83D\uDE04")).perform(click())
    }
}