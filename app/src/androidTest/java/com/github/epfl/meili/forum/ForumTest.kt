package com.github.epfl.meili.forum

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.google.android.gms.auth.api.Auth
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumTest {

    private lateinit var mockService: PostService

    @Before
    fun initializeMockDatabase() {
        mockService = MockPostService()
    }

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }



}