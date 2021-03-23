package com.github.epfl.meili.review

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Review
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class ReviewsActivityTest {

    companion object {
        private const val TEST_POI_KEY = "poiKey"
        private const val TEST_TITLE = "Beach too sandy"
        private const val TEST_SUMMARY = "Water too wet"
    }

    class MockReviewService(poiKey: String) : ReviewService(poiKey) {
        override var reviews: List<Review> = mockReviewsList()

        override fun addReview(review: Review) {
            TODO("Not yet implemented")
        }

        private fun mockReviewsList(): List<Review> {
            return IntRange(0, 5).map { i: Int -> Review(i, "$TEST_TITLE$i", "$TEST_SUMMARY$i")}
        }
    }

    @Before
    fun setupMocksAndIntents() {
        Intents.init()
    }
 
    @Test
    fun testReviewsActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), ReviewsActivity::class.java)
                .putExtra("POI_KEY", TEST_POI_KEY)

        val scenario: ActivityScenario<ReviewsActivity> = launch(intent)
        scenario.onActivity { _ ->
            val mockService: ReviewService = MockReviewService(TEST_POI_KEY)
            ReviewsActivityViewModel.setReviewService(mockService)

            onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
            IntRange(0, 5).forEach { i: Int ->
                onView(withText("$TEST_TITLE$i")).check(matches(isDisplayed()))
            }
        }
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }
}