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
    var viewModel = PostViewModel("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Get post id
        val post_id = intent.getStringExtra(EXTRA_POST_ID)

        if (post_id != null) {
            viewModel = PostViewModel(post_id)

            // Create observer that makes a UI for each post in the observed list
            val postObserver = Observer<Post?> { post ->
                createPostUI(post)
            }

            // Observe the post from viewModel
            viewModel.post.observe(this, postObserver)
        } else {
            Log.e(TAG, "Error getting the post ID from the forum")
        }

    }

    // Show post in post UI
    private fun createPostUI(post: Post?) {
        if (post != null) {
            // Add post information to the predefined templates
            findViewById<TextView>(R.id.post_author).text = post.author
            findViewById<TextView>(R.id.post_title).text = post.title
            findViewById<TextView>(R.id.post_text).text = post.text
        } else {
            Log.e(TAG, "Error showing post")
        }

    }

}