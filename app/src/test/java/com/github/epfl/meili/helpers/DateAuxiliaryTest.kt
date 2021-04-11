package com.github.epfl.meili.helpers

import com.github.epfl.meili.util.DateAuxiliary
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
        val splitted = testDate.toString().split(" ")
        val expected = splitted[0]+" "+splitted[1]+" "+ splitted[2]
        MatcherAssert.assertThat(DateAuxiliary.getDay(testDate), CoreMatchers.`is`(expected))
    }

    @Test
    fun getTimeTest() {
        val splitted = testDate.toString().split(" ")
        MatcherAssert.assertThat(DateAuxiliary.getTime(testDate), CoreMatchers.`is`(splitted[3].substring(0, splitted[3].length-3)))
    }
}