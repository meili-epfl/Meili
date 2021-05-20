package com.github.epfl.meili.poi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.epfl.meili.R
import com.github.epfl.meili.models.PointOfInterest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*

/**
 * Fragment to be displayed inside of PoiActivity and which contains basic info about POI
 */
class PoiInfoFragment(val poi: PointOfInterest) : Fragment() {
    companion object {
        private const val PHOTO_WIDTH = 1000
        private const val PHOTO_HEIGHT = 600
        private val DEFAULT_SERVICE = { PlacesClientService() }
        var placesClientService: () -> PlacesClientService = DEFAULT_SERVICE
    }

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
            placesClientService().getPlacesClient(
                activity?.applicationContext!!,
                getString(R.string.google_maps_key)
            )

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

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener(getOnSuccessListener(view, placesClient)).addOnFailureListener {
                val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
                infoTextView.text = getString(R.string.poi_no_info)
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
                place.isOpen!! -> getString(R.string.open)
                else -> getString(R.string.closed)
            }

            val infoTextView = view.findViewById<TextView>(R.id.infoTextView)
            "${place.address}\n${place.phoneNumber}\n${place.websiteUri}\n${openText}".also {
                infoTextView.text = it
            }

            val poiImageView = view.findViewById<ImageView>(R.id.poiImageView)

            val metadata = place.photoMetadatas
            if (!(metadata == null || metadata.isEmpty())) {


                val photoMetadata = metadata.first()

                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(PHOTO_WIDTH)
                    .setMaxHeight(PHOTO_HEIGHT)
                    .build()
                placesClient.fetchPhoto(photoRequest)
                    .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                        val bitmap = fetchPhotoResponse.bitmap
                        poiImageView.setImageBitmap(bitmap)
                    }
            }
        }
}
