package com.github.epfl.meili.map

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.database.Database
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.util.LandmarkDetectionService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.google.maps.android.SphericalUtil.computeHeading
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.PI
import kotlin.math.roundToInt

class MapActivityViewModel(application: Application) :
    AndroidViewModel(application), Observer, LocationListener
{
    companion object {
        private const val SENSOR_DELAY = 500_000 // microseconds
        private const val FIELD_OF_VIEW = 25.0 // degrees
        private const val LENS_MAX_DISTANCE = 500.0 // meters
        private const val AZIMUTH_TOLERANCE = 15.0 // degrees

        private const val REACHABLE_DIST = 50.0 //meters
        private const val MAX_NUM_REQUESTS = 2

        var getSensorManager: (application: Application) -> SensorManager =
            { getSystemService(it, SensorManager::class.java)!! }

        var getEventValues: (event: SensorEvent) -> FloatArray = { it.values }
    }

    private var database: Database<PointOfInterest>? = null
    private var poiService: PoiService? = null
    private var lastUserLocation: LatLng? = null

    private var nbCurrentRequests = 0

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
            SENSOR_DELAY
        )
    }

    fun getPoiDist(): LiveData<Pair<PointOfInterest, Int>> = mPoiDist
    fun getLandmarks(): LiveData<List<FirebaseVisionCloudLandmark>> = mLandMarks

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
        Log.e(azimuthInDegrees().toString(), lastUpdatedAzimuth.toString())
        if (!checkAnglesClose(azimuthInDegrees(), lastUpdatedAzimuth, AZIMUTH_TOLERANCE)) {
            mPoiDist.value = closestPoiAndDistance(fieldOfViewPOIs())
            lastUpdatedAzimuth = azimuthInDegrees()
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

        var minDist = LENS_MAX_DISTANCE
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
        nbCurrentRequests += 1

        if (nbCurrentRequests >= MAX_NUM_REQUESTS) {
            Toast.makeText(MainApplication.applicationContext(), "An error occured while fetching POIs", Toast.LENGTH_LONG).show()
        } else {
            requestPois()
        }
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
                requestPois()
            }
        }

        setReachablePois()
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}