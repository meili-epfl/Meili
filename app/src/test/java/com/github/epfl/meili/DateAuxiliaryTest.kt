package com.github.epfl.meili

import com.github.epfl.meili.helpers.DateAuxiliary
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import java.util.*

class DateAuxiliaryTest {
    private val testDate = Date(1666669 * 1000)

    @Test
    fun getDateFromTimestampTest() {
        MatcherAssert.assertThat(DateAuxiliary.getDateFromTimestamp(1666669), CoreMatchers.`is`(testDate))
    }

    @Test
    fun getDayTest() {
        MatcherAssert.assertThat(DateAuxiliary.getDay(testDate), CoreMatchers.`is`("Tue Jan 20"))
    }

    @Test
    fun getTimeTest() {
        MatcherAssert.assertThat(DateAuxiliary.getTime(testDate), CoreMatchers.`is`("07:57"))
    }
}