package com.github.epfl.meili.forum

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.storage.FirebaseStorageService
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PostActivity"
        private val DEFAULT_URI = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Forum_romanum_6k_%285760x2097%29.jpg/2880px-Forum_romanum_6k_%285760x2097%29.jpg")

        var picasso: () -> Picasso = { Picasso.get() }
    }

    private lateinit var imageView: ImageView
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra("Post")!!
        postId = intent.getStringExtra("PostId")!!

        val authorView: TextView = findViewById(R.id.post_author)
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)
        imageView = findViewById(R.id.post_image)

        authorView.text = post.author
        titleView.text = post.title
        textView.text = post.text

        FirebaseStorageService.getDownloadUrl(
                "forum/$postId",
                { uri -> getDownloadUrlCallback(uri)},
                { exception ->
                    Log.e(TAG,"Image not found", exception)
                    getDownloadUrlCallback(DEFAULT_URI)
                }
        )
    }

    private fun getDownloadUrlCallback(uri: Uri) {
        picasso().load(uri).into(imageView)
    }
}