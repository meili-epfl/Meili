package com.github.epfl.meili.poi


import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class PoiActivityTest {

    @get: Rule
    var testRule = ActivityScenarioRule(PoiActivity::class.java)

    @Test
    fun poiActivityTest() {
    //TODO Test that poi activity opens when poi is clicked on map and that correct info/posts/chat are displayed
    }

    @Test
    fun pressBackOnPoi(){
        pressBack()
    }
}
