package com.github.epfl.meili

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.storage.FirebaseStorageService
import com.google.firebase.storage.FirebaseStorage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class PhotoDemoActivityTest {
    @get:Rule
    var testRule: ActivityScenarioRule<PhotoDemoActivity> = ActivityScenarioRule(PhotoDemoActivity::class.java)

    @Before
    fun injectMock() {
        FirebaseStorageService.storageProvider = { mock(FirebaseStorage::class.java) }
    }

    @Test
    fun emptyTest() {

    }
}