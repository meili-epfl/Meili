package com.github.epfl.meili.map

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiService
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.HashMap

/**
 * PoiMarkerViewModel is the class that handles the POIs list
 * This list is Personalized for each user and depending on their position and the history of visited POIs
 * each POI will have a different status between VISITED, VISIBLE and REACHABLE
 */
class MarkerViewModel : ViewModel(), Observer, LocationListener {

    private var database: Database<PointOfInterest>? = null
    private var poiService: PoiService? = null
    private var lastUserLocation: LatLng? = null

    private var nbCurrentRequests = 0

    val mPointsOfInterest: MutableLiveData<Map<String, PointOfInterest>> =
            MutableLiveData(HashMap())
    val mPointsOfInterestStatus: MutableLiveData<Map<String, PointOfInterestStatus>> =
            MutableLiveData(HashMap())

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    /**
     * Sets the poi service to `service`
     */
    fun setPoiService(service: PoiService) {
        this.poiService = service
        if (lastUserLocation != null) {
            requestPois()
        }
    }

    private fun requestPois() {
        poiService!!.requestPois(
                lastUserLocation!!,
                { poiList -> onSuccessPoiReceived(poiList) },
                { error -> onError(error) })
    }

    private fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        nbCurrentRequests = 0
        addPoiList(poiList)
        setReachablePois()
    }

    private fun onError(error: Error) {
        Log.d(TAG, "error getting pois from service", error)

        nbCurrentRequests += 1

        if (nbCurrentRequests >= MAX_NUM_REQUESTS) {
            Toast.makeText(MainApplication.applicationContext(), MainApplication.applicationContext().getString(R.string.fetch_pois_error_message), Toast.LENGTH_LONG).show()
        } else {
            requestPois()
        }
    }

    /**
     * Sets the database to `db`
     */
    fun setDatabase(db: Database<PointOfInterest>) {
        this.database = db
        database!!.addObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        mPointsOfInterest.value = mPointsOfInterest.value!! + database!!.elements

        // update status of each Poi to VISITED
        var statusMap: Map<String, PointOfInterestStatus> = mPointsOfInterestStatus.value!!
        for (poi in database!!.elements) {
            statusMap = statusMap + Pair(poi.value.uid, PointOfInterestStatus.VISITED)
        }

        mPointsOfInterestStatus.value = statusMap

        setReachablePois()
    }

    private fun setReachablePois() {
        if (lastUserLocation != null) {
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
                val visitedPois = database!!.elements

                for (poi in unreachablePois) {
                    statusMap = statusMap + Pair(
                            poi.uid,
                            if (visitedPois.containsKey(poi.uid)) PointOfInterestStatus.VISITED else PointOfInterestStatus.VISIBLE
                    )
                }
            }

            mPointsOfInterestStatus.value = statusMap
        }
    }

    private fun addPoiList(list: List<PointOfInterest>) {
        var poiMap = mPointsOfInterest.value!!
        var statusMap = mPointsOfInterestStatus.value!!

        for (poi in list) {
            if (!poiMap.containsKey(poi.uid)) {
                poiMap = poiMap + Pair(poi.uid, poi)
                statusMap = statusMap + Pair(poi.uid, PointOfInterestStatus.VISIBLE)
            }
        }

        mPointsOfInterest.value = poiMap
        mPointsOfInterestStatus.value = statusMap
    }

    /**
     * Marks `poi` as having been visited if it is reachable
     */
    fun setPoiVisited(poi: PointOfInterest) {
        if (mPointsOfInterestStatus.value!![poi.uid] == PointOfInterestStatus.REACHABLE) {
            if (database != null && !database!!.elements.containsKey(poi.uid)) {
                database!!.addElement(poi.uid, poi)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val shouldCallService = lastUserLocation == null
        val newLocation = LatLng(location.latitude, location.longitude)

        if (lastUserLocation != newLocation) {
            lastUserLocation = newLocation
            if (shouldCallService && poiService != null) {
                Log.d(TAG, "lastUserLocation$lastUserLocation")
                requestPois()
            }
        }

        setReachablePois()
    }

    companion object {
        const val TAG = "PoiMarkerViewModel"
        const val REACHABLE_DIST = 50.0 //meters
        const val MAX_NUM_REQUESTS = 2
    }

    /**
     * VISITED: means that the user interacted with this POI when it was reachable
     * VISIBLE: means that POI is loaded in your device
     * REACHABLE: means that the user is close enough to the POI and is allowed to interact with it
     */
    enum class PointOfInterestStatus {
        VISITED, VISIBLE, REACHABLE
    }
}