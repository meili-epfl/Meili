package com.github.epfl.meili.util

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.forum.ForumActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.messages.ChatLogActivity
import com.github.epfl.meili.models.PointOfInterest
import com.github.epfl.meili.review.ReviewsActivity


open class MenuActivity(private val menuRes: Int): AppCompatActivity() {
    private var showMenu = true
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(menuRes, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(getIntentFromMenuItem(this, item))
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        var item = menu.findItem(R.id.menu_reviews)
        if(item!=null){
            item.isVisible = showMenu
        }
        item = menu.findItem(R.id.menu_forum)
        if(item != null){
            item.isVisible = showMenu
        }

        return super.onPrepareOptionsMenu(menu)
    }
    private fun getIntentFromMenuItem(activity: Activity, item: MenuItem): Intent{
        val launchedActivityClass = when (item.itemId) {
            R.id.menu_reviews -> ReviewsActivity::class.java
            R.id.menu_chat -> ChatLogActivity::class.java
            R.id.menu_forum -> ForumActivity::class.java
            else -> MainActivity::class.java
        }
        return Intent(activity, launchedActivityClass)
                .putExtra(
                    MapActivity.POI_KEY, intent.getParcelableExtra<PointOfInterest>(
                        MapActivity.POI_KEY
                    )
                )
    }
    protected fun setShowMenu(showMenu: Boolean){
        this.showMenu = showMenu
    }
}