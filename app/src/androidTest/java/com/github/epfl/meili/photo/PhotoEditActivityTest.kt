package com.github.epfl.meili.photo


import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.github.epfl.meili.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class PhotoEditActivityTest {

    private val intent = Intent(getInstrumentation().targetContext.applicationContext, PhotoEditActivity::class.java)
            .putExtra(CameraActivity.URI_KEY, Uri.EMPTY)

    @get:Rule
    var testRule: ActivityScenarioRule<PhotoEditActivity> = ActivityScenarioRule(intent)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

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
        onView(withId(R.id.filters)).perform(click())
    }

    @Test
    fun fabClick() {
        onView(withId(R.id.fab)).perform(click())
    }
}