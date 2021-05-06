package com.github.epfl.meili.util

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.feed.FeedActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class NavigableActivity(private val layout: Int, private val activityId: Int) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)

        val navigation: BottomNavigationView = findViewById(R.id.navigation)!!
        navigation.selectedItemId = activityId

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.map -> startActivity(Intent(this, MapActivity::class.java))
                R.id.profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.feed -> startActivity(Intent(this, FeedActivity::class.java))
            }
            overridePendingTransition(0, 0)
            true
        }
    }
}