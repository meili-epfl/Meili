package com.github.epfl.meili.poi

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.android.volley.VolleyError
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.CustomMath
import com.github.epfl.meili.util.InternetConnectionService
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


class PoiServiceCached : PoiService {
    // Service for fetching POIs from Google Places API
    private var poiGoogleRetriever: PoiGoogleRetriever

    // Auxiliary service
    private var internetConnectionService: InternetConnectionService

    // Object for handling saving data locally on phone
    private var mPrefs: SharedPreferences
    private var gsonObject: Gson

    // In-Object cached values
    private var lastPoiListResponse: List<PointOfInterest> = ArrayList()
    private var responseTimestamp: Long = 0L
    private var responsePosition: LatLng = LatLng(0.0, 0.0)

    init {
        poiGoogleRetriever = PoiGoogleRetriever()
        mPrefs = MainApplication.applicationContext().getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
        gsonObject = Gson()
        internetConnectionService = InternetConnectionService
    }

    fun setPoiGoogleRetriever(poiGoogleRetriever: PoiGoogleRetriever) {
        this.poiGoogleRetriever = poiGoogleRetriever
    }

    fun setInternetConnectionServicce(internetConnectionService: InternetConnectionService) {
        this.internetConnectionService = internetConnectionService
    }

    override fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((VolleyError) -> Unit)?) {
        if (onSuccess != null && onError != null && latLng != null) {
            // If data saved in object is valid then return it
            // check if last fetch was more than 1 hour ago, also check if place updated was more than 1km far away
            if (isObjectDataValid(latLng)) {
                onSuccess(lastPoiListResponse)
            }

            // first get info from phone then verify the validity
            // if you don't have internet conection then return what you saved on your phone
            if (isCacheValid(latLng)) {
                onSuccess(retrieveCachedPoiResponse())
            }

            // Data saved in the object and on the phone are not valid hence we need to fetch from the API
            if (internetConnectionService.isConnectedToInternet()) {
                poiGoogleRetriever.requestPois(latLng, onSuccessSaveResponse(latLng) { onSuccess(it) }, onError)
            } else {
                // Unfortunately there was no way to retrieve POIs
                onError(VolleyError())
            }
        }
    }

    fun onSuccessSaveResponse(latLng: LatLng, onSuccess: ((List<PointOfInterest>) -> Unit)): ((List<PointOfInterest>) -> Unit)? {
        return { response ->
            saveResponse(response, latLng)
            onSuccess(response)
        }
    }

    fun saveResponse(poiList: List<PointOfInterest>, position: LatLng) {
        saveTimeOfResponse()
        savePositionOfResponse(position)

        val prefsEditor = mPrefs.edit()
        val jsonPoiList = gsonObject.toJson(poiList, poiList::class.java)
        prefsEditor.putString(POI_LIST_KEY, jsonPoiList)
        prefsEditor.apply()
    }

    fun saveTimeOfResponse() {
        val tsLong = System.currentTimeMillis() / 1000

        val prefsEditor = mPrefs.edit()

        prefsEditor.putLong(TIMESTAMP_KEY, tsLong)
        prefsEditor.apply()
    }

    fun savePositionOfResponse(latLng: LatLng) {
        val prefsEditor = mPrefs.edit()
        val jsonPoiList = gsonObject.toJson(latLng, LatLng::class.java)
        prefsEditor.putString(POSITION_KEY, jsonPoiList)
        prefsEditor.apply()
    }

    fun retrieveCachedPoiResponse(): List<PointOfInterest> {
        val json = mPrefs.getString(POI_LIST_KEY, "")
        var poiList: List<PointOfInterest> = ArrayList()
        return gsonObject.fromJson(json, poiList::class.java)
    }

    fun retrieveTimeOfResponse(): Long {
        return mPrefs.getLong(TIMESTAMP_KEY, 0L)
    }

    fun retrievePositionOfResponse(): LatLng {
        val json = mPrefs.getString(POSITION_KEY, "")
        return gsonObject.fromJson(json, LatLng::class.java)
    }

    fun isCacheValid(currentPosition: LatLng): Boolean {
        val currentTimestamp = System.currentTimeMillis() / 1000
        val cachedTimestamp = retrieveTimeOfResponse()

        if (currentTimestamp - cachedTimestamp > CACHE_TIME_LIMIT) {
            return false
        }
        val cachedPosition = retrievePositionOfResponse()
        return CustomMath.distanceOnSphere(cachedPosition, currentPosition) < CACHE_DISTANCE_LIMIT
    }

    fun isObjectDataValid(currentPosition: LatLng): Boolean {
        val currentTimestamp = System.currentTimeMillis() / 1000

        if (currentTimestamp - responseTimestamp > CACHE_TIME_LIMIT) {
            return false
        }

        return CustomMath.distanceOnSphere(responsePosition, currentPosition) < CACHE_DISTANCE_LIMIT
    }

    companion object {
        const val SHARED_PREFERENCES_KEY = "POI-Response"
        const val POI_LIST_KEY = "POI-List"
        const val TIMESTAMP_KEY = "timestamp"
        const val POSITION_KEY = "position"
        const val CACHE_TIME_LIMIT = 0L //todo: put around 1 hour
        const val CACHE_DISTANCE_LIMIT = 1000 //todo check meters
    }
}