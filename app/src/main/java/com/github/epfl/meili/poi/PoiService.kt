package com.github.epfl.meili.poi

import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.CustomMath
import com.google.android.gms.maps.model.LatLng

interface PoiService {
    fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((Error) -> Unit)?)

    /**
     * @param userPosition: location of the user in coordinates
     * @param poiList: list of POIs that we know of
     * @param radius: radius that determine reachable POIs
     *
     * @return list of POIs that are withing the radius distance from the user location
     */
    fun getReachablePoi(userPosition: LatLng?, poiList: List<PointOfInterest>?, radius: Double?): List<PointOfInterest> {
        val reachablePois = ArrayList<PointOfInterest>()
        if (userPosition != null && poiList != null && radius != null) {
            for (poi in poiList) {
                val distance = CustomMath.distanceOnSphere(userPosition, poi.getLatLng())
                if (distance < radius) {
                    reachablePois.add(poi)
                }
            }
        }

        return reachablePois
    }
}