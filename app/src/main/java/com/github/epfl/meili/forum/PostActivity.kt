package com.github.epfl.meili.forum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.epfl.meili.R

class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Get and set author's name
        findViewById<TextView>(R.id.post_author).apply {
            text = "The author"
        }

        // Get and set title text
        findViewById<TextView>(R.id.post_title).apply {
            text = "The title of the post"
        }

        // Get and set post text
        findViewById<TextView>(R.id.post_text).apply {
            text = "This is the text of the post, I spent way too much time on the UI and I am " +
                    "not very good with Android Studio. Hopefully I will get better soon, otherwise " +
                    "I will pass my semester trying to make the forum work."
        }

    }
}