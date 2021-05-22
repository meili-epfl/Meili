package com.github.epfl.meili.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HttpRequestQueueTests {
    @Test
    fun getQueueWhenNotInitialized() {
        assertNotNull(HttpRequestQueue.getQueue())
    }

    @Test
    fun getQueueReturnsSingleton() {
        assertEquals(HttpRequestQueue.getQueue(), HttpRequestQueue.getQueue())
    }
}