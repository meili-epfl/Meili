package com.github.epfl.meili.poi

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

open class PlacesClientService : PlacesService {

    override fun getPlacesClient(context: Context, key: String?): PlacesClient {
        Places.initialize(context, key!!)
        return Places.createClient(context)
    }
}