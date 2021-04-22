package com.github.epfl.meili.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.github.epfl.meili.MainActivity
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

    fun onOptionsItemSelectedHelper(activity: Activity, item: MenuItem, intent: Intent){
        // get the POI
        val poi = intent.getParcelableExtra<PointOfInterest>(MapActivity.POI_KEY)!!

        //Now that the buttons are added at the top control what each menu buttons does
        val initializedIntent: Intent = getIntentFromMenuItem(activity, item, poi)

        activity.startActivity(initializedIntent)
    }

    private fun getIntentFromMenuItem(activity: Activity, item: MenuItem, poi: PointOfInterest): Intent{

        val launchedActivity: AppCompatActivity = when (item?.itemId) {
            R.id.menu_reviews -> {
                ReviewsActivity()
            }
            R.id.menu_chat -> {
                ChatLogActivity()
            }
            R.id.menu_forum -> {
                ForumActivity()
            }
            else -> {
                MainActivity()
            }
        }
        return Intent(activity, launchedActivity::class.java).putExtra(MapActivity.POI_KEY, poi)

    }
}