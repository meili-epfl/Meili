package com.github.epfl.meili.poi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.database.FirestoreDatabase
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.poi.PointOfInterestStatus
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivity
import com.github.epfl.meili.util.MeiliViewModel
import com.github.epfl.meili.util.navigation.PoiActivity
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype
import io.github.ponnamkarthik.richlinkpreview.ViewListener

class PoiInfoActivity : PoiActivity(R.layout.activity_poi_info, R.id.poi_info_activity) {
    companion object {
        private const val REQUEST_CODE = 1000
        private const val NO_INFO_TEXT = "No information found for this point of interest :("
        private const val GOOGLE_MAPS_URL_FOR_INTENT = "google.navigation:q="
        private const val GOOGLE_MAPS_INTENT_PACKAGE = "com.google.android.apps.maps"
        private const val CALLING_URL_FOR_INTENT = "tel:"

        // Places API query fields
        val placeFields = listOf(
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
            Place.Field.UTC_OFFSET,
            Place.Field.OPENING_HOURS,
            Place.Field.PHOTO_METADATAS
        )

        var placesClientService: () -> PlacesClientService = { PlacesClientService() }
    }

    private lateinit var poi: PointOfInterest
    private lateinit var poiStatus: PointOfInterestStatus
    private lateinit var takeMeThereButton: Button
    private lateinit var favoriteButton: ToggleButton
    private lateinit var viewModel: MeiliViewModel<PointOfInterest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        poi = intent.getParcelableExtra(MapActivity.POI_KEY)!!
        poiStatus = intent.getSerializableExtra(MapActivity.POI_STATUS_KEY) as PointOfInterestStatus
        title = poi.name

        initViewModel()

        // Initialize Places API entry point
        val placesClient = placesClientService()
            .getPlacesClient(this, getString(R.string.google_maps_key))

        val placeId = poi.uid

        takeMeThereButton = findViewById(R.id.take_me_there_button)
        takeMeThereButton.visibility = View.GONE

        initFavoriteButton()

        findViewById<TextView>(R.id.poi_name).text = poi.name

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener(getOnSuccessListener(placesClient)).addOnFailureListener {
                val infoTextView = findViewById<TextView>(R.id.infoTextView)
                infoTextView.text = NO_INFO_TEXT
            }
    }

    private fun initFavoriteButton() {
        favoriteButton = findViewById(R.id.favorite_button)
        if (Auth.getCurrentUser() == null) {
            favoriteButton.visibility = View.GONE
        }

        if (viewModel.getElements().value != null && viewModel.getElements().value!!.containsKey(poi.uid))
            favoriteButton.isChecked = true


        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                viewModel.addElement(poi.uid, poi)
            else
                viewModel.removeElement(poi.uid)
        }
    }

    private fun initViewModel() {
        @Suppress("UNCHECKED_CAST")
        viewModel =
            ViewModelProvider(this).get(MeiliViewModel::class.java) as MeiliViewModel<PointOfInterest>

        if (Auth.getCurrentUser() != null) {
            viewModel.initDatabase(
                FirestoreDatabase(
                    String.format(FavoritePoisActivity.DB_PATH, Auth.getCurrentUser()!!.uid),
                    PointOfInterest::class.java
                )
            )
        }
        viewModel.getElements().observe(this, { map ->
            if (map.containsKey(poi.uid))
                favoriteButton.isChecked = true
        })
    }

    private fun getOnSuccessListener(
        placesClient: PlacesClient
    ): (FetchPlaceResponse) -> Unit =
        { response: FetchPlaceResponse ->
            val place = response.place

            val openText = when {
                place.isOpen == null -> "No Info"
                place.isOpen!! -> "OPEN"
                else -> "CLOSED"
            }

            val openStatusInfo = findViewById<TextView>(R.id.openStatusTextView)
            openStatusInfo.text = openText

            val infoTextView = findViewById<TextView>(R.id.infoTextView)
            "${place.address}\n".also {
                infoTextView.text = it
            }

            if (place.websiteUri != null && place.websiteUri.toString().isNotEmpty()) {
                val richLinkView = findViewById<RichLinkViewSkype>(R.id.richLinkView)

                richLinkView.setLink(urlToHttps(place.websiteUri.toString()), object :
                    ViewListener {
                    override fun onSuccess(status: Boolean) {}
                    override fun onError(e: Exception) {}
                })
            }

            val callButton = findViewById<FloatingActionButton>(R.id.call_poi_button)
            callButton.setOnClickListener {
                launchCallIntent(place.phoneNumber)
            }

            takeMeThereButton.visibility = View.VISIBLE

            takeMeThereButton.setOnClickListener {
                val gmmIntentUri =
                    Uri.parse("${GOOGLE_MAPS_URL_FOR_INTENT}${place.address}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage(GOOGLE_MAPS_INTENT_PACKAGE)
                startActivity(mapIntent)
            }


            val poiImageView = findViewById<ImageView>(R.id.poiImageView)

            val metadata = place.photoMetadatas
            if (!(metadata == null || metadata.isEmpty())) {


                val photoMetadata = metadata.first()

                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(1000)
                    .setMaxHeight(600)
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        poiImageView.setImageBitmap(bitmap)
                    }
            }
        }

    private fun launchCallIntent(phoneNumber: String?) {
        if (phoneNumber != null) {
            if (ContextCompat.checkSelfPermission(
                    MainApplication.applicationContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CODE
                )
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("${CALLING_URL_FOR_INTENT}${phoneNumber}")
                startActivity(callIntent)
            }
        }
    }

    private fun urlToHttps(url: String): String {
        if (url.startsWith("https://")) {
            return url
        }

        val index = url.indexOf("://")
        if (index >= 0) {
            return "https${url.subSequence(index, url.length)}"
        }
        return url
    }
}
