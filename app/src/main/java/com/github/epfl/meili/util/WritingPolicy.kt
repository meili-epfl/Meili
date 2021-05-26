package com.github.epfl.meili.util

import com.github.epfl.meili.map.PointOfInterestStatus


object WritingPolicy {
    fun isWriteEnabled(loggedIn: Boolean, pointOfInterestStatus: PointOfInterestStatus): Boolean {
        return loggedIn && (pointOfInterestStatus == PointOfInterestStatus.REACHABLE || pointOfInterestStatus == PointOfInterestStatus.VISITED)
    }
}