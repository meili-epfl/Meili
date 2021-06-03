package com.github.epfl.meili.map

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.location.Location
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.poi.PointOfInterestStatus
import com.github.epfl.meili.util.LandmarkDetectionService
import com.github.epfl.meili.util.PoiServiceViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.google.maps.android.SphericalUtil.computeHeading
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.PI
import kotlin.math.roundToInt

/**
 * Manages the list of points of interest and Meili Lens
 * The list personalized for each user and depending on their position and the history of visited POIs
 * each POI will have a different status between VISITED, VISIBLE and REACHABLE
 */
class MapActivityViewModel(application: Application) :
    AndroidViewModel(application), PoiServiceViewModel, Observer {
    companion object {
        private const val FIELD_OF_VIEW = 60.0 // degrees
        private const val AZIMUTH_TOLERANCE = 20.0 // degrees
        private const val REACHABLE_DIST = 500.0 //meters

        var getSensorManager: (application: Application) -> SensorManager =
            { getSystemService(it, SensorManager::class.java)!! }

        var getEventValues: (event: SensorEvent) -> FloatArray = { it.values }
    }

    override var poiService: PoiService? = null
    override var nbCurrentRequests: Int = 0
    override var lastUserLocation: LatLng? = null

    private var database: Database<PointOfInterest>? = null

    val mPointsOfInterest: MutableLiveData<Map<String, PointOfInterest>> =
        MutableLiveData(HashMap())
    val mPointsOfInterestStatus: MutableLiveData<Map<String, PointOfInterestStatus>> =
        MutableLiveData(HashMap())

    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)

    private var lastUpdatedAzimuth: Double = 500.0 // impossible azimuth for initialisation

    private val mPoiDist: MutableLiveData<Pair<PointOfInterest, Int>> = MutableLiveData()
    private val mLandMarks: MutableLiveData<List<FirebaseVisionCloudLandmark>> = MutableLiveData()

    init {
        val sensorManager = getSensorManager(getApplication())
        registerListener(sensorManager, Sensor.TYPE_ACCELEROMETER)
        registerListener(sensorManager, Sensor.TYPE_MAGNETIC_FIELD)
    }

    private fun registerListener(sensorManager: SensorManager, sensor: Int) {
        sensorManager.registerListener(
            MapSensorEventListener(sensor),
            sensorManager.getDefaultSensor(sensor),
            SENSOR_DELAY_NORMAL
        )
    }

    /**
     * Get the live data object containing the nearest point of interest in the user's field of view
     * and it's distance from the user (in meters).
     */
    fun getPoiDist(): LiveData<Pair<PointOfInterest, Int>> = mPoiDist

    /**
     * Get the live data object containing the list of landmarks detected in a photo taken by the user
     */
    fun getLandmarks(): LiveData<List<FirebaseVisionCloudLandmark>> = mLandMarks

    /**
     * Detect landmarks in the photo received from the camera activity
     */
    fun handleCameraResponse(uri: Uri) {
        LandmarkDetectionService.detectInImage(getApplication(), uri)
            .addOnSuccessListener { mLandMarks.value = it }
            .addOnFailureListener { mLandMarks.value = listOf() }
    }

    private fun updateOrientation() {
        SensorManager.getRotationMatrix(
            floatRotationMatrix,
            null,
            floatGravity,
            floatGeoMagnetic
        )
        SensorManager.getOrientation(floatRotationMatrix, floatOrientation)
    }

    private fun updatePoiDist() {
        // currently no lens poi OR sufficiently different orientation
        if (mPoiDist.value == null || !checkAnglesClose(
                azimuthInDegrees(),
                lastUpdatedAzimuth,
                AZIMUTH_TOLERANCE
            )
        ) {
            val newPoiDist = closestPoiAndDistance(fieldOfViewPOIs())
            if (newPoiDist != mPoiDist.value) {
                lastUpdatedAzimuth = azimuthInDegrees()
                mPoiDist.value = newPoiDist
            }
        }
    }

    private fun azimuthInDegrees(): Double = floatOrientation[0] * 180 / PI

    private fun checkAnglesClose(a: Double, b: Double, tolerance: Double) =
        (a - tolerance / 2).rem(360) <= b && b <= (a + tolerance / 2).rem(360)

    // assumes lastUserLocation not null
    private fun getUserLocation() =
        LatLng(lastUserLocation!!.latitude, lastUserLocation!!.longitude)

    private fun closestPoiAndDistance(pois: List<PointOfInterest>): Pair<PointOfInterest, Int>? {
        if (lastUserLocation == null) return null

        var minDist = REACHABLE_DIST
        var nearestPoi: PointOfInterest? = null
        val userLocation = getUserLocation()
        for (poi in pois) {
            val dist = computeDistanceBetween(userLocation, LatLng(poi.latitude, poi.longitude))
            if (dist < minDist) {
                nearestPoi = poi
                minDist = dist
            }
        }

        return if (nearestPoi == null) null
        else Pair(nearestPoi, minDist.roundToInt())
    }

    private fun fieldOfViewPOIs(): List<PointOfInterest> {
        if (mPointsOfInterest.value == null || lastUserLocation == null) {
            return listOf()
        }

        val pois = ArrayList<PointOfInterest>()

        val userLocation = getUserLocation()
        val azimuth = azimuthInDegrees() + 180

        for (poi in mPointsOfInterest.value!!.values) {
            val poiLocation = LatLng(poi.latitude, poi.longitude)
            val angle = computeHeading(userLocation, poiLocation) + 180
            if (checkAnglesClose(azimuth, angle, FIELD_OF_VIEW)) {
                pois.add(poi)
            }
        }

        return pois
    }

    private inner class MapSensorEventListener(private val sensor: Int) : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (sensor) {
                Sensor.TYPE_ACCELEROMETER -> floatGravity = getEventValues(event)
                Sensor.TYPE_MAGNETIC_FIELD -> floatGeoMagnetic = getEventValues(event)
            }
            updateOrientation()
            updatePoiDist()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

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
            val reachablePois = getReachablePoi(
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

    override fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        super.onSuccessPoiReceived(poiList)
        addPoiList(poiList)
        setReachablePois()
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

    fun setPoiVisited(poi: PointOfInterest) {
        if (mPointsOfInterestStatus.value!![poi.uid] == PointOfInterestStatus.REACHABLE) {
            if (database != null && !database!!.elements.containsKey(poi.uid)) {
                database!!.addElement(poi.uid, poi)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        super.onLocationChanged(location)
        setReachablePois()
    }
}