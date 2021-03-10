package com.github.epfl.meili.forum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.github.epfl.meili.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// key to retrieve the post IDs
const val EXTRA_POST_ID = "com.github.epfl.meili.forum.POST_ID"

class PostActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "PostActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Get post id
        val post_id = intent.getStringExtra(EXTRA_POST_ID)

        // Get post from database and show it
        getPostFromDatabase(post_id)
    }

    // Get the post from the database using the ID passed on Activity creation & show it
    private fun getPostFromDatabase(post_id: String?) {
        // Check for null post_id
        if (post_id != null) {
            // Access Cloud Firestore
            val db = Firebase.firestore

            // Get the relevant post
            db.collection("posts")
                    .document(post_id)  // Get post from id
                    .get()
                    .addOnSuccessListener { result -> // If success
                        // Show post in UI
                        createPostUI(result)

                        Log.d(TAG, "${result.id} => ${result.data}")
                    }
                    .addOnFailureListener { exception -> // If failure
                        Log.w(TAG, "Error getting document.", exception)
                    }
        }
    }

    // Show post in post UI
    private fun createPostUI(post: DocumentSnapshot) {
        // Get post information
        val username = post.data?.get("username").toString()
        val title = post.data?.get("title").toString()
        val text = post.data?.get("text").toString()

        // Add post information to the predefined templates
        findViewById<TextView>(R.id.post_author).text = username
        findViewById<TextView>(R.id.post_title).text = title
        findViewById<TextView>(R.id.post_text).text = text
    }

}