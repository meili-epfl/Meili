package com.github.epfl.meili.poi

import android.content.Intent
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
import androidx.fragment.app.Fragment
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewSkype
import io.github.ponnamkarthik.richlinkpreview.ViewListener


/**
 * Fragment to be displayed inside of PoiActivity and which contains basic info about POI
 */
class PoiInfoFragment(val poi: PointOfInterest) : Fragment() {
    companion object {
        private const val TAG = "PoiInfoFragment"
        private val DEFAULT_SERVICE = { PlacesClientService() }
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
                place.isOpen == null -> ""
                place.isOpen!! -> "OPEN"
                else -> "CLOSED"
            }

            val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
            "${place.address}\n${place.phoneNumber}\n${openText}".also {
                infoTextView.text = it
            }

            Log.d(TAG, place.websiteUri.toString())
            // TODO: fix and always make it https
            if (place.websiteUri != null && !place.websiteUri.toString().isEmpty()) {
                val richLinkView = view.findViewById<RichLinkViewSkype>(R.id.richLinkView)

                richLinkView.setLink(place.websiteUri.toString(), object : ViewListener {
                    override fun onSuccess(status: Boolean) {}
                    override fun onError(e: Exception) {}
                })
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
}
