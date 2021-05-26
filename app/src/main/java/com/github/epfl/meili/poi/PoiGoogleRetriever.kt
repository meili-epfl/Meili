package com.github.epfl.meili.poi

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.cache.ResponseFetcher
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.HttpRequestQueue
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

open class PoiGoogleRetriever : ResponseFetcher<List<PointOfInterest>> {
    private var queue: RequestQueue = HttpRequestQueue.getQueue()

    fun setQueue(newQueue: RequestQueue) {
        queue = newQueue
    }

    override fun fetchResponse(
        arg: Any?,
        onSuccess: ((List<PointOfInterest>) -> Unit)?,
        onError: (Error) -> Unit
    ) {
        val position = arg as LatLng

        requestPoisAPI(position, onSuccess, onError)
    }

    open fun requestPoisAPI(
        latLng: LatLng?,
        onSuccess: ((List<PointOfInterest>) -> Unit)?,
        onError: ((Error) -> Unit)?
    ) {

        if (latLng != null && onSuccess != null && onError != null) {
            val apiKey = MainApplication.applicationContext().getString(R.string.google_api_key)
            val query = String.format(
                QUERY_TEMPLATE,
                latLng.latitude,
                latLng.longitude,
                MAX_COVERAGE_RADIUS,
                apiKey
            )
            queryGooglePlacesAPI(query, onSuccess, onError)
        }
    }

    private fun queryGooglePlacesAPI(
        query: String,
        onSuccess: (List<PointOfInterest>) -> Unit,
        onError: (Error) -> Unit
    ) {

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, GOOGLE_PLACES_URL + query,
            null, customOnSuccessFrom(onSuccess), customOnErrorFrom(onError)
        )

        queue.add(jsonObjectRequest)
    }

    fun customOnSuccessFrom(onSuccess: (List<PointOfInterest>) -> Unit): (JSONObject) -> Unit {
        return { response ->
            if (response["status"] == OK_STATUS) {
                val placesResponse =
                    Gson().fromJson(response.toString(), GooglePlacesResponse::class.java)
                Log.d(TAG, response.toString())
                Log.d(TAG, placesResponse.getCustomPois().toString())
                onSuccess(placesResponse.getCustomPois())
            }
        }
    }

    private fun customOnErrorFrom(onError: ((Error) -> Unit)): (VolleyError) -> Unit {
        return { error ->
            onError(Error(error.message))
        }
    }

    companion object {
        private const val GOOGLE_PLACES_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        private const val MAX_COVERAGE_RADIUS = 3000
        private const val TAG = "PoiGoogleRetriever"
        private const val QUERY_TEMPLATE = "location=%s,%s&radius=%s&type=point_of_interest&key=%s"
        private const val OK_STATUS = "OK"
    }

}

/**
 * The classes below are used to deserialize the JSON object received from the Google Places API
 * When completed you can call getCustomPois function to receive the List<PointOfInterest>
 */
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
        return PointOfInterest(
            geometry!!.latLng!!.latitude!!,
            geometry.latLng!!.longitude!!,
            name!!,
            uid!!,
            icon!!,
            poiTypes!!,
            openingHours?.openNow
        )
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