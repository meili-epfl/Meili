package com.github.epfl.meili.forum

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.espresso.idling.CountingIdlingResource
import com.github.epfl.meili.R
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Global Idling resource for UI tests
val ForumCountingIdlingResource = CountingIdlingResource("forum")

class ForumActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "ForumActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        getPostsFromDatabase()
    }

    /** Called when the user taps a post */
    fun openPost(view: View, post_id: String) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra(EXTRA_POST_ID, post_id) // pass ID to PostActivity so it knows which one to fetch
        }
        startActivity(intent) // starts the instance of PostActivity
    }

    /** Called when the user taps the + button */
    fun goToPostCreation(view: View) {
        val intent = Intent(this, NewPostActivity::class.java)
        startActivity(intent) // starts the instance of NewPostActivity
    }

    /** Gets posts from database and shows them asynchronously*/
    private fun getPostsFromDatabase() {
        // Make UI test wait until task is finished
        ForumCountingIdlingResource.increment()

        // Access Cloud Firestore
        val db = Firebase.firestore

        // Get the relevant posts
        // TODO: restrict number of posts retrieved
        db.collection("posts")
            .get() // Get all posts
            .addOnSuccessListener { result -> // If success
                for (document in result) {
                    // Show post in UI
                    createForumUI(document)

                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception -> // If failure
                Log.w(TAG, "Error getting documents.", exception)
            }

        // Task is finished, UI test can now proceed
        ForumCountingIdlingResource.decrement()
    }

    /** Creates the post UI and display in the forum */
    private fun createForumUI(post: QueryDocumentSnapshot) {
        // Create and get box to put information into
        val box = addPostBoxToForumUI(post.id) // pass id for when post is clicked

        // Add elements that go in the linearLayout
        addAuthorToPostUI(post.data.get("username").toString(), box)
        addTitleToPostUI(post.data.get("title").toString(), box)
    }

    /** Creates a box for the post's information to be stored in */
    private fun addPostBoxToForumUI(post_id: String) : LinearLayout {
        // Create vertical linear layout (box)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.id = View.generateViewId() // Generate id

        // Set layout parameters
        val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // layout_width
                LinearLayout.LayoutParams.WRAP_CONTENT // layout_height
        )
        param.setMargins(0, 10, 0, 0) // layout_margin
        box.layoutParams = param

        // Set onClick behaviour
        box.setOnClickListener(View.OnClickListener {
            // function to call when clicked
            view -> openPost(view, post_id)  // pass id to know what post to fetch
        })

        // Set other aesthetic parameters
        // TODO: make pretty
        box.setBackgroundColor(Color.parseColor("#90e0ef")) // background color

        // Add the box to the parent linearLayout
        findViewById<LinearLayout>(R.id.forum_layout).addView(box)

        return box
    }

    /** Adds the author to the post UI */
    private fun addAuthorToPostUI(author: String, linearLayout: LinearLayout) {
        // Create TextView
        val textView = TextView(this)
        textView.text = author
        textView.id = View.generateViewId() // Generate id

        // Set layout parameters
        val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // layout_width
                LinearLayout.LayoutParams.WRAP_CONTENT // layout_height
        )
        param.setMargins(20, 20, 0, 10) // layout_margin
        textView.layoutParams = param

        //TODO: make pretty
        textView.textSize = 14.0f

        linearLayout.addView(textView)
    }

    /** Adds the title to the post UI */
    private fun addTitleToPostUI(title: String, linearLayout: LinearLayout) {
        // Create TextView
        val textView = TextView(this)
        textView.text = title
        textView.id = View.generateViewId() // Generate id

        // Set layout parameters
        val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // layout_width
                LinearLayout.LayoutParams.WRAP_CONTENT // layout_height
        )
        param.setMargins(20, 0, 20, 20) // layout_margin
        textView.layoutParams = param

        //TODO: make pretty
        textView.textSize = 18.0f

        linearLayout.addView(textView)
    }

}