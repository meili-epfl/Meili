package com.github.epfl.meili.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.BuildConfig
import com.github.epfl.meili.R
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.photo.CameraActivity
import com.github.epfl.meili.poi.PoiActivity
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

    private lateinit var lensPoiNameText: TextView
    private lateinit var lensPoiDistText: TextView
    private lateinit var lensInfoContainer: View

    private lateinit var lensDismissLandmark: ImageView
    private lateinit var lensDetectedLandmark: TextView
    private lateinit var lensDetectedLandmarkContainer: View
    private lateinit var lensCamera: ImageView

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

        viewModel = ViewModelProvider(this).get(MapActivityViewModel::class.java)

        initLensViews()
        setupLandmarkDetection()
        setupLensCamera()

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize map
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun initLensViews() {
        lensPoiNameText = findViewById(R.id.lens_poi_name)
        lensPoiDistText = findViewById(R.id.lens_poi_distance)
        lensInfoContainer = findViewById(R.id.lens_info_container)

        lensDismissLandmark = findViewById(R.id.lens_dismiss_landmark)
        lensDetectedLandmark = findViewById(R.id.lens_detected_landmark)
        lensDetectedLandmarkContainer = findViewById(R.id.lens_detected_landmark_container)
        lensCamera = findViewById(R.id.lens_camera)
    }

    private fun setupLandmarkDetection() {
        viewModel.getPoiDist().observe(this) { poiDist ->
            if (poiDist != null) {
                lensPoiNameText.text = poiDist.first.name
                lensPoiDistText.text =
                    String.format(getString(R.string.lens_poi_distance), poiDist.second)
            } else {
                lensPoiNameText.text = getString(R.string.no_poi_found)
                lensPoiDistText.text = ""
            }
        }
    }

    private val launchCameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK && result.data != null && result.data!!.data != null) {
                viewModel.handleCameraResponse(result.data!!.data!!)
            }
        }

    private fun setupLensCamera() {
        lensCamera.setOnClickListener {
            launchCameraActivity.launch(
                Intent(this, CameraActivity::class.java)
                    .putExtra(CameraActivity.EDIT_PHOTO, false)
            )
        }

        viewModel.getLandmarks().observe(this) { landmarks ->
            if (landmarks.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.no_landmark_detected),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                lensDetectedLandmark.text = landmarks[0].landmark
                toggleLens()
            }
        }

        lensDismissLandmark.setOnClickListener { toggleLens() }
    }

    private fun toggleLens() {
        lensCamera.isVisible = !lensCamera.isVisible
        lensInfoContainer.isVisible = !lensInfoContainer.isVisible
        lensDetectedLandmarkContainer.isVisible = !lensDetectedLandmarkContainer.isVisible
        lensDismissLandmark.isVisible = !lensDismissLandmark.isVisible
    }

    private fun setUpClusterer() {
        LocationService.listenToLocationChanges(applicationContext, viewModel)
        viewModel.setPoiService(PoiServiceCached())

        val currentUser = Auth.getCurrentUser()
        if (currentUser != null) {
            viewModel.setDatabase(
                FirestoreDatabase(
                    "users-poi-list/${currentUser.uid}/poi-list",
                    PointOfInterest::class.java
                )
            )
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
            onPoiItemClicked(it)
        }

        viewModel.mPointsOfInterestStatus.observe(this) {
            addItems(it)
        }
    }

    private fun onPoiItemClicked(poiItem: PoiItem): Boolean{
        val intent = Intent(this, PoiActivity::class.java)
        intent.putExtra(POI_KEY, poiItem.poi)

        val statuses: Map<String, PointOfInterestStatus> =
                viewModel.mPointsOfInterestStatus.value!!

        if (statuses[poiItem.poi.uid] == PointOfInterestStatus.REACHABLE) {
            viewModel.setPoiVisited(poiItem.poi)
        }

        startActivity(intent)
        return true
    }

    private fun addItems(map: Map<String, PointOfInterestStatus>) {
        val newMap = HashMap<PoiItem, PointOfInterestStatus>()
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
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED ->
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            Configuration.UI_MODE_NIGHT_YES ->
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark))
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