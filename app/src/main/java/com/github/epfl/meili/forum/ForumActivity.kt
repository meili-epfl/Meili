package com.github.epfl.meili.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.epfl.meili.R

class ForumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)
    }

    /** Called when the user taps a post */
    fun openPost(view: View) {
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent) // starts the instance of ForumActivity
    }
}