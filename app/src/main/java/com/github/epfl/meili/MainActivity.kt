package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.home.GoogleSignInActivity
import com.github.epfl.meili.map.MapActivity

import com.github.epfl.meili.profile.ProfileActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.nearby.NearbyActivity
import com.github.epfl.meili.poi.PoiActivity
import com.github.epfl.meili.profile.PoiHistoryActivity
import com.github.epfl.meili.review.ReviewsActivity


class MainActivity : AppCompatActivity() {

    companion object {
        private val POI = PointOfInterest(10.0, 10.0, "art_brut","ChIJAAAAAAAAAAARg4pb6XR5bo0")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        val intent: Intent = when (view.id) {
            R.id.launchSignInView -> Intent(this, GoogleSignInActivity::class.java)
            R.id.launchChatView -> Intent(this, ChatLogActivity::class.java)
            R.id.launchMapView -> Intent(this, MapActivity::class.java)
            R.id.launchReviewView -> Intent(this, ReviewsActivity::class.java)
            R.id.launchPoiView -> Intent(this, PoiActivity::class.java)
            R.id.launchForumView -> Intent(this, ForumActivity::class.java)
            R.id.launchProfileView -> Intent(this, ProfileActivity::class.java)
            R.id.launchNearby -> Intent(this, NearbyActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }.putExtra("POI_KEY", POI)
        startActivity(intent)
    }
}