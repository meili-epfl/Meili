package com.github.epfl.meili.photo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoCropActivityTest {

    @get:Rule
    var testRule: ActivityScenarioRule<CameraActivity> = ActivityScenarioRule(CameraActivity::class.java)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Before
    fun gotoPhotoCrop() {
        onView(withId(R.id.camera_capture_button)).perform(click())
        Thread.sleep(1500)
    }

    @Test
    fun rotate90Click() {
        onView(withId(R.id.rotate_90)).perform(click())
    }

    @Test
    fun cropModeButtonClick() {
        onView(withId(R.id.crop_mode_button)).perform(click())
        onView(withId(R.id.crop_mode_button)).perform(click())
    }

    @Test
    fun cropClick() {
        onView(withId(R.id.crop_mode_button)).perform(click())
        onView(withId(R.id.crop)).perform(click())
    }

    @Test
    fun effectsClick() {
        onView(withId(R.id.effects)).perform(click())
    }
}