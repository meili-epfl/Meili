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
    private var poiGoogleRetriever: PoiGoogleRetriever = PoiGoogleRetriever()

    // Auxiliary service
    private var internetConnectionService = InternetConnectionService()

    // Object for handling saving data locally on phone
    private var mPrefs = MainApplication.applicationContext().getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE)
    private var gsonObject = Gson()

    // In-Object cached values
    var lastPoiListResponse: List<PointOfInterest> = ArrayList()
    var responseTimestamp: Long = 0L
    var responsePosition: LatLng = LatLng(0.0, 0.0)

    fun setSharedPreferences(sharedPreferences: SharedPreferences){
        this.mPrefs = sharedPreferences
    }

    override fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((VolleyError) -> Unit)?) {
        if (onSuccess != null && onError != null && latLng != null) {
            when {
                isObjectDataValid(latLng) -> {
                    // If data saved in object is valid then return it

                    Log.d(TAG, "Getting info from in-object")
                    onSuccess(lastPoiListResponse)
                }
                isCacheValid(latLng) -> {
                    // If saved data locally is valid then return it

                    Log.d(TAG, "Getting info from shared preferences")
                    onSuccess(retrieveCachedPoiResponse())
                }
                internetConnectionService.isConnectedToInternet(MainApplication.applicationContext()) -> {
                    // Data saved in the object and on the phone are not valid hence we need to fetch from the API
                    // On data received save it in object and locally

                    Log.d(TAG, "Getting info from the API")
                    poiGoogleRetriever.requestPoisAPI(latLng, onSuccessSaveResponse(latLng) { onSuccess(it) }, onError)
                }
                else -> {
                    // If there is no internet connection but some data available then return it even if not valid
                    when {
                        responseTimestamp > 0L -> {
                            Log.d(TAG, "Getting old info from in-object")
                            onSuccess(lastPoiListResponse)
                        }
                        retrieveTimeOfResponse() > 0L -> {
                            Log.d(TAG, "Getting old info from shared preferences")
                            onSuccess(retrieveCachedPoiResponse())
                        }
                        else -> {
                            // Unfortunately there was no way to retrieve POIs
                            Log.d(TAG, "Not possible to retreive POIs")
                            onError(VolleyError("No Internet Connection and no cached data"))
                        }
                    }
                }
            }
        }
    }

    private fun onSuccessSaveResponse(latLng: LatLng, onSuccess: ((List<PointOfInterest>) -> Unit)): (List<PointOfInterest>) -> Unit {
        return { response ->
            saveResponse(response, latLng)
            onSuccess(response)
        }
    }

    private fun saveResponse(poiList: List<PointOfInterest>, position: LatLng) {
        // Save data both in object and in shared preferences
        saveTimeOfResponse()
        savePositionOfResponse(position)

        // Save data in shared preferences
        val prefsEditor = mPrefs.edit()
        val jsonPoiList = gsonObject.toJson(poiList, poiList::class.java)
        prefsEditor.putString(POI_LIST_KEY, jsonPoiList)
        prefsEditor.apply()

        // Save data in object
        lastPoiListResponse = poiList
    }

    private fun saveTimeOfResponse() {
        val tsLong = System.currentTimeMillis() / 1000

        // Save data in shared preferences
        val prefsEditor = mPrefs.edit()
        prefsEditor.putLong(TIMESTAMP_KEY, tsLong)
        prefsEditor.apply()

        // Save data in object
        responseTimestamp = tsLong
    }

    private fun savePositionOfResponse(latLng: LatLng) {
        // Save data in shared preferences
        val prefsEditor = mPrefs.edit()
        val jsonPoiList = gsonObject.toJson(latLng, LatLng::class.java)
        prefsEditor.putString(POSITION_KEY, jsonPoiList)
        prefsEditor.apply()

        // Save data in object
        responsePosition = latLng
    }

    private fun retrieveCachedPoiResponse(): List<PointOfInterest> {
        val json = mPrefs.getString(POI_LIST_KEY, "")
        val type: Type = object : TypeToken<List<PointOfInterest?>?>() {}.type
        return gsonObject.fromJson(json, type)
    }

    private fun retrieveTimeOfResponse(): Long {
        return mPrefs.getLong(TIMESTAMP_KEY, 0L)
    }

    private fun retrievePositionOfResponse(): LatLng {
        val json = mPrefs.getString(POSITION_KEY, "")
        return gsonObject.fromJson(json, LatLng::class.java)
    }

    /**
     * Data is considered valid if it was retrieved less than 1 hour ago and the new request comes
     * from less than 1km far away from the last data
     *
     * @return whether the data saved in shared preferences is valid or not
     */
    private fun isCacheValid(currentPosition: LatLng): Boolean {
        val currentTimestamp = System.currentTimeMillis() / 1000
        val cachedTimestamp = retrieveTimeOfResponse()

        if (currentTimestamp - cachedTimestamp > CACHE_TIME_LIMIT) {
            return false
        }
        val cachedPosition = retrievePositionOfResponse()
        return CustomMath.distanceOnSphere(cachedPosition, currentPosition) < CACHE_DISTANCE_LIMIT
    }

    /**
     * Data is considered valid if it was retrieved less than 1 hour ago and the new request comes
     * from less than 1km far away from the last data
     *
     * @return whether the data in the object is valid or not
     */
    private fun isObjectDataValid(currentPosition: LatLng): Boolean {
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