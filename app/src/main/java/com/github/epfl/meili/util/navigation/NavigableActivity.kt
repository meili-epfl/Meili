package com.github.epfl.meili.util.navigation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.posts.feed.FeedActivity
import com.github.epfl.meili.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class NavigableActivity(
    private val layout: Int,
    private val activityId: Int
) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        val navigation: BottomNavigationView = findViewById(R.id.navigation)!!
        navigation.selectedItemId = activityId

        navigation.setOnNavigationItemSelectedListener {
            startActivity(getNavigationIntent(it.itemId))
            finish()
            overridePendingTransition(0, 0)
            true
        }
    }

    abstract fun getNavigationIntent(id: Int): Intent
}