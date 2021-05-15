package com.github.epfl.meili.poi

import android.content.Context
import com.google.android.libraries.places.api.net.PlacesClient

interface PlacesService {
    fun getPlacesClient(context: Context, key: String?): PlacesClient
}