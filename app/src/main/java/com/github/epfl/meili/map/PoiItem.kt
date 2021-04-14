package com.github.epfl.meili.map

import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PoiItem(val poi: PointOfInterest): ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(poi.latitude, poi.longitude)
    }

    override fun getTitle(): String? {
        return poi.name
    }

    override fun getSnippet(): String? {
        return null
    }
}