package com.github.epfl.meili.map

import com.github.epfl.meili.models.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Cluster Item representing a POI to be added as item in the ClusterManager (for representing POIs
 * on Map as Markers)
 */
class MarkerItem(val poi: PointOfInterest) : ClusterItem {
    override fun getPosition(): LatLng {
        return poi.getLatLng()
    }

    override fun getTitle(): String {
        return poi.name
    }

    override fun getSnippet(): String? {
        return null
    }

    override fun toString(): String {
        return "PoiItem: $poi"
    }

    override fun equals(other: Any?): Boolean {
        return other is MarkerItem && other.poi == poi
    }

    override fun hashCode(): Int {
        return poi.hashCode()
    }
}