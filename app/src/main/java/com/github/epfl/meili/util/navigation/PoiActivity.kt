package com.github.epfl.meili.util.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.poi.PoiInfoActivity
import com.github.epfl.meili.posts.forum.ForumActivity
import com.github.epfl.meili.review.ReviewsActivity

abstract class PoiActivity(layout: Int, activityId: Int) : NavigableActivity(layout, activityId) {
    override fun getNavigationIntent(id: Int): Intent {
        val activityClass: Class<out AppCompatActivity> = when (id) {
            R.id.poi_info_activity -> PoiInfoActivity::class.java
            R.id.forum_activity -> ForumActivity::class.java
            R.id.chat_activity -> ChatActivity::class.java
            R.id.reviews_activity -> ReviewsActivity::class.java
            else -> PoiInfoActivity::class.java
        }

        return Intent(this, activityClass).putExtra(
            MapActivity.POI_KEY,
            intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)
        ).putExtra(
            MapActivity.POI_STATUS_KEY,
            intent.getSerializableExtra(MapActivity.POI_STATUS_KEY)
        )
    }
}