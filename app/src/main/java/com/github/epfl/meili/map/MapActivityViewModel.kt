package com.github.epfl.meili.map

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.google.maps.android.SphericalUtil.computeHeading
import kotlin.math.PI
import kotlin.math.roundToInt

class MapActivityViewModel(application: Application): PoiMarkerViewModel(application) {
    companion object {
        private const val SENSOR_DELAY = 2000_000 // microseconds
        private const val FIELD_OF_VIEW = 40 // degrees
        private const val LENS_MAX_DISTANCE: Double = 500.0 // meters
    }

    private var sensorManager: SensorManager =
        getSystemService(getApplication<MainApplication>().applicationContext, SensorManager::class.java)!!
    private var accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var magneticField: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var floatGravity = FloatArray(3)
    private var floatGeoMagnetic = FloatArray(3)

    private val floatOrientation = FloatArray(3)
    private val floatRotationMatrix = FloatArray(9)

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            floatGravity = event.values
            updateOrientation()
            // Do not recompute POI as both sensors listeners are called approximately at the same time
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private val magneticFieldListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            floatGeoMagnetic = event.values
            updateOrientation()
            mPOIDist.value = closestPoiAndDistance(fieldOfViewPOIs())
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
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

    private fun azimuth(): Double = floatOrientation[0] * 180 / PI

    // assumes lastUserLocation not null
    private fun getUserLocation() = LatLng(lastUserLocation!!.latitude, lastUserLocation!!.longitude)

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
        val azimuth = azimuth() + 180

        for (poi in mPointsOfInterest.value!!.values) {
            val poiLocation = LatLng(poi.latitude, poi.longitude)
            val angle = computeHeading(userLocation, poiLocation) + 180
            if (
                (azimuth - FIELD_OF_VIEW / 2).rem(360) <= angle &&
                angle <= (azimuth + FIELD_OF_VIEW / 2).rem(360)
            ) {
                pois.add(poi)
            }
        }

        return pois
    }

    private val mPOIDist: MutableLiveData<Pair<PointOfInterest, Int>> = MutableLiveData()

    init {
        sensorManager.registerListener(accelerometerListener, accelerometer, SENSOR_DELAY)
        sensorManager.registerListener(magneticFieldListener, magneticField, SENSOR_DELAY)
    }

    fun getPOIDist(): LiveData<Pair<PointOfInterest, Int>> = mPOIDist
}