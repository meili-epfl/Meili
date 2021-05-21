package com.github.epfl.meili

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.home.GoogleSignInActivity
import com.github.epfl.meili.map.MapActivity
import com.github.epfl.meili.util.UserPreferences

class LogoActivity : AppCompatActivity() {
    companion object {
        private const val LOGO_DISPLAY_TIME = 1000L // milliseconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        supportActionBar?.hide()

        val preferences = UserPreferences(this)
        preferences.applyMode()

        val firstActivityClass: Class<out AppCompatActivity> = if (preferences.firstUse) {
            GoogleSignInActivity::class.java
        } else {
            MapActivity::class.java
        }

        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, firstActivityClass))
            finish()
        }, LOGO_DISPLAY_TIME)
    }
}