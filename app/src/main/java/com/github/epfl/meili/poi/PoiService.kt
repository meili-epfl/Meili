package com.github.epfl.meili.poi

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.util.CustomMath
import com.github.epfl.meili.util.HttpRequestQueue
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

open class PoiService {
    private var queue: RequestQueue = HttpRequestQueue.getQueue()

    fun setQueue(newQueue: RequestQueue) {
        queue = newQueue
    }

    open fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((VolleyError) -> Unit)?) {

        if (latLng != null && onSuccess != null && onError != null) {
            val apiKey = MainApplication.applicationContext().getString(R.string.google_api_key)
            val query = "location=${latLng.latitude},${latLng.longitude}&radius=$MAX_COVERAGE_RADIUS&type=point_of_interest&key=$apiKey"
            queryGooglePlacesAPI(query, onSuccess, onError)
        }
    }

    fun queryGooglePlacesAPI(query: String, onSuccess: (List<PointOfInterest>) -> Unit, onError: (VolleyError) -> Unit) {

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, GOOGLE_PLACES_URL + query,
                null, customOnSuccessFrom(onSuccess), onError)

        queue.add(jsonObjectRequest)
    }

    fun customOnSuccessFrom(onSuccess: (List<PointOfInterest>) -> Unit): (JSONObject) -> Unit {
        return { response ->
            if (response["status"] == "OK") {
                val placesResponse = Gson().fromJson(response.toString(), GooglePlacesResponse::class.java)
                Log.d("POI Service", response.toString())
                Log.d("POI Service", placesResponse.getCustomPois().toString())
                onSuccess(placesResponse.getCustomPois())
            }
        }
    }

    /**
     * @param userPosition: location of the user in coordinates
     * @param poiList: list of POIs that we know of
     * @param radius: radius that determine reachable POIs
     *
     * @return list of POIs that are withing the radius distance from the user location
     */
    open fun getReachablePoi(userPosition: LatLng?, poiList: List<PointOfInterest>?, radius: Double?): List<PointOfInterest> {
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

    companion object {
        private const val GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        private const val MAX_COVERAGE_RADIUS = 3000
    }
}

data class GooglePlacesResponse(
        @SerializedName("results")
        val pointsOfInterest: List<PlacesPointOfInterest> = ArrayList()
) {
    fun getCustomPois(): List<PointOfInterest> {
        val poiList = ArrayList<PointOfInterest>()
        for (poi in pointsOfInterest) {
            if (poi.geometry != null && poi.name != null && poi.geometry.latLng != null && poi.uid != null) {
                poiList.add(poi.toStandardPoi())
            }
        }
        return poiList
    }
}

//TODO: we could also get some photos from the GooglePlaces API if we want
data class PlacesPointOfInterest(
        @SerializedName("geometry")
        val geometry: PoiGeometry? = null,
        @SerializedName("place_id")
        val uid: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("icon")
        val icon: String? = null,
        @SerializedName("types")
        val poiTypes: List<String>? = null,
        @SerializedName("opening_hours")
        val openingHours: PoiOpeningHours? = null
) {
    fun toStandardPoi(): PointOfInterest {
        if (openingHours != null) {
            return PointOfInterest(geometry!!.latLng!!.latitude!!, geometry.latLng!!.longitude!!, name!!, uid!!, icon!!, poiTypes!!, openingHours.openNow)
        }
        return PointOfInterest(geometry!!.latLng!!.latitude!!, geometry.latLng!!.longitude!!, name!!, uid!!, icon!!, poiTypes!!)
    }
}

data class PoiGeometry(
        @SerializedName("location")
        val latLng: PoiLocation? = null
)

data class PoiLocation(
        @SerializedName("lat")
        val latitude: Double? = null,
        @SerializedName("lng")
        val longitude: Double? = null
)

data class PoiOpeningHours(
        @SerializedName("open_now")
        val openNow: Boolean? = null
)