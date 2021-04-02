package com.github.epfl.meili.map

import android.location.Location
import android.location.LocationListener
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.HashMap

class PoiMarkerViewModel : ViewModel(), Observer, LocationListener {

    private lateinit var database: Database<PointOfInterest>
    private lateinit var poiService: PoiService

    private var locationService = LocationService()
    private var lastUserLocation: LatLng = LatLng(0.0,0.0)

    private val mPointsOfInterest: MutableLiveData<Map<String, PointOfInterest>> = MutableLiveData(HashMap())
    private val mPointsOfInterestStatus: MutableLiveData<HashMap<String, PointOfInterestStatus>> = MutableLiveData(HashMap())

    init {
        locationService.listenToLocationChanges(this)
    }

    fun setPoiService(service: PoiService) {
        this.poiService = service
        poiService.requestPois(LatLng(0.0, 0.0), { poiList -> addPoiList(poiList); setReachablePois() }, { error -> Log.d(TAG, "error getting pois from service", error) })
    }

    fun setDatabase(db: Database<PointOfInterest>) {
        this.database = db
        database.addObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        mPointsOfInterest.value = mPointsOfInterest.value!! + database.values

        // update status of each Poi to VISITED
        val statusMap: HashMap<String, PointOfInterestStatus> = mPointsOfInterestStatus.value!!
        for (poi in database.values) {
            statusMap.put(poi.value.uid, PointOfInterestStatus.VISITED)
        }

        setReachablePois()
    }

    fun setReachablePois() {
        //set all reachable pois to REACHABLE status
        val reachablePois = poiService.getReachablePoi(lastUserLocation, mPointsOfInterest.value!!.values.toList(), REACHABLE_DIST)
        val statusMap = mPointsOfInterestStatus.value!!

        for (poi in reachablePois) {
            statusMap.put(poi.uid, PointOfInterestStatus.REACHABLE)
        }

        // Unset unreachable pois to either VISIBLE or VISITED
        val unreachablePois = mPointsOfInterest.value!!.values.minus(reachablePois)

        val visitedPois = database.values

        for(poi in unreachablePois){
           statusMap.put(poi.uid, if (visitedPois.containsKey(poi.uid)) PointOfInterestStatus.VISITED else PointOfInterestStatus.VISIBLE)
        }

        mPointsOfInterestStatus.value = statusMap
    }

    fun addPoiList(list: List<PointOfInterest>) {
        val poiMap = mPointsOfInterest.value!!
        val statusMap = mPointsOfInterestStatus.value!!

        for (poi in list) {
            if (!poiMap.containsKey(poi.uid)) {
                poiMap + Pair(poi.uid, poi)
                statusMap + Pair(poi.uid, PointOfInterestStatus.VISIBLE)
            }
        }

        mPointsOfInterestStatus.value = statusMap
        mPointsOfInterest.value = poiMap
    }

    fun setPoiVisited(poi: PointOfInterest){
        if(mPointsOfInterestStatus.value!![poi.uid] == PointOfInterestStatus.REACHABLE){
            if (!database.values.containsKey(poi.uid)){
                database.addElement(poi.uid, poi)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        lastUserLocation = LatLng(location.latitude, location.longitude)

        setReachablePois()
    }

    companion object {
        const val TAG = "PoiMarkerViewModel"
        const val REACHABLE_DIST = 25.0 //meters
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