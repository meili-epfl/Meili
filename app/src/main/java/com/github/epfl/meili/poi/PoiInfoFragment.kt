package com.github.epfl.meili.poi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.epfl.meili.MainApplication
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype
import io.github.ponnamkarthik.richlinkpreview.ViewListener

/**
 * Fragment to be displayed inside of PoiActivity and which contains basic info about POI
 */
class PoiInfoFragment(val poi: PointOfInterest) : Fragment() { //TODO verify that everything working properly when better connection
    companion object {
        private const val TAG = "PoiInfoFragment"
        private val DEFAULT_SERVICE = { PlacesClientService() }
        private const val REQUEST_CODE = 1000
        var placesClientService: () -> PlacesClientService = DEFAULT_SERVICE
    }

    private lateinit var takeMeThereButton: Button

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poi_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Places API entry point
        val placesClient =
                placesClientService().getPlacesClient(activity, getString(R.string.google_maps_key))

        val placeId = poi.uid

        // Places API query fields
        val placeFields = listOf(
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.UTC_OFFSET,
                Place.Field.OPENING_HOURS,
                Place.Field.PHOTO_METADATAS
        )

        takeMeThereButton = view.findViewById(R.id.take_me_there_button)
        takeMeThereButton.visibility = GONE

        view.findViewById<TextView>(R.id.poi_name).text = poi.name

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
                .addOnSuccessListener(getOnSuccessListener(view, placesClient)).addOnFailureListener {
                    val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
                    infoTextView.text = "No information found for this point of interest :("
                }
    }

    private fun getOnSuccessListener(
            view: View,
            placesClient: PlacesClient
    ): (FetchPlaceResponse) -> Unit =
            { response: FetchPlaceResponse ->
                val place = response.place

                val openText = when {
                    place.isOpen == null -> "No Info"
                    place.isOpen!! -> "OPEN"
                    else -> "CLOSED"
                }

                val openStatusInfo = view.findViewById<TextView>(R.id.openStatusTextView)
                openStatusInfo.text = openText

                val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
                "${place.address}\n".also {
                    infoTextView.text = it
                }

                if (place.websiteUri != null && place.websiteUri.toString().isNotEmpty()) {
                    val richLinkView = view.findViewById<RichLinkViewSkype>(R.id.richLinkView)

                    richLinkView.setLink(urlToHttps(place.websiteUri.toString()), object : ViewListener {
                        override fun onSuccess(status: Boolean) {}
                        override fun onError(e: Exception) {}
                    })
                }

                val callButton = view.findViewById<FloatingActionButton>(R.id.call_poi_button)
                callButton.setOnClickListener {
                    launchCallIntent(place.phoneNumber)
                }

                takeMeThereButton.visibility = VISIBLE

                takeMeThereButton.setOnClickListener {
                    val gmmIntentUri =
                            Uri.parse("google.navigation:q=${place.address}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }


                val poiImageView = view.findViewById<ImageView>(R.id.poiImageView)

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
            if (ContextCompat.checkSelfPermission(MainApplication.applicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CODE)
            } else {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:${phoneNumber}")
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
            Log.d(TAG, index.toString())
            val res = "https${url.subSequence(index, url.length)}"
            Log.d(TAG, res)
            return res
        }
        return url
    }
}
