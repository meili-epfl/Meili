package com.github.epfl.meili.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.review.ReviewsActivity

object MenuInflaterHelper {
    fun onCreateOptionsMenuHelper(activity: Activity, ctx: Int, menu: Menu?){
        activity.menuInflater.inflate(ctx, menu)
    }

    fun onOptionsItemSelectedHelpoer(acitvity: Activity,item: MenuItem, intent: Intent){
        // get the POI
        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!

        //Now that the buttons are added at the top control what each menu buttons does
        val initializedIntent: Intent = when (item?.itemId) {
            R.id.menu_reviews_from_forum -> {
                Intent(acitvity, ReviewsActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_chat_from_forum-> {
                Intent(acitvity, ChatLogActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_reviews_from_chat -> {
                Intent(acitvity, ReviewsActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_forum_from_chat-> {
                Intent(acitvity, ForumActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_chat_from_review -> {
                Intent(acitvity, ChatLogActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            R.id.menu_forum_from_review-> {
                Intent(acitvity, ForumActivity::class.java)
                    .putExtra(MapActivity.POI_KEY, poi)
            }
            else -> {
                Intent(acitvity, ForumActivity::class.java)
            }
        }
        //clear the older intents so that the back button works correctly
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        acitvity.startActivity(initializedIntent)
    }


}