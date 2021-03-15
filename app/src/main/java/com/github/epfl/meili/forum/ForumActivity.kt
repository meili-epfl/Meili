package com.github.epfl.meili.forum

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.epfl.meili.R
import com.google.firebase.firestore.QueryDocumentSnapshot


class ForumActivity : AppCompatActivity() {

    private val TAG = "ForumActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        val viewModel = ForumViewModel()

        // Create observer that makes a UI for each post in the observed list
        val forumObserver = Observer<List<Post>> { posts ->
            for (post in posts) {
                addPostToForum(post) // Add the post to the forum UI
            }
        }
        // Observe the posts from viewModel
        viewModel.posts.observe(this, forumObserver)
    }

    /** Called when the user taps a post */
    fun openPost(view: View, post_id: String) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra(
                EXTRA_POST_ID,
                post_id
            ) // pass ID to PostActivity so it knows which one to fetch
        }
        startActivity(intent) // starts the instance of PostActivity
    }

    /** Called when the user taps the + button */
    fun goToPostCreation(view: View) {
        val intent = Intent(this, NewPostActivity::class.java)
        startActivity(intent) // starts the instance of NewPostActivity
    }

    /** Add a new post to the forum UI */
    private fun addPostToForum(post: Post) {
        val layout = makePostBox(post.id)
        addAuthor(post.author, layout)
        addTitle(post.title, layout)
    }

    /** Makes the clickable box for the post information to go into */
    private fun makePostBox(post_id: String) : LinearLayout {
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
                view ->
            openPost(view, post_id)  // pass id to know what post to fetch
        })

        // Set other aesthetic parameters
        box.setBackgroundColor(Color.parseColor("#90e0ef")) // background color

        // Add the box to the parent linearLayout
        findViewById<LinearLayout>(R.id.forum_layout).addView(box)

        return box

    }

    /** Adds the author to the post UI */
    private fun addAuthor(author: String, linearLayout: LinearLayout) {
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
        textView.textSize = 14.0f

        linearLayout.addView(textView)
    }

    /** Adds the title to the post UI */
    private fun addTitle(title: String, linearLayout: LinearLayout) {
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
        textView.textSize = 18.0f

        linearLayout.addView(textView)
    }

}