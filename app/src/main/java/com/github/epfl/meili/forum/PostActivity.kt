package com.github.epfl.meili.forum

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra("Post")!!

        val authorView: TextView = findViewById(R.id.post_author)
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)

        authorView.text = post.author
        titleView.text = post.title
        textView.text = post.text
    }
}