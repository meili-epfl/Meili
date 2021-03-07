package com.github.epfl.meili

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.epfl.meili.forum.ForumActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /** Called when the user taps the forum button */
    fun openForum(view: View) {
        val intent = Intent(this, ForumActivity::class.java)
        startActivity(intent) // starts the instance of ForumActivity
    }
}