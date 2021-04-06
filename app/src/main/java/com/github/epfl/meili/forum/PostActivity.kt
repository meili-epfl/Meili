package com.github.epfl.meili.forum

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.epfl.meili.R

// key to retrieve the post IDs
const val EXTRA_POST_ID = "com.github.epfl.meili.forum.POST_ID"

class PostActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "PostActivity"

    // Views
    private lateinit var authorView: TextView
    private lateinit var titleView: TextView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Initialize views
        authorView = findViewById<TextView>(R.id.post_author)
        titleView = findViewById<TextView>(R.id.post_title)
        textView = findViewById<TextView>(R.id.post_text)

        // Get post id
        val post_id = intent.getStringExtra(EXTRA_POST_ID)

        if (post_id != null) {
            PostViewModel.setID(post_id) // Set post id in the viewModel

            // Create observer that makes a UI for the current post
            val postObserver = Observer<Post?> { post ->
                createPostUI(post)
            }

            // Observe the post from viewModel
            PostViewModel.post.observe(this, postObserver)
        } else {
            Log.e(TAG, "Error getting the post ID from the forum")
        }

    }

    // Show post in post UI
    private fun createPostUI(post: Post?) {
        if (post != null) {
            // Add post information to the predefined templates
            authorView.text = post.author
            titleView.text = post.title
            textView.text = post.text
        } else {
            Log.e(TAG, "Error showing post")
        }

    }

}