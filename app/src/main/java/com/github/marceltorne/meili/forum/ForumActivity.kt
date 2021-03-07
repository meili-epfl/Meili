package com.github.marceltorne.meili.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.github.marceltorne.meili.R

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