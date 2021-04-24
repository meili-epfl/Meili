package com.github.epfl.meili.poi

import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

open class PlacesClientService : PlacesService {

    override fun getPlacesClient(activity: FragmentActivity?, key: String?): PlacesClient {
        Places.initialize(activity?.applicationContext!!, key!!)
        return Places.createClient(activity?.applicationContext!!)
    }
}