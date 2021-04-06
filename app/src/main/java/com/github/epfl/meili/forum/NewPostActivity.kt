package com.github.epfl.meili.forum

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R

class NewPostActivity : AppCompatActivity() {

    // Unique tag to tell where a log message came from
    private val TAG = "NewPostActivity"

    // Views
    private lateinit var titleView: TextView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        // Initialize views
        titleView = findViewById<EditText>(R.id.new_post_title)
        textView = findViewById<EditText>(R.id.new_post_text)
    }

    /** Called when the user taps the Create Post button */
    fun createNewPost(view: View) {
        // Add post to database
        NewPostViewModel.createNewPost(titleView.text.toString(), textView.text.toString())

        // Go back to the forum activity
        val intent = Intent(this, ForumActivity::class.java)
        startActivity(intent) // starts the instance of ForumActivity
    }
}