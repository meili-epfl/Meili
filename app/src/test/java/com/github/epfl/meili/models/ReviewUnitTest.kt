package com.github.epfl.meili.models

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

object ReviewUnitTest {

    private const val TEST_RATING: Int = 3
    private const val TEST_TITLE : String = "Beach Too Sandy"
    private const val TEST_SUMMARY: String = "Water Too Wet"

    @Test
    fun reviewConstructorTest() {
        val review = Review(TEST_RATING, TEST_TITLE, TEST_SUMMARY)
        assertThat(review.rating, `is`(TEST_RATING))
        assertThat(review.title, `is`(TEST_TITLE))
        assertThat(review.summary, `is`(TEST_SUMMARY))
    }
}
