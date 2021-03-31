package com.github.epfl.meili.poi

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.github.epfl.meili.helpers.HttpRequestQueue
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class PoiService {
    private var queue: RequestQueue = HttpRequestQueue.getQueue()

    fun setQueue(newQueue: RequestQueue) {
        queue = newQueue
    }

    fun requestPois(latLng: LatLng, onSuccess: (List<PointOfInterest>) -> Unit, onError: (VolleyError) -> Unit) {

        val typeOfObjects = "node"

        /*bounding box: lowest_lat, lowest_lng, highest_lat, highest_long*/
        val bbox = "(" + (latLng.latitude - LAT_MARGIN) + "," + (latLng.longitude - LNG_MARGIN) + "," +
                (latLng.latitude + LAT_MARGIN) + "," + (latLng.longitude + LNG_MARGIN) + ")"

        val filter = "[historic=monument]"

        val resultTypeJSON = "[out:json];"

        val overpass_query = resultTypeJSON + typeOfObjects + bbox + filter + ";out;"

        queryOverpassAPI(overpass_query, onSuccess, onError)
    }

    fun queryOverpassAPI(overpass_query: String, onSuccess: (List<PointOfInterest>) -> Unit, onError: (VolleyError) -> Unit) {

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, OVERPASS_URL + overpass_query,
                null, customOnSuccessFrom(onSuccess), onError)

        queue.add(jsonObjectRequest)
    }

    fun customOnSuccessFrom(onSuccess: (List<PointOfInterest>) -> Unit): (JSONObject) -> Unit {
        return { response ->
            val overpassResponse = Gson().fromJson<OverpassResponse>(response.toString(), OverpassResponse::class.java)

            Log.d("POI Service", response.toString() + overpassResponse.getCustomPois().toString())
            onSuccess(overpassResponse.getCustomPois())
        }
    }

    companion object {
        private const val LAT_MARGIN = 0.125
        private const val LNG_MARGIN = 0.125
        private const val OVERPASS_URL = "https://overpass-api.de/api/interpreter?data="
    }

    data class OverpassResponse(
            @SerializedName("elements")
            val pointsOfInterest: List<OverpassPointOfInterest> = ArrayList()
    ) {
        fun getCustomPois(): List<PointOfInterest> {
            val poiList = ArrayList<PointOfInterest>()
            for (poi in pointsOfInterest) {
                if (poi.poiTags != null && poi.poiTags!!.name != null && poi.uid != null) {
                    poiList.add(poi.toStandardPoi())
                }
            }
            return poiList
        }
    }

    data class OverpassPointOfInterest(
            @SerializedName("lat")
            val lat: Double? = null,
            @SerializedName("lon")
            val lon: Double? = null,
            @SerializedName("tags")
            val poiTags: PoiTag? = null,
            @SerializedName("id")
            val uid: String? = null
    ) {
        fun toStandardPoi(): PointOfInterest {
            return PointOfInterest(LatLng(lat!!, lon!!), poiTags!!.name!!, uid!!)
        }
    }

    data class PoiTag(
            @SerializedName("name")
            val name: String? = null
    )
}