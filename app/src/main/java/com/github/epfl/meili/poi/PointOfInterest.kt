package com.github.epfl.meili.poi

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointOfInterest(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var name: String = "",
        var uid: String = "",
        var icon: String="",
        var poiTypes: List<String> = ArrayList(),
        var openNow:Boolean? = null
) : Parcelable {
    override fun toString(): String {
        return START_CHAR + "POI:" + "lat:"+latitude +"long:"+longitude+ ",name:" + name + ",uid:" + uid  +",icon:"+icon+ END_CHAR
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other::class.java == PointOfInterest::class.java){
            val otherPoi =  other as PointOfInterest
            return otherPoi.latitude.equals(latitude) && otherPoi.longitude==longitude && otherPoi.name == name
                    && otherPoi.uid == uid && otherPoi.icon == icon
        }

        return false
    }

    fun getLatLng(): LatLng{
        return LatLng(latitude, longitude)
    }

    companion object {
        const val START_CHAR = '{'
        const val END_CHAR = '}'
        const val TAG = "PointOfInterestClass"
    }
}