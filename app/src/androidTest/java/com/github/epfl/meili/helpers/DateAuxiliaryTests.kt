package com.github.epfl.meili.helpers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DateAuxiliaryTests {
    private val testDate = Date(1666669*1000)

    @Test
    fun getDateFromTimestampTest(){
        assertThat(DateAuxiliary.getDateFromTimestamp(1666669), `is`(testDate))
    }

    @Test
    fun getDayTest(){
        assertThat(DateAuxiliary.getDay(testDate), `is`("Tue Jan 20"))
    }

    @Test
    fun getTimeTest(){
        assertThat(DateAuxiliary.getTime(testDate), `is`("07:57"))
    }
}