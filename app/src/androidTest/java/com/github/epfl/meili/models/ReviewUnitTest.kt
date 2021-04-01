package com.github.epfl.meili.models

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class ReviewUnitTest {

    companion object {
        private const val TEST_RATING: Float = 3f
        private const val TEST_TITLE : String = "Beach Too Sandy"
        private const val TEST_SUMMARY: String = "Water Too Wet"
    }

    @Test
    fun reviewConstructorTest() {
        val review = Review(TEST_RATING, TEST_TITLE, TEST_SUMMARY)
        assertThat(review.rating, `is`(TEST_RATING))
        assertThat(review.title, `is`(TEST_TITLE))
        assertThat(review.summary, `is`(TEST_SUMMARY))
    }

    @Test
    fun averageRatingCalculationTest() {
        val reviewMap: MutableMap<String, Review> = HashMap()
        val range: IntRange = IntRange(1, 5)

        var averageRating: Float = 0f

        for (i in range) {
            reviewMap[i.toString()] = Review(i.toFloat(), TEST_TITLE, TEST_SUMMARY)
            averageRating += i.toFloat()
        }

        averageRating /= range.last - range.first + 1
        assertThat(Review.averageRating(reviewMap), `is`(averageRating))
    }
}
