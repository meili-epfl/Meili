package com.github.epfl.meili

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.storage.MockStorageService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDemoActivityTest {
    @get:Rule
    var testRule: ActivityScenarioRule<PhotoDemoActivity> = ActivityScenarioRule(PhotoDemoActivity::class.java)

    @Before
    fun injectMock() {
        PhotoDemoActivity.storageService = { MockStorageService }
    }

    @Test
    fun emptyTest() {

    }
}