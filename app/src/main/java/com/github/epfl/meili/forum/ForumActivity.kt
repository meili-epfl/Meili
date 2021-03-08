package com.github.epfl.meili.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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

        getPostsFromDatabase()
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

    /** Gets posts from database and shows them asynchronously*/
    private fun getPostsFromDatabase() {
        // Access Cloud Firestore
        val db = Firebase.firestore

        // Get the relevant posts
        // TODO: restrict number of posts retrieved
        db.collection("posts")
            .get()
            .addOnSuccessListener { result -> // If success
                for (document in result) {
                    // Show post in UI
                    showPost(document)

                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception -> // If failure
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /** Shows the post in the forum UI */
    private fun showPost(post: QueryDocumentSnapshot) {
        // Create and get linearLayout (box for the information to go in)
        val linearLayout = addLinearLayout()

        // Add elements that go in the linearLayout
        addAuthorToPost(post.data.get("username").toString(), linearLayout)
        addTitleToPost(post.data.get("title").toString(), linearLayout)
    }

    /** Creates a linearLayout for the post's information to be stored in */
    private fun addLinearLayout() : LinearLayout {
        // Create layout
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL

        //TODO: make pretty

        // Add it to the parent linearLayout
        findViewById<LinearLayout>(R.id.forum_layout).addView(linearLayout)

        return linearLayout
    }

    /** Adds the author to the post UI */
    private fun addAuthorToPost(author: String, linearLayout: LinearLayout) {
        // Create TextView
        val textView = TextView(this)
        textView.text = author

        //TODO: make pretty

        linearLayout.addView(textView)
    }

    /** Adds the title to the post UI */
    private fun addTitleToPost(title: String, linearLayout: LinearLayout) {
        // Create TextView
        val textView = TextView(this)
        textView.text = title

        //TODO: make pretty

        linearLayout.addView(textView)
    }

}