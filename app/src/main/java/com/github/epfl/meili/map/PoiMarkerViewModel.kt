package com.github.epfl.meili.map

import android.location.Location
import android.location.LocationListener
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.VolleyError
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.HashMap

class PoiMarkerViewModel : ViewModel(), Observer, LocationListener {

    private var database: Database<PointOfInterest>? = null
    private var poiService: PoiService? = null
    private var lastUserLocation: LatLng? = null

    val mPointsOfInterest: MutableLiveData<Map<String, PointOfInterest>> =
            MutableLiveData(HashMap())
    val mPointsOfInterestStatus: MutableLiveData<Map<String, PointOfInterestStatus>> =
            MutableLiveData(HashMap())


    fun setPoiService(service: PoiService) {
        this.poiService = service
        if (lastUserLocation != null) {
            poiService!!.requestPois(
                    lastUserLocation!!,
                    { poiList -> onSuccessPoiReceived(poiList) },
                    { error -> onError(error) })
        }
    }

    private fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        addPoiList(poiList)
        setReachablePois()
    }

    private fun onError(error: VolleyError) {
        Log.d(TAG, "error getting pois from service", error)
    }

    fun setDatabase(db: Database<PointOfInterest>) {
        this.database = db
        database!!.addObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        mPointsOfInterest.value = mPointsOfInterest.value!! + database!!.values

        // update status of each Poi to VISITED
        var statusMap: Map<String, PointOfInterestStatus> = mPointsOfInterestStatus.value!!
        for (poi in database!!.values) {
            statusMap = statusMap + Pair(poi.value.uid, PointOfInterestStatus.VISITED)
        }

        mPointsOfInterestStatus.value = statusMap

        setReachablePois()
    }

    fun setReachablePois() {
        //set all reachable pois to REACHABLE status
        val reachablePois = poiService!!.getReachablePoi(
                lastUserLocation!!,
                mPointsOfInterest.value!!.values.toList(),
                REACHABLE_DIST
        )
        var statusMap = mPointsOfInterestStatus.value!!

        for (poi in reachablePois) {
            statusMap = statusMap + Pair(poi.uid, PointOfInterestStatus.REACHABLE)
        }

        // Unset unreachable pois to either VISIBLE or VISITED
        val unreachablePois = mPointsOfInterest.value!!.values.minus(reachablePois)

        if (database != null) {
            val visitedPois = database!!.values

            for (poi in unreachablePois) {
                statusMap = statusMap + Pair(
                        poi.uid,
                        if (visitedPois.containsKey(poi.uid)) PointOfInterestStatus.VISITED else PointOfInterestStatus.VISIBLE
                )
            }
        }

        mPointsOfInterestStatus.value = statusMap
    }

    fun addPoiList(list: List<PointOfInterest>) {
        var poiMap = mPointsOfInterest.value!!
        var statusMap = mPointsOfInterestStatus.value!!

        for (poi in list) {
            if (!poiMap.containsKey(poi.uid)) {
                poiMap = poiMap + Pair(poi.uid, poi)
                statusMap = statusMap + Pair(poi.uid, PointOfInterestStatus.VISIBLE)
            }
        }

        mPointsOfInterestStatus.value = statusMap
        mPointsOfInterest.value = poiMap
    }

    fun setPoiVisited(poi: PointOfInterest) {
        if (mPointsOfInterestStatus.value!![poi.uid] == PointOfInterestStatus.REACHABLE) {
            if (database != null && !database!!.values.containsKey(poi.uid)) {
                database!!.addElement(poi.uid, poi)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val shouldCallService = lastUserLocation == null
        lastUserLocation = LatLng(location.latitude, location.longitude)

        Log.d(TAG, "lastUserLocation" + lastUserLocation)
        if (shouldCallService && poiService != null) {
            Log.d(TAG, "lastUserLocation" + lastUserLocation)
            poiService!!.requestPois(
                    lastUserLocation!!,
                    { poiList -> onSuccessPoiReceived(poiList) },
                    { error -> onError(error) })
        }

        setReachablePois()
    }

    companion object {
        const val TAG = "PoiMarkerViewModel"
        const val REACHABLE_DIST = 50.0 //meters
    }

    /**
     * VISIBLE: means that POI is loaded in your device
     * VISITED: means that the user interacted with this POI when it was reachable
     * REACHABLE: means that the user is close enough to the POI and is allowed to interact with it
     */
    enum class PointOfInterestStatus {
        VISITED, VISIBLE, REACHABLE
    }
}