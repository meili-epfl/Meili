package com.github.epfl.meili

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPoiClickListener {
    companion object {
        private const val CAMERA_POSITION_KEY = "camera_position"
        private const val LOCATION_KEY = "location"
        private val TAG = MapActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
    }

    // API entry points
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermission = false

    private lateinit var map: GoogleMap
    private var cameraPosition: CameraPosition? = null

    private val defaultLocation = LatLng(48.864716, 2.349014) // Paris
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize API entry points
        Places.initialize(
            applicationContext,
            "AIzaSyCPSWNqpE5t5brgj7VjB3oeqtcte8_mAfI"
        ) // change API key here
        this.placesClient = Places.createClient(this)
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Retrieve previously saved state
        if (savedInstanceState != null) {
            this.cameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION_KEY)
            this.location = savedInstanceState.getParcelable(LOCATION_KEY)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        this.locationPermission = false

        // Check whether permission was granted
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        )
            this.locationPermission = true

        updateMapUI()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style
            )
        )

        updateMapUI()
        getDeviceLocation()

        this.map.setOnPoiClickListener(this)
    }

    override fun onPoiClick(poi: PointOfInterest) {
        Toast.makeText(
            this, poi.name, Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map.let { map ->
            outState.putParcelable(CAMERA_POSITION_KEY, map.cameraPosition)
            outState.putParcelable(LOCATION_KEY, this.location)
        }
        super.onSaveInstanceState(outState)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            this.locationPermission = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun updateMapUI() {
        try {
            if (this.locationPermission) {
                this.map.isMyLocationEnabled = true
                this.map.uiSettings?.isMyLocationButtonEnabled = true
                // Need to figure out how to center camera on location when app is opened instead of needing user to press on location button
            } else {
                this.map.isMyLocationEnabled = false
                this.map.uiSettings?.isMyLocationButtonEnabled = false
                this.location = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (this.locationPermission) {
                val locationResult = this.fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        this.location = task.result
                        if (this.location != null) {
                            this.map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        this.location!!.latitude,
                                        this.location!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        this.map.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        this.map.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}