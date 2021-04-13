package com.github.epfl.meili.util

import com.google.android.gms.maps.model.LatLng

object CustomMath {
    const val EARTH_R = 6378100.0

    /**
     * Distance computed using Harversine's formula, for more details: https://en.wikipedia.org/wiki/Haversine_formula
     *
     * @return distance in meters between the two points
     */
    fun distanceOnSphere(from: LatLng, to: LatLng): Double {
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)

        val originLat = Math.toRadians(from.latitude)
        val destinationLat = Math.toRadians(to.latitude)

        val a = Math.pow(Math.sin(dLat / 2), 2.0) + Math.pow(Math.sin(dLon / 2.0), 2.0) * Math.cos(originLat) * Math.cos(destinationLat)
        val c = 2 * Math.asin(Math.sqrt(a))

        return EARTH_R * c;
    }
}