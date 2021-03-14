package com.github.epfl.meili

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private val TAG = MapActivity::class.java.name
        private val DEFAULT_ZOOM = 15
        private val REQUEST_CODE: Int = 1
    }

    // API entry points
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    private val defaultLocation = LatLng(48.864716, 2.349014) // Paris
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize API entry points
        Places.initialize(
            applicationContext,
            getString(R.string.google_maps_key)
        ) // change API key here

        this.placesClient = Places.createClient(this)
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
//        // Check whether permission was granted
//        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() &&
//            grantResults[0] == PackageManager.PERMISSION_GRANTED
//        )
        updateMapUI()
        getDeviceLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        val mapStyle = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style
            )
        )
        if (!isPermissionGranted()) {
            getLocationPermission()
        }

        updateMapUI()
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        assert(!isPermissionGranted())
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun updateMapUI() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            this.map.uiSettings?.isMyLocationButtonEnabled = true
        } else {
            map.isMyLocationEnabled = false
            map.uiSettings?.isMyLocationButtonEnabled = false
            location = null
            Toast.makeText(this, "Cannot show location without permission", Toast.LENGTH_LONG)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        var unableToSetCameraToLocation: Boolean = true
        if (isPermissionGranted()) {
            this.fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    location = task.result
                    if (location != null) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location!!.latitude,
                                    location!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                        unableToSetCameraToLocation = false
                    }
                }
            }
        }
        if (unableToSetCameraToLocation) {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    defaultLocation,
                    DEFAULT_ZOOM.toFloat()
                )
            )
        }
    }
}