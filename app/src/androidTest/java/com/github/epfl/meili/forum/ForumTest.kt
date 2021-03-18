package com.github.epfl.meili.forum

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumTest {

    private lateinit var mockService: PostService

    @get:Rule
    var testRule = ActivityScenarioRule(ForumActivity::class.java)

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

    @Test
    fun test() {

    }

}