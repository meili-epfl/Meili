package com.github.epfl.meili.util

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.widget.Toast
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiService
import com.google.android.gms.maps.model.LatLng

interface PoiServiceViewModel : LocationListener {
    companion object {
        const val MAX_NUM_REQUESTS = 2
    }

    var poiService: PoiService?
    var nbCurrentRequests: Int
    var lastUserLocation: LatLng?

    fun initPoiService(service: PoiService) {
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

    fun onSuccessPoiReceived(poiList: List<PointOfInterest>) {
        nbCurrentRequests = 0
    }

    private fun onError(error: Error): Boolean {
        nbCurrentRequests += 1
        if (nbCurrentRequests < MAX_NUM_REQUESTS) {
            requestPois()
        } else {
            Toast.makeText(
                MainApplication.applicationContext(),
                "An error occured while fetching POIs",
                Toast.LENGTH_LONG
            ).show()
        }

        return nbCurrentRequests >= MAX_NUM_REQUESTS
    }

    fun getReachablePoi(userPosition: LatLng?, poiList: List<PointOfInterest>?, radius: Double?)
        = poiService!!.getReachablePoi(userPosition, poiList, radius)

    override fun onLocationChanged(location: Location) {
        val shouldCallService = lastUserLocation == null
        val newLocation = LatLng(location.latitude, location.longitude)

        if (lastUserLocation != newLocation) {
            lastUserLocation = newLocation
            if (shouldCallService && poiService != null) {
                requestPois()
            }
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}