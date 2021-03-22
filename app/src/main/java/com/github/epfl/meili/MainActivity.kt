package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.home.GoogleSignInActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.photo.CameraActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.launchSignInView -> {
                Intent(this, GoogleSignInActivity::class.java)
            }
            R.id.launchChatView -> {
                Intent(this, ChatLogActivity::class.java)
            }
            R.id.launchMapView -> {
                Intent(this, MapActivity::class.java)
            }
            R.id.launchCameraView -> {
                Intent(this, CameraActivity::class.java)
            }
            else -> {
                Intent(this, MainActivity::class.java)
            }
        }

        if (view.id == R.id.launchChatView) {
            intent.putExtra(
                "POI_KEY",
                PointOfInterest(LatLng(100.0, 100.0), "tour-eiffel1", "tour-eiffel2")
            )
        }
        startActivity(intent)
    }
}