package com.github.epfl.meili.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.poi.PoiService
import com.github.epfl.meili.poi.PointOfInterest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.clustering.ClusterManager


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val REQUEST_CODE: Int = 1
        const val POI_KEY = "POI_KEY"
    }

    // API entry points
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    private var location: Location? = null

    // Cluster Manager for PoiMarkers
    private lateinit var clusterManager: ClusterManager<PoiItem>

    private lateinit var clusterRenderer: PoiRenderer

    private val poiMarkerViewModel = PoiMarkerViewModel()

    private val poiItems: HashMap<String, PoiItem> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize API entry points
        Places.initialize(
            applicationContext,
            getString(R.string.google_maps_key)
        ) // change API key here

        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setUpClusterer() {
        if (isPermissionGranted()) {
            val locationService = LocationService()
            locationService.listenToLocationChanges(poiMarkerViewModel)
        }

        poiMarkerViewModel.setPoiService(PoiService())

        val currentUser = Auth.getCurrentUser()
        if (currentUser != null) {
            poiMarkerViewModel.setDatabase(FirestoreDatabase("users-poi-list/${currentUser.uid}/poi-list", PointOfInterest::class.java))
        }

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(this, map)

        clusterRenderer = PoiRenderer(this, map, clusterManager)

        clusterManager.renderer = clusterRenderer

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // Add on click listener
        clusterManager.setOnClusterItemClickListener {
            val intent = Intent(this, ForumActivity::class.java)
            intent.putExtra(POI_KEY, it.poi)

            if (poiMarkerViewModel.mPointsOfInterestStatus.value?.get(it.poi) == PoiMarkerViewModel.PointOfInterestStatus.REACHABLE) {
                poiMarkerViewModel.setPoiVisited(it.poi)
            }

            startActivity(intent)
            true
        }

        poiMarkerViewModel.mPointsOfInterestStatus.observe(this) {
            addItems(it)
        }
    }

    private fun addItems(map: Map<String, PoiMarkerViewModel.PointOfInterestStatus>) {
        val newMap = HashMap<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>()
        for (entry in map.entries) {
            val poiItem: PoiItem
            if (poiItems.containsKey(entry.key)) {
                poiItem = poiItems[entry.key]!!
            } else {
                poiItem = PoiItem(poiMarkerViewModel.mPointsOfInterest.value?.get(entry.key)!!)
            }
            newMap.put(poiItem, entry.value)
        }

        clusterRenderer.renderClusterItems(newMap)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updateMapUI()

        if (isPermissionGranted()) {
            val locationService = LocationService()
            locationService.listenToLocationChanges(poiMarkerViewModel)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        updateMapUI()

        if (isPermissionGranted()) {
            getDeviceLocationAndSetCameraPosition()
        } else {
            getLocationPermission()
        }

        setUpClusterer()
    }

    private fun getLocationPermission() {
        if (BuildConfig.DEBUG && isPermissionGranted()) {
            error("Assertion failed")
        }
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
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocationAndSetCameraPosition() {
        if (BuildConfig.DEBUG && !isPermissionGranted()) {
            error("Assertion failed")
        }
        this.fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                location = task.result
                map.moveCamera(
                    newLatLngZoom(
                        LatLng(location!!.latitude, location!!.longitude),
                        DEFAULT_ZOOM.toFloat()
                    )
                )
            }
        }
    }
}