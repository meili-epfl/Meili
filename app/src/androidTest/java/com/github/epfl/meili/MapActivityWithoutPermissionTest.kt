package com.github.epfl.meili

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapActivityWithoutPermissionTest {
    private val DENY_BUTTON_INDEX: Int = 2

    @get: Rule
    var testRule = ActivityScenarioRule(MapActivity::class.java)

    @Test
    fun mapActivityWithoutPermissionTest() {
        Thread.sleep(5000)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val device = UiDevice.getInstance(getInstrumentation())
                val button = device.findObject(
                    UiSelector()
                        .clickable(true)
                        .checkable(false)
                        .index(DENY_BUTTON_INDEX)
                )
                Log.e("button is ${button.text}", "button is ${button.text}")
                button.click()
            }
        } catch (e: UiObjectNotFoundException) {
            Log.e("There is no permissions dialog to interact with","There is no permissions dialog to interact with")
        }
    }
}