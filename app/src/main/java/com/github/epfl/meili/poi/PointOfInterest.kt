package com.github.epfl.meili.poi

import com.google.android.gms.maps.model.LatLng


data class PointOfInterest(var latLng: LatLng, var name: String, var uid: String) {
    override fun toString(): String {
        return "{ POI: "+uid+" , name: "+name+", uid:"+uid+"}"
    }
}