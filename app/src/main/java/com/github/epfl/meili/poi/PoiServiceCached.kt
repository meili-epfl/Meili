package com.github.epfl.meili.poi

import com.github.epfl.meili.cache.CacheService
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.CustomMath
import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import java.lang.reflect.Type


class PoiServiceCached : PoiService, CacheService<List<PointOfInterest>>(SHARED_PREFERENCES_KEY, CLASS_TYPE) {

    init {
        super.setResponseFetcher(PoiGoogleRetriever())
    }

    // In-Object cached values
    var responsePosition: LatLng = LatLng(0.0, 0.0)
    private var currentRequestedPosition: LatLng = LatLng(0.0,0.0)


    override fun requestPois(latLng: LatLng?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((Error) -> Unit)?) {
        if (onSuccess != null && onError != null && latLng != null) {
            currentRequestedPosition = latLng
            super.getResponse(latLng, onSuccessSaveResponse(latLng, onSuccess), onError)
        }
    }

    override fun getResponse(arg: Any?, onSuccess: ((List<PointOfInterest>) -> Unit)?, onError: ((Error) -> Unit)?) {
        requestPois(arg as LatLng, onSuccess, onError)
    }

    private fun onSuccessSaveResponse(latLng: LatLng, onSuccess: ((List<PointOfInterest>) -> Unit)): (List<PointOfInterest>) -> Unit {
        return { response ->
            savePositionOfResponse(latLng)
            super.saveResponse(response)
            onSuccess(response)
        }
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

    private fun retrievePositionOfResponse(): LatLng {
        val json = super.mPrefs.getString(POSITION_KEY, "")
        return gsonObject.fromJson(json, LatLng::class.java)
    }

    /**
     * Data is considered valid if it was retrieved less than 1 hour ago and the new request comes
     * from less than 1km far away from the last data
     *
     * @return whether the data saved in shared preferences is valid or not
     */
    override fun isCacheValid(cachedTimestamp: Long): Boolean {
        if (super.isCacheValid(cachedTimestamp)) {
            val cachedPosition = retrievePositionOfResponse()
            return CustomMath.distanceOnSphere(cachedPosition, currentRequestedPosition) < CACHE_DISTANCE_LIMIT

        }

        return false
    }


    /**
     * Data is considered valid if it was retrieved less than 1 hour ago and the new request comes
     * from less than 1km far away from the last data
     *
     * @return whether the data in the object is valid or not
     */
    override fun isObjectDataValid(): Boolean {
        if (super.isObjectDataValid()){
            return CustomMath.distanceOnSphere(responsePosition, currentRequestedPosition) < CACHE_DISTANCE_LIMIT
        }

        return false
    }

    companion object {
        const val SHARED_PREFERENCES_KEY = "POI-Response"
        const val POSITION_KEY = "position"
        const val CACHE_DISTANCE_LIMIT = 1000 // 1 km
        const val TAG = "PoiServiceCached"
        val CLASS_TYPE: Type = object: TypeToken<List<PointOfInterest?>?>() {}.type
    }
}