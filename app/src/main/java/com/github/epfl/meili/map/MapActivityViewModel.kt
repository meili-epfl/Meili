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
import kotlin.math.PI

class MapActivityViewModel(application: Application): PoiMarkerViewModel(application) {
    companion object {
        private const val SENSOR_DELAY = 500_000 // microseconds
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
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    private val magneticFieldListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            floatGeoMagnetic = event.values
            updateOrientation()
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
        mAzimuth.value = azimuth()
    }

    private fun azimuth() = floatOrientation[0] * 180 / PI

    private val mAzimuth: MutableLiveData<Double> = MutableLiveData()

    init {
        sensorManager.registerListener(accelerometerListener, accelerometer, SENSOR_DELAY)
        sensorManager.registerListener(magneticFieldListener, magneticField, SENSOR_DELAY)
    }

    fun getAzimuth(): LiveData<Double> = mAzimuth
}