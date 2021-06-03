package com.github.epfl.meili.util

import com.github.epfl.meili.poi.PointOfInterestStatus


object WritingPolicy {
    fun isWriteEnabled(loggedIn: Boolean, pointOfInterestStatus: PointOfInterestStatus): Boolean {
        return loggedIn && (pointOfInterestStatus == PointOfInterestStatus.REACHABLE || pointOfInterestStatus == PointOfInterestStatus.VISITED)
    }
}