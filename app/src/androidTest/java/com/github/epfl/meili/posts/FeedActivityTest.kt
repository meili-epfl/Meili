package com.github.epfl.meili.posts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.R
import com.github.epfl.meili.posts.feed.FeedActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedActivityTest {

    @get:Rule
    var rule = ActivityScenarioRule(FeedActivity::class.java)

    @Test
    fun feedRecyclerViewDisplayed() {
        onView(withId(R.id.feed_recycler_view)).check(matches(isDisplayed()))
    }
}