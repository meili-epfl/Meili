package com.github.epfl.meili.poi

/**
 * VISITED: means that the user interacted with this POI when it was reachable
 * VISIBLE: means that POI is loaded in your device
 * REACHABLE: means that the user is close enough to the POI and is allowed to interact with it
 */
enum class PointOfInterestStatus {
    VISITED, VISIBLE, REACHABLE
}