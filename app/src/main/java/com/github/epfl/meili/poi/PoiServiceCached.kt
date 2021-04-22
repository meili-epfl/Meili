package com.github.epfl.meili.poi

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.VolleyError
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.CustomMath
import com.github.epfl.meili.util.InternetConnectionService
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


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

    fun setSharedPreferences(sharedPreferences: SharedPreferences){
        this.mPrefs = sharedPreferences
    }

    override fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((VolleyError) -> Unit)?) {
        if (onSuccess != null && onError != null && latLng != null) {
            // If data saved in object is valid then return it
            // check if last fetch was more than 1 hour ago, also check if place updated was more than 1km far away
            if (isObjectDataValid(latLng)) {
                Log.d(TAG, "Getting info from in-object")
                onSuccess(lastPoiListResponse)
            }

            // first get info from phone then verify the validity
            // if you don't have internet conection then return what you saved on your phone
            else if (isCacheValid(latLng)) {
                Log.d(TAG, "Getting info from shared preferences")
                onSuccess(retrieveCachedPoiResponse())
            }

            // Data saved in the object and on the phone are not valid hence we need to fetch from the API
            else if (internetConnectionService.isConnectedToInternet(MainApplication.applicationContext())) {
                Log.d(TAG, "Getting info from the API")
                poiGoogleRetriever.requestPoisAPI(latLng, onSuccessSaveResponse(latLng) { onSuccess(it) }, onError)
            } else {
                // If there is some data available return it even if not valid
                if (responseTimestamp > 0L) {
                    Log.d(TAG, "Getting old info from in-object")
                    onSuccess(lastPoiListResponse)
                } else if (retrieveTimeOfResponse() > 0L) {
                    Log.d(TAG, "Getting old info from shared preferences")
                    onSuccess(retrieveCachedPoiResponse())
                }

                // Unfortunately there was no way to retrieve POIs
                Log.d(TAG, "Not possible to retreive POIs")
                onError(VolleyError("No Internet Connection and no cached data"))
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
        val type: Type = object : TypeToken<List<PointOfInterest?>?>() {}.type
        val arrayItems: List<PointOfInterest> = gsonObject.fromJson(json, type)
        return arrayItems
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
        const val CACHE_TIME_LIMIT = 60 * 60 // 1 hour in seconds
        const val CACHE_DISTANCE_LIMIT = 1000 // 1 km
        const val TAG = "PoiServiceCached"
    }
}