package com.github.epfl.meili


import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.forum.ForumCountingIdlingResource
import com.github.epfl.meili.forum.NewPostActivity
import com.github.epfl.meili.forum.PostActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CanCreateNewPostTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun canCreateNewPostTest() {
        // Source of characters for random string generation
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz !?.:;,$"

        // Make random title
        var length = java.util.Random().nextInt(70) // Length between 0 and 70
        val title = (1..length).map{source.random()}.joinToString("")

        // Make random text
        length = java.util.Random().nextInt(500) // Length between 0 and 500
        val text = (1..length).map{source.random()}.joinToString("")

        val materialButton = onView(
            allOf(
                withId(R.id.main_forum_button), withText("Forum"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
            allOf(
                withId(R.id.forum_new_post_button), withText("+"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.new_post_title),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText(title), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.new_post_text),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText(text), closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.new_post_create_button), withText("Create post"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        // Get Forum's idling counter and register it (to make espresso wait)
        IdlingRegistry.getInstance().register(ForumCountingIdlingResource)

        // Check the text of the texView who's parent's parent is forum_layout
        val textView = onView(
            allOf(
                withText(title),
                withParent(withParent(withId(R.id.forum_layout)))
            )
        )
        textView.check(matches(withText(title)))

        // Click on the layout who's child has a text value of title
        val linearLayout2 = onView(
            allOf(
                withChild(
                    withText(title)
                )
            )
        )
        linearLayout2.perform(click())

        val textView2 = onView(
            allOf(
                withId(R.id.post_title), withText(title),
                withParent(
                    allOf(
                        withId(R.id.post_top_layout),
                        withParent(withId(R.id.post_layout))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText(title)))

        val textView3 = onView(
            allOf(
                withId(R.id.post_text), withText(text),
                withParent(
                    allOf(
                        withId(R.id.post_top_layout),
                        withParent(withId(R.id.post_layout))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText(text)))

        // Unregister the idling counter
        IdlingRegistry.getInstance().unregister(ForumCountingIdlingResource)
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
