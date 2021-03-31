package com.github.epfl.meili.poi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.epfl.meili.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class TestPoiRequest : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_poi_request)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun requestPois(view: View){

        PoiService().requestPois(LatLng(41.076679, 1.144500),{poi -> Log.d("POI activity", poi.toString())},{error -> Log.d("POI Activity", error.toString())})

    }
}