package com.github.epfl.meili.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.epfl.meili.R
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ForumActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "ForumActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val posts = getPostsFromDatabase()
        showPosts(posts)
    }

    /** Called when the user taps a post */
    fun openPost(view: View) {
        val intent = Intent(this, PostActivity::class.java)
        startActivity(intent) // starts the instance of PostActivity
    }

    /** Called when the user taps the + button */
    fun goToPostCreation(view: View) {
        val intent = Intent(this, NewPostActivity::class.java)
        startActivity(intent) // starts the instance of NewPostActivity
    }

    /** Gets posts from database */
    private fun getPostsFromDatabase(): ArrayList<QueryDocumentSnapshot> {
        // Access Cloud Firestore
        val db = Firebase.firestore

        // List of posts
        val posts = arrayListOf<QueryDocumentSnapshot>()

        // Get the relevant posts
        // TODO: restrict number of posts retrieved
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    posts.add(document)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        return posts
    }

    /** Shows the posts in the forum UI */
    private fun showPosts(posts : ArrayList<QueryDocumentSnapshot>) {
        // TODO: Create UI for each post in the list
    }
}