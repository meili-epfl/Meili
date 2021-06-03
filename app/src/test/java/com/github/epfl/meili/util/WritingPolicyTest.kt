package com.github.epfl.meili.util

import com.github.epfl.meili.poi.PointOfInterestStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class WritingPolicyTest {
    @Test
    fun writeNotEnabledWhenLoggedOut(){
        assertEquals(WritingPolicy.isWriteEnabled(false, PointOfInterestStatus.VISITED), false)
        assertEquals(WritingPolicy.isWriteEnabled(false, PointOfInterestStatus.REACHABLE), false)
        assertEquals(WritingPolicy.isWriteEnabled(false, PointOfInterestStatus.VISIBLE), false)
    }

    @Test
    fun writeIsEnabledWhenLoggedInAndVisitedOrReachable(){
        assertEquals(WritingPolicy.isWriteEnabled(true, PointOfInterestStatus.VISITED), true)
        assertEquals(WritingPolicy.isWriteEnabled(true, PointOfInterestStatus.REACHABLE), true)
        assertEquals(WritingPolicy.isWriteEnabled(true, PointOfInterestStatus.VISIBLE), false)
    }
}