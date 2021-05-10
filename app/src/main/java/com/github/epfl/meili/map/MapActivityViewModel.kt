package com.github.epfl.meili.map

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.util.LandmarkDetectionService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.google.maps.android.SphericalUtil.computeHeading
import kotlin.math.PI
import kotlin.math.roundToInt

class MapActivityViewModel(application: Application) : PoiMarkerViewModel(application) {
    companion object {
        private const val SENSOR_DELAY = 500_000 // microseconds
        private const val FIELD_OF_VIEW = 40.0 // degrees
        private const val LENS_MAX_DISTANCE = 500.0 // meters
        private const val AZIMUTH_TOLERANCE = 5.0 // degrees

        var getSensorManager: (application: Application) -> SensorManager =
            { getSystemService(it, SensorManager::class.java)!! }

        var getEventValues: (event: SensorEvent) -> FloatArray = { it.values }
    }

    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)

    private var lastAzimuth: Double = 500.0 // impossible azimuth for initialisation

    private val mPOIDist: MutableLiveData<Pair<PointOfInterest, Int>> = MutableLiveData()
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

    fun getPOIDist(): LiveData<Pair<PointOfInterest, Int>> = mPOIDist
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

    private fun updatePOIDist() {
        if (!checkAnglesClose(azimuthInDegrees(), lastAzimuth, AZIMUTH_TOLERANCE)) {
            mPOIDist.value = closestPoiAndDistance(fieldOfViewPOIs())
        }

        lastAzimuth = azimuthInDegrees()
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
            updatePOIDist()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}