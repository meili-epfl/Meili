package com.github.epfl.meili.poi

import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.net.PlacesClient

interface PlacesService {
    fun getPlacesClient(activity: FragmentActivity?, key: String?): PlacesClient
}