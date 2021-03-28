package com.github.epfl.meili.helpers

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HttpRequestQueueTests {
    @Test
    fun getQueueWhenNotInitialized(){
        assertNotNull(HttpRequestQueue.getQueue())
    }

    @Test
    fun getQueueReturnsSignleton(){
        assertEquals(HttpRequestQueue.getQueue(), HttpRequestQueue.getQueue())
    }
}