package com.github.epfl.meili.forum

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R

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
        val title = findViewById<EditText>(R.id.new_post_title).text.toString()
        val text = findViewById<EditText>(R.id.new_post_text).text.toString()

        // Add post to database
        NewPostViewModel.createNewPost(title, text)

        // Go back to the forum activity
        val intent = Intent(this, ForumActivity::class.java)
        startActivity(intent) // starts the instance of ForumActivity
    }
}