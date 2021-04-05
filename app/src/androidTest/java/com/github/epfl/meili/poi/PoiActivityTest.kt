package com.github.epfl.meili.poi


import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.epfl.meili.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class PoiActivityTest {
    private val fake_poi: PointOfInterest =
        PointOfInterest(LatLng(10.0, 10.0), "ChIJAAAAAAAAAAARg4pb6XR5bo0", "art_brut")

    @get:Rule
    val mActivityTestRule: ActivityTestRule<PoiActivity> =
        object : ActivityTestRule<PoiActivity>(PoiActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                return Intent(targetContext, PoiActivity::class.java).apply {
                    putExtra("POI_KEY", fake_poi)
                }
            }
        }

    @Test
    fun poiActivityTest() {
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeLeft());
        pressBack()
        pressBack()
    }
}
