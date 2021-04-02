package com.github.epfl.meili.poi

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointOfInterest(
        var latLng: LatLng = LatLng(0.0, 0.0),
        var name: String = "",
        var uid: String = ""
) : Parcelable {

    override fun toString(): String {
        return START_CHAR + "POI:" + latLng.toString() + ",name:" + name + ",uid:" + uid + END_CHAR
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other::class.java == PointOfInterest::class.java){
            val otherPoi =  other as PointOfInterest
            return otherPoi.latLng.equals(latLng) && otherPoi.name == name && otherPoi.uid == uid
        }

        return false
    }

    companion object {
        const val START_CHAR = '{'
        const val END_CHAR = '}'
        const val TAG = "PointOfInterestClass"
    }
}