package com.github.epfl.meili.photo

import android.app.Activity
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.epfl.meili.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoCropActivityTest {

    @get:Rule
    var testRule: ActivityScenarioRule<CameraActivity> =
        ActivityScenarioRule(CameraActivity::class.java)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Before
    fun gotoPhotoCrop() {
        Thread.sleep(1500)  // Camera is slow to start and sometimes fails tests
        onView(withId(R.id.camera_capture_button)).perform(click())
        Thread.sleep(1500)
    }
    
    @Test
    fun rotate90Click() {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }

        val photoView = currentActivity!!.findViewById<ImageView>(R.id.photo_edit_image_view)
        val drawable = photoView.drawable

        // Check rotates
        onView(withId(R.id.rotate_90)).perform(click())
        assert(photoView.rotation == 90f)
        onView(withId(R.id.rotate_90)).perform(click())
        assert(photoView.rotation == 180f)

        // Check the image hasn't changed
        assert(photoView.drawable == drawable)
    }

    @Test
    fun cropModeButtonClick() {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }

        val photoView = currentActivity!!.findViewById<ImageView>(R.id.photo_edit_image_view)
        val drawable = photoView.drawable

        onView(withId(R.id.crop_mode_button)).perform(click())
        onView(withId(R.id.crop_mode_button)).perform(click())

        // Check the image hasn't changed
        assert(photoView.drawable == drawable)
    }

    @Test
    fun cropClick() {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }

        val photoView = currentActivity!!.findViewById<ImageView>(R.id.photo_edit_image_view)
        val drawable = photoView.drawable

        onView(withId(R.id.crop_mode_button)).perform(click())
        onView(withId(R.id.crop)).perform(click())

        // Check the image has changed
        assert(photoView.drawable != drawable)
        assert(photoView.drawable.intrinsicWidth < drawable.intrinsicWidth
                || photoView.drawable.intrinsicHeight < drawable.intrinsicHeight)
    }

    @Test
    fun effectsClick() {
        onView(withId(R.id.effects)).perform(click())
    }
}