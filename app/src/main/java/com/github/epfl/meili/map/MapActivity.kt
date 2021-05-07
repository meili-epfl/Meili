package com.github.epfl.meili.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.util.NavigableActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiServiceCached
import com.github.epfl.meili.util.LocationService
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.requestLocationPermission
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


class MapActivity : NavigableActivity(R.layout.activity_map, R.id.map), OnMapReadyCallback {
    companion object {
        private const val DEFAULT_ZOOM = 15
        const val POI_KEY = "POI_KEY"
    }

    private lateinit var azimuthText: TextView

    // API entry points
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    private var location: Location? = null

    // Cluster Manager for PoiMarkers
    private lateinit var clusterManager: ClusterManager<PoiItem>

    private lateinit var clusterRenderer: PoiRenderer

    private lateinit var viewModel: MapActivityViewModel

    private val poiItems: HashMap<String, PoiItem> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MapActivityViewModel(application)

        azimuthText = findViewById(R.id.azimuth)
        listenToAzimuth()

        // Initialize API entry points
        Places.initialize(
            applicationContext,
            getString(R.string.google_maps_key)
        ) // change API key here

        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun listenToAzimuth() {
        viewModel.getAzimuth().observe(this) { azimuth ->
            azimuthText.text = azimuth.toString()
        }
    }

    private fun setUpClusterer() {
        LocationService.listenToLocationChanges(applicationContext, viewModel)
        viewModel.setPoiService(PoiServiceCached())

        val currentUser = Auth.getCurrentUser()
        if (currentUser != null) {
            viewModel.setDatabase(FirestoreDatabase("users-poi-list/${currentUser.uid}/poi-list", PointOfInterest::class.java))
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

            val statuses: Map<String, PoiMarkerViewModel.PointOfInterestStatus> =
                viewModel.mPointsOfInterestStatus.value!!

            if (statuses[it.poi.uid] == PoiMarkerViewModel.PointOfInterestStatus.REACHABLE) {
                viewModel.setPoiVisited(it.poi)
            }

            startActivity(intent)
            true
        }

        viewModel.mPointsOfInterestStatus.observe(this) {
            addItems(it)
        }
    }

    private fun addItems(map: Map<String, PoiMarkerViewModel.PointOfInterestStatus>) {
        val newMap = HashMap<PoiItem, PoiMarkerViewModel.PointOfInterestStatus>()
        for (entry in map.entries) {
            val poiItem = if (poiItems.containsKey(entry.key)) {
                poiItems[entry.key]!!
            } else {
                PoiItem(viewModel.mPointsOfInterest.value?.get(entry.key)!!)
            }
            newMap[poiItem] = entry.value
        }

        clusterRenderer.renderClusterItems(newMap)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        updateMapUI()

        if (isLocationPermissionGranted(this)) {
            getDeviceLocationAndSetCameraPosition()
            setUpClusterer()
            LocationService.listenToLocationChanges(applicationContext, viewModel)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))}
            Configuration.UI_MODE_NIGHT_YES -> {googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))}
        }

        updateMapUI()

        if (isLocationPermissionGranted(this)) {
            getDeviceLocationAndSetCameraPosition()

            setUpClusterer()
        } else {
            requestLocationPermission(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateMapUI() {
        if (isLocationPermissionGranted(this)) {
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
        if (BuildConfig.DEBUG && !isLocationPermissionGranted(this)) {
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