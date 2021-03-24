package com.github.epfl.meili.poi

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse

class PoiInfoActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PoiInfoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi_info)

        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)

        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        // Define a Place ID.
        val placeId = poi?.placeId

        // Specify the fields to return.
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
        )

        // Construct a request object, passing the place ID and fields array.
        val request = FetchPlaceRequest.newInstance(placeId!!, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val infoTextView = findViewById<TextView>(R.id.infoTextView)
                infoTextView.text =
                    place.name + "\n" + place.address + "\n" + place.phoneNumber + "\n" + place.websiteUri
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: ${exception.message}")
                    val statusCode = exception.statusCode
                    TODO("Handle error with given status code")
                }
            }


    }
}