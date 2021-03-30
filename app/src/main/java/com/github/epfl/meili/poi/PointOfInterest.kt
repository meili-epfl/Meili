package com.github.epfl.meili.poi

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable


data class PointOfInterest(var latLng: LatLng, var name: String, var uid: String) {
    override fun toString(): String {
        return START_CHAR + "POI:" + latLng.toString() + ",name:" + name + ",uid:" + uid + END_CHAR
    }

    fun fromString(s: String): PointOfInterest? {
        if (s[0] != '{' || s[s.length - 1] != '}') {
            return null
        }

        val attr = s.split(DELIMITER)
        if (attr.size!=3){
            return null
        }



    }

    companion object {
        const val START_CHAR = '{'
        const val END_CHAR = '}'
        const val DELIMITER = "&|&"
        const val POI = "POI:"
        const val NAME = "name:"
        const val UID =  "uid:"
    }
}