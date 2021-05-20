package com.github.epfl.meili.models

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewUnitTest {

    companion object {
        private const val TEST_POI_KEY = "POI"
        private const val TEST_TITLE: String = "Beach Too Sandy"
        private const val TEST_SUMMARY: String = "Water Too Wet"
        private const val TEST_AUTHOR_ID: String = "author id"
    }

    @Test
    fun averageRatingCalculationTest() {
        val reviewMap: MutableMap<String, Review> = HashMap()
        val range = IntRange(1, 5)

        var averageRating = 0f

        for (i in range) {
            reviewMap[i.toString()] =
                Review(TEST_AUTHOR_ID, TEST_POI_KEY, i.toFloat(), TEST_TITLE, TEST_SUMMARY)
            averageRating += i.toFloat()
        }

        averageRating /= range.last - range.first + 1
        assertEquals(averageRating, Review.averageRating(reviewMap))
    }
}
