package com.github.epfl.meili.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiServiceCached
import com.github.epfl.meili.util.LocationService
import com.github.epfl.meili.util.LocationService.isLocationPermissionGranted
import com.github.epfl.meili.util.LocationService.requestLocationPermission
import com.github.epfl.meili.util.NavigableActivity
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

    // API entry points
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    private var location: Location? = null

    private lateinit var clusterManager: ClusterManager<MarkerItem>

    private lateinit var clusterRenderer: MarkerRenderer

    private val poiMarkerViewModel = MarkerViewModel()

    private val markerItems: HashMap<String, MarkerItem> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Places.initialize(
            applicationContext,
            getString(R.string.google_maps_key)
        )

        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setUpClusterer() {
        LocationService.listenToLocationChanges(applicationContext, poiMarkerViewModel)
        poiMarkerViewModel.setPoiService(PoiServiceCached())

        val currentUser = Auth.getCurrentUser()
        if (currentUser != null) {
            poiMarkerViewModel.setDatabase(
                FirestoreDatabase(
                    "users-poi-list/${currentUser.uid}/poi-list",
                    PointOfInterest::class.java
                )
            )
        }

        clusterManager = ClusterManager(this, map)

        clusterRenderer = MarkerRenderer(this, map, clusterManager)

        clusterManager.renderer = clusterRenderer

        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterItemClickListener {
            val intent = Intent(this, ForumActivity::class.java)
            intent.putExtra(POI_KEY, it.poi)

            val statuses: Map<String, MarkerViewModel.PointOfInterestStatus> =
                poiMarkerViewModel.mPointsOfInterestStatus.value!!

            if (statuses[it.poi.uid] == MarkerViewModel.PointOfInterestStatus.REACHABLE) {
                poiMarkerViewModel.setPoiVisited(it.poi)
            }

            startActivity(intent)
            true
        }

        poiMarkerViewModel.mPointsOfInterestStatus.observe(this) {
            addItems(it)
        }
    }

    private fun addItems(map: Map<String, MarkerViewModel.PointOfInterestStatus>) {
        val newMap = HashMap<MarkerItem, MarkerViewModel.PointOfInterestStatus>()
        for (entry in map.entries) {
            val poiItem = if (markerItems.containsKey(entry.key)) {
                markerItems[entry.key]!!
            } else {
                MarkerItem(poiMarkerViewModel.mPointsOfInterest.value?.get(entry.key)!!)
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
            LocationService.listenToLocationChanges(applicationContext, poiMarkerViewModel)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val mode = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        when (mode) {
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.map_style_dark
                    )
                )
            }
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

    private fun getDeviceLocationAndSetCameraPosition() {
        if (BuildConfig.DEBUG && !isLocationPermissionGranted(this)) {
            error("Assertion failed")
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission(this)
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