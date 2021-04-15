package com.github.epfl.meili.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import com.github.epfl.meili.MainApplication

/**
 * This service is supposing that permissions have already being granted before using it
 */
class LocationService {
    private var locationManager: LocationManager

    init {
        locationManager = MainApplication.applicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun setLocationManager(locationManager: LocationManager) {
        this.locationManager = locationManager
    }

    @SuppressLint("MissingPermission")
    fun listenToLocationChanges(locationListener: LocationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0F, locationListener)
    }
}