package com.github.epfl.meili.forum


import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PostActivityTest {

    companion object {
        private val TEST_POST = Post("AUTHOR", "TITLE", "TEXT")
    }

    private val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext, PostActivity::class.java)
            .putExtra("Post", TEST_POST)

    @get:Rule
    var testRule: ActivityScenarioRule<PostActivity> = ActivityScenarioRule(intent)

    @Test
    fun checkPostShown() {
        onView(withId(R.id.post_author)).check(matches(withText(containsString(TEST_POST.author))))
        onView(withId(R.id.post_title)).check(matches(withText(containsString(TEST_POST.title))))
        onView(withId(R.id.post_text)).check(matches(withText(containsString(TEST_POST.text))))
    }
}
