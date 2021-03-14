package com.github.epfl.meili.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.github.epfl.meili.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewPostActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "NewPostActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)
    }

    /** Called when the user taps the Create Post button */
    fun createNewPost(view: View) {
        // Get post information to store in database
        val user = "user1"
        val title = findViewById<EditText>(R.id.new_post_title).text.toString()
        val text = findViewById<EditText>(R.id.new_post_text).text.toString()

        // add the post to the database
        addNewPostToDatabase(user, title, text)

        // Go back to the forum activity
        val intent = Intent(this, ForumActivity::class.java)
        startActivity(intent) // starts the instance of ForumActivity
    }

    /** Adds a new post to the database, based on the entries in the activity */
    private fun addNewPostToDatabase(user: String, title: String, text: String) {
        // Access Cloud Firestore
        val db = Firebase.firestore

        // Create a post instance that will go in the database
        val post = hashMapOf(
            "username" to user,
            "title" to title,
            "text" to text
        )

        // Add a new document with a generated ID
        db.collection("posts").add(post)
    }
}